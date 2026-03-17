package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Reservation;
import main.java.com.entity.Voiture;
import main.java.com.entity.Parametre;
import main.java.com.entity.Voyage;
import main.java.com.entity.VoyageStop;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class PlanificationService {

    private final ReservationService reservationService = new ReservationService();
    private final VoitureService voitureService = new VoitureService();
    private final LieuService lieuService = new LieuService();
    private final ParametreService parametreService = new ParametreService();
    private final DistanceService distanceService = new DistanceService();
    private final VoyageService voyageService = new VoyageService();

    public static class PlanificationResult {
        private final LocalDate date;
        private final int reservationsTraitees;
        private final int clientsTraites;
        private final int assignmentsCrees;
        private final List<String> warnings;

        public PlanificationResult(LocalDate date, int reservationsTraitees, int clientsTraites, int assignmentsCrees, List<String> warnings) {
            this.date = date;
            this.reservationsTraitees = reservationsTraitees;
            this.clientsTraites = clientsTraites;
            this.assignmentsCrees = assignmentsCrees;
            this.warnings = warnings;
        }

        public LocalDate getDate() {
            return date;
        }

        public int getReservationsTraitees() {
            return reservationsTraitees;
        }

        public int getClientsTraites() {
            return clientsTraites;
        }

        public int getAssignmentsCrees() {
            return assignmentsCrees;
        }

        public List<String> getWarnings() {
            return warnings;
        }
    }

    public PlanificationResult planifier(LocalDate date) throws SQLException {
        List<Reservation> reservations = reservationService.getReservationsByDate(date);

        Map<String, List<Reservation>> byClient = new LinkedHashMap<>();
        for (Reservation r : reservations) {
            byClient.computeIfAbsent(r.getIdClient(), k -> new ArrayList<>()).add(r);
        }

        List<Voiture> voituresDisponibles = voitureService.getAllVoitures();
        Set<Integer> voituresDejaUtilisees = getVoituresUtiliseesPourLaDate(date);

        List<String> warnings = new ArrayList<>();
        int assignmentsCrees = 0;

        for (Map.Entry<String, List<Reservation>> entry : byClient.entrySet()) {
            String clientId = entry.getKey();
            List<Reservation> clientReservations = entry.getValue();

            int nbPersonnesMax = 0;
            for (Reservation r : clientReservations) {
                nbPersonnesMax = Math.max(nbPersonnesMax, r.getNbPersonnes());
            }

            List<Voiture> candidates = new ArrayList<>();
            for (Voiture v : voituresDisponibles) {
                if (voituresDejaUtilisees.contains(v.getId())) {
                    continue;
                }
                if (v.getNbPlace() >= nbPersonnesMax) {
                    candidates.add(v);
                }
            }

            if (candidates.isEmpty()) {
                warnings.add("Aucune voiture disponible pour le client " + clientId + " (nbPersonnes max=" + nbPersonnesMax + ")");
                continue;
            }

            Voiture chosen = chooseBestVoiture(candidates, nbPersonnesMax);
            voituresDejaUtilisees.add(chosen.getId());

            for (Reservation r : clientReservations) {
                if (isReservationDejaPlanifiee(r.getId())) {
                    continue;
                }
                createPlanification(r.getId(), chosen.getId());
                assignmentsCrees++;
            }
        }

        return new PlanificationResult(date, reservations.size(), byClient.size(), assignmentsCrees, warnings);
    }

    public PlanificationResult planifierV2(LocalDate date) throws SQLException {
        List<Reservation> reservations = reservationService.getReservationsByDate(date);
        List<Voiture> voitures = voitureService.getAllVoitures();
        Parametre parametre = parametreService.getParametreActif();

        clearPlanificationForDate(date);

        int aeroportLieuId = getAeroportLieuId();

        int maxCapacity = 0;
        for (Voiture v : voitures) {
            maxCapacity = Math.max(maxCapacity, v.getNbPlace());
        }

        Map<LocalTime, List<Reservation>> byTime = new LinkedHashMap<>();
        for (Reservation r : reservations) {
            LocalTime t = r.getHeureReservation();
            if (t == null) {
                t = LocalTime.MIDNIGHT;
            }
            byTime.computeIfAbsent(t, k -> new ArrayList<>()).add(r);
        }

        Map<Integer, List<Interval>> agenda = new HashMap<>();
        for (Voiture v : voitures) {
            agenda.put(v.getId(), new ArrayList<>());
        }

        List<String> warnings = new ArrayList<>();
        int assignmentsCrees = 0;

        for (Map.Entry<LocalTime, List<Reservation>> entry : byTime.entrySet()) {
            LocalTime slotTime = entry.getKey();
            List<Reservation> slotReservations = entry.getValue();

            slotReservations.sort((a, b) -> Integer.compare(b.getNbPersonnes(), a.getNbPersonnes()));
            List<List<Reservation>> groups = buildGroupsFFD(slotReservations, maxCapacity);

            for (List<Reservation> group : groups) {
                int groupPeople = 0;
                for (Reservation r : group) {
                    groupPeople += r.getNbPersonnes();
                }

                List<StopCandidate> stops = new ArrayList<>();
                for (Reservation r : group) {
                    if (r.getHotel() == null) {
                        r.setHotel(reservationService.getReservationById(r.getId()).getHotel());
                    }
                    if (r.getHotel() == null) {
                        throw new SQLException("Hotel introuvable pour la réservation id=" + r.getId());
                    }
                    int lieuId = r.getHotel().getIdLieu();
                    double km = distanceService.getDistanceKm(aeroportLieuId, lieuId);
                    String lieuLabel = lieuService.getLieuById(lieuId) != null ? lieuService.getLieuById(lieuId).getLieu() : null;
                    stops.add(new StopCandidate(r.getId(), lieuId, km, lieuLabel));
                }

                stops.sort((s1, s2) -> {
                    int c = Double.compare(s1.distanceKm, s2.distanceKm);
                    if (c != 0) return c;
                    String l1 = s1.lieuLabel != null ? s1.lieuLabel : "";
                    String l2 = s2.lieuLabel != null ? s2.lieuLabel : "";
                    c = l1.compareToIgnoreCase(l2);
                    if (c != 0) return c;
                    return Integer.compare(s1.lieuId, s2.lieuId);
                });

                double kmTotal = 0.0;
                int currentLieuId = aeroportLieuId;
                for (StopCandidate s : stops) {
                    kmTotal += distanceService.getDistanceKm(currentLieuId, s.lieuId);
                    currentLieuId = s.lieuId;
                }
                kmTotal += distanceService.getDistanceKm(currentLieuId, aeroportLieuId);

                int minutesTrajet = (int) Math.ceil((kmTotal / parametre.getVitesseMoyenneKmh()) * 60.0);
                int minutesAttente = parametre.getTempsAttenteMin() * stops.size();
                int minutesTotal = minutesTrajet + minutesAttente;

                LocalDateTime start = LocalDateTime.of(date, slotTime);
                LocalDateTime end = start.plusMinutes(minutesTotal);

                List<Voiture> candidates = new ArrayList<>();
                for (Voiture v : voitures) {
                    if (v.getNbPlace() < groupPeople) {
                        continue;
                    }
                    if (isAvailable(agenda.get(v.getId()), start, end)) {
                        candidates.add(v);
                    }
                }

                if (candidates.isEmpty()) {
                    warnings.add("Aucune voiture disponible pour le slot " + slotTime + " (personnes=" + groupPeople + ")");
                    continue;
                }

                Voiture chosen = chooseBestVoiture(candidates, groupPeople);
                agenda.get(chosen.getId()).add(new Interval(start, end));

                int voyageId = voyageService.createVoyage(date, slotTime, chosen.getId(), minutesTotal);

                int ordre = 1;
                for (StopCandidate s : stops) {
                    voyageService.addStop(voyageId, ordre, s.reservationId, s.lieuId, s.distanceKm);
                    ordre++;
                }

                for (Reservation r : group) {
                    createPlanification(r.getId(), chosen.getId());
                    assignmentsCrees++;
                }
            }
        }

        Set<String> clients = new HashSet<>();
        for (Reservation r : reservations) {
            clients.add(r.getIdClient());
        }
        return new PlanificationResult(date, reservations.size(), clients.size(), assignmentsCrees, warnings);
    }

    public List<Voyage> getVoyages(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        return voyageService.getVoyagesByDateRange(dateDebut, dateFin);
    }

    public List<VoyageStop> getStops(int voyageId) throws SQLException {
        return voyageService.getStopsByVoyage(voyageId);
    }

    public double getVoyageDistanceTotalKm(int voyageId) throws SQLException {
        int aeroportLieuId = getAeroportLieuId();
        List<VoyageStop> stops = voyageService.getStopsByVoyage(voyageId);

        if (stops == null || stops.isEmpty()) {
            return 0.0;
        }

        double kmTotal = 0.0;
        int currentLieuId = aeroportLieuId;
        for (VoyageStop s : stops) {
            kmTotal += distanceService.getDistanceKm(currentLieuId, s.getIdLieuDestination());
            currentLieuId = s.getIdLieuDestination();
        }
        kmTotal += distanceService.getDistanceKm(currentLieuId, aeroportLieuId);
        return kmTotal;
    }

    private Voiture chooseBestVoiture(List<Voiture> candidates, int nbPersonnes) {
        int minDelta = Integer.MAX_VALUE;
        for (Voiture v : candidates) {
            minDelta = Math.min(minDelta, v.getNbPlace() - nbPersonnes);
        }

        List<Voiture> deltaFiltered = new ArrayList<>();
        for (Voiture v : candidates) {
            if (v.getNbPlace() - nbPersonnes == minDelta) {
                deltaFiltered.add(v);
            }
        }

        Map<String, Integer> fuelRank = new HashMap<>();
        fuelRank.put("D", 1);
        fuelRank.put("H", 2);
        fuelRank.put("El", 3);
        fuelRank.put("E", 4);

        int bestRank = Integer.MAX_VALUE;
        for (Voiture v : deltaFiltered) {
            int r = fuelRank.getOrDefault(v.getTypeCarburant(), 999);
            bestRank = Math.min(bestRank, r);
        }

        List<Voiture> fuelFiltered = new ArrayList<>();
        for (Voiture v : deltaFiltered) {
            int r = fuelRank.getOrDefault(v.getTypeCarburant(), 999);
            if (r == bestRank) {
                fuelFiltered.add(v);
            }
        }

        if (fuelFiltered.size() == 1) {
            return fuelFiltered.get(0);
        }

        Collections.shuffle(fuelFiltered);
        return fuelFiltered.get(0);
    }

    private Set<Integer> getVoituresUtiliseesPourLaDate(LocalDate date) throws SQLException {
        Set<Integer> used = new HashSet<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT rp.id_voiture FROM reservation_planification rp JOIN reservation r ON rp.id_reservation = r.id WHERE r.date_reservation = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                used.add(rs.getInt("id_voiture"));
            }
            rs.close();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return used;
    }

    private boolean isReservationDejaPlanifiee(int reservationId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT 1 FROM reservation_planification WHERE id_reservation = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();
            boolean exists = rs.next();
            rs.close();
            pstmt.close();
            return exists;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void createPlanification(int reservationId, int voitureId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO reservation_planification (id_reservation, id_voiture) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            pstmt.setInt(2, voitureId);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void clearPlanificationForDate(LocalDate date) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlDeleteStops = "DELETE FROM voyage_stop WHERE id_voyage IN (SELECT id FROM voyage WHERE date_voyage = ?)";
            PreparedStatement psStops = conn.prepareStatement(sqlDeleteStops);
            psStops.setDate(1, java.sql.Date.valueOf(date));
            psStops.executeUpdate();
            psStops.close();

            String sqlDeleteVoyages = "DELETE FROM voyage WHERE date_voyage = ?";
            PreparedStatement psVoyages = conn.prepareStatement(sqlDeleteVoyages);
            psVoyages.setDate(1, java.sql.Date.valueOf(date));
            psVoyages.executeUpdate();
            psVoyages.close();

            String sqlDeletePlanif = "DELETE FROM reservation_planification rp USING reservation r WHERE rp.id_reservation = r.id AND r.date_reservation = ?";
            PreparedStatement psPlanif = conn.prepareStatement(sqlDeletePlanif);
            psPlanif.setDate(1, java.sql.Date.valueOf(date));
            psPlanif.executeUpdate();
            psPlanif.close();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
            DatabaseConnection.closeConnection(conn);
        }
    }

    private int getAeroportLieuId() throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id FROM lieu WHERE lower(lieu) = 'aeroport' LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Integer id = null;
            if (rs.next()) {
                id = rs.getInt("id");
            }
            rs.close();
            stmt.close();
            if (id == null) {
                throw new SQLException("Lieu 'Aeroport' introuvable");
            }
            return id;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private static class Interval {
        private final LocalDateTime start;
        private final LocalDateTime end;

        private Interval(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }
    }

    private boolean isAvailable(List<Interval> intervals, LocalDateTime start, LocalDateTime end) {
        for (Interval i : intervals) {
            if (start.isBefore(i.end) && end.isAfter(i.start)) {
                return false;
            }
        }
        return true;
    }

    private List<List<Reservation>> buildGroupsFFD(List<Reservation> sortedByPeopleDesc, int maxCapacity) {
        List<List<Reservation>> groups = new ArrayList<>();
        List<Integer> sums = new ArrayList<>();
        for (Reservation r : sortedByPeopleDesc) {
            if (r.getNbPersonnes() > maxCapacity) {
                List<Reservation> g = new ArrayList<>();
                g.add(r);
                groups.add(g);
                sums.add(r.getNbPersonnes());
                continue;
            }
            boolean placed = false;
            for (int i = 0; i < groups.size(); i++) {
                int current = sums.get(i);
                if (current + r.getNbPersonnes() <= maxCapacity) {
                    groups.get(i).add(r);
                    sums.set(i, current + r.getNbPersonnes());
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                List<Reservation> g = new ArrayList<>();
                g.add(r);
                groups.add(g);
                sums.add(r.getNbPersonnes());
            }
        }
        return groups;
    }

    private static class StopCandidate {
        private final int reservationId;
        private final int lieuId;
        private final double distanceKm;
        private final String lieuLabel;

        private StopCandidate(int reservationId, int lieuId, double distanceKm, String lieuLabel) {
            this.reservationId = reservationId;
            this.lieuId = lieuId;
            this.distanceKm = distanceKm;
            this.lieuLabel = lieuLabel;
        }
    }

    public List<Map<String, Object>> getReservationsPlanifiees(LocalDate date) throws SQLException {
        return getReservationsPlanifiees(date, date);
    }

    public List<Map<String, Object>> getReservationsPlanifiees(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT r.id as reservation_id, r.date_reservation, r.heure_reservation, r.nb_personnes, r.id_client, " +
                    "h.nom as hotel_nom, rp.date_planification, v.id as voiture_id, v.type_carburant, v.nb_place, " +
                    "vs.id_voyage as voyage_id " +
                    "FROM reservation_planification rp " +
                    "JOIN reservation r ON rp.id_reservation = r.id " +
                    "JOIN hotel h ON r.id_hotel = h.id " +
                    "JOIN voiture v ON rp.id_voiture = v.id " +
                    "LEFT JOIN voyage_stop vs ON vs.id_reservation = r.id " +
                    "WHERE r.date_reservation BETWEEN ? AND ? " +
                    "ORDER BY r.date_reservation ASC, rp.date_planification DESC, r.id ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(dateDebut));
            pstmt.setDate(2, java.sql.Date.valueOf(dateFin));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("reservationId", rs.getInt("reservation_id"));
                row.put("dateReservation", rs.getDate("date_reservation"));
                row.put("heureReservation", rs.getTime("heure_reservation"));
                row.put("nbPersonnes", rs.getInt("nb_personnes"));
                row.put("idClient", rs.getString("id_client"));
                row.put("hotelNom", rs.getString("hotel_nom"));
                row.put("datePlanification", rs.getTimestamp("date_planification"));
                row.put("voitureId", rs.getInt("voiture_id"));
                row.put("typeCarburant", rs.getString("type_carburant"));
                row.put("nbPlace", rs.getInt("nb_place"));
                Object voyageId = rs.getObject("voyage_id");
                row.put("voyageId", voyageId != null ? ((Number) voyageId).intValue() : null);
                rows.add(row);
            }
            rs.close();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return rows;
    }
}
