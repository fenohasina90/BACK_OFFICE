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
                createPlanification(r.getId(), chosen.getId(), null, r.getNbPersonnes());
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

        Map<Integer, Reservation> reservationById = new HashMap<>();
        for (Reservation r : reservations) {
            reservationById.put(r.getId(), r);
        }

        NavigableMap<LocalTime, List<Reservation>> byTime = new TreeMap<>();
        int waitMinutes = parametre.getTempsAttenteMin();
        for (Reservation r : reservations) {
            LocalTime t = r.getHeureReservation();
            if (t == null) {
                t = LocalTime.MIDNIGHT;
            }
            LocalTime windowStart = getWindowStart(t, waitMinutes);
            byTime.computeIfAbsent(windowStart, k -> new ArrayList<>()).add(r);
        }

        Map<Integer, List<Interval>> agenda = new HashMap<>();
        for (Voiture v : voitures) {
            agenda.put(v.getId(), new ArrayList<>());
        }

        Map<Integer, Integer> voyageCountByVoitureId = new HashMap<>();
        for (Voiture v : voitures) {
            voyageCountByVoitureId.put(v.getId(), 0);
        }

        List<String> warnings = new ArrayList<>();
        int assignmentsCrees = 0;

        Deque<Fragment> restesFIFO = new ArrayDeque<>();

        while (!byTime.isEmpty()) {
            Map.Entry<LocalTime, List<Reservation>> entry = byTime.pollFirstEntry();
            LocalTime slotTime = entry.getKey();
            List<Reservation> slotReservations = entry.getValue();

            if (slotReservations == null || slotReservations.isEmpty()) {
                continue;
            }

            for (Reservation r : slotReservations) {
                if (r.getHotel() == null) {
                    Reservation full = reservationService.getReservationById(r.getId());
                    if (full != null) {
                        r.setHotel(full.getHotel());
                    }
                }
                if (r.getHotel() == null) {
                    throw new SQLException("Hotel introuvable pour la réservation id=" + r.getId());
                }
            }

            List<Fragment> poolNormal = new ArrayList<>();
            for (Reservation r : slotReservations) {
                poolNormal.add(Fragment.fromReservation(r, aeroportLieuId, distanceService, lieuService));
            }

            while (true) {
                if (poolNormal.isEmpty() && restesFIFO.isEmpty()) {
                    break;
                }

                Integer demandePrioritaire = null;
                if (!restesFIFO.isEmpty()) {
                    demandePrioritaire = restesFIFO.peekFirst().nbPersonnesRestantes;
                } else {
                    int max = 0;
                    for (Fragment f : poolNormal) {
                        max = Math.max(max, f.nbPersonnesRestantes);
                    }
                    demandePrioritaire = max;
                }

                if (demandePrioritaire == null || demandePrioritaire <= 0) {
                    break;
                }

                List<Voiture> deltaCandidates = new ArrayList<>();
                for (Voiture v : voitures) {
                    if (v.getNbPlace() >= demandePrioritaire) {
                        deltaCandidates.add(v);
                    }
                }

                if (deltaCandidates.isEmpty()) {
                    deltaCandidates.addAll(voitures);
                }

                final int dp = demandePrioritaire;

                deltaCandidates.sort((v1, v2) -> {
                    int d1 = v1.getNbPlace() - dp;
                    int d2 = v2.getNbPlace() - dp;
                    int c;
                    if (d1 >= 0 && d2 >= 0) {
                        c = Integer.compare(d1, d2);
                    } else if (d1 < 0 && d2 < 0) {
                        c = Integer.compare(Math.abs(d1), Math.abs(d2));
                    } else {
                        c = (d1 >= 0) ? -1 : 1;
                    }
                    if (c != 0) return c;
                    int c1 = voyageCountByVoitureId.getOrDefault(v1.getId(), 0);
                    int c2 = voyageCountByVoitureId.getOrDefault(v2.getId(), 0);
                    c = Integer.compare(c1, c2);
                    if (c != 0) return c;
                    return 0;
                });

                LocalTime departTime = slotTime.plusMinutes(waitMinutes);
                LocalDateTime start = LocalDateTime.of(date, departTime);

                CandidateVoyage chosenCandidate = null;
                for (Voiture v : deltaCandidates) {
                    CandidateVoyage candidate = buildCandidateVoyage(date, slotTime, v, parametre, aeroportLieuId, start, agenda.get(v.getId()), restesFIFO, poolNormal);
                    if (candidate != null) {
                        chosenCandidate = candidate;
                        break;
                    }
                }

                if (chosenCandidate == null) {
                    if (waitMinutes <= 0) {
                        warnings.add("Aucune voiture disponible pour le slot " + slotTime + " (demandePrioritaire=" + demandePrioritaire + ")");
                        break;
                    }
                    LocalTime nextSlot = slotTime.plusMinutes(waitMinutes);
                    if (nextSlot.equals(slotTime) || nextSlot.isBefore(slotTime)) {
                        warnings.add("Aucune voiture disponible pour le slot " + slotTime + " (demandePrioritaire=" + demandePrioritaire + ")");
                        break;
                    }

                    byTime.computeIfAbsent(nextSlot, k -> new ArrayList<>()).addAll(materializeFragmentsAsReservations(reservationById, restesFIFO, poolNormal));
                    restesFIFO.clear();
                    poolNormal.clear();
                    break;
                }

                applyCandidateVoyage(chosenCandidate, restesFIFO, poolNormal);

                agenda.get(chosenCandidate.voiture.getId()).add(new Interval(chosenCandidate.start, chosenCandidate.end));

                int voyageId = voyageService.createVoyage(date, departTime, chosenCandidate.voiture.getId(), chosenCandidate.minutesTotal);
                voyageCountByVoitureId.put(chosenCandidate.voiture.getId(), voyageCountByVoitureId.getOrDefault(chosenCandidate.voiture.getId(), 0) + 1);

                int ordre = 1;
                for (StopForInsert s : chosenCandidate.stopsForInsert) {
                    voyageService.addStop(voyageId, ordre, s.reservationId, s.lieuId, s.distanceKm);
                    ordre++;
                }

                for (Assignment a : chosenCandidate.assignments) {
                    createPlanification(a.reservationId, chosenCandidate.voiture.getId(), voyageId, a.nbPersonnes);
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

    private void createPlanification(int reservationId, int voitureId, Integer voyageId, int nbPersonnesAffectees) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO reservation_planification (id_reservation, id_voiture, id_voyage, nb_personnes_affectees) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, reservationId);
            pstmt.setInt(2, voitureId);
            if (voyageId == null) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, voyageId);
            }
            pstmt.setInt(4, nbPersonnesAffectees);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private LocalTime getWindowStart(LocalTime t, int waitMinutes) {
        if (t == null) {
            return LocalTime.MIDNIGHT;
        }
        if (waitMinutes <= 0) {
            return t;
        }
        int minutes = t.getHour() * 60 + t.getMinute();
        int windowStartMinutes = (minutes / waitMinutes) * waitMinutes;
        int h = windowStartMinutes / 60;
        int m = windowStartMinutes % 60;
        return LocalTime.of(h, m);
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

    public List<Voyage> getVoyages(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        return voyageService.getVoyagesByDateRange(dateDebut, dateFin);
    }

    public Map<Integer, Integer> getVoyageCountsByVoiture(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        return voyageService.getVoyageCountsByVoiture(dateDebut, dateFin);
    }

    public List<VoyageStop> getStops(int voyageId) throws SQLException {
        return voyageService.getStopsByVoyage(voyageId);
    }

    public Voyage getVoyageById(int voyageId) throws SQLException {
        return voyageService.getVoyageById(voyageId);
    }

    public static class VoyageTiming {
        private final Map<Integer, LocalTime> arrivalAtDestinationByStopId;
        private final LocalTime arrivalAtAeroport;

        public VoyageTiming(Map<Integer, LocalTime> arrivalAtDestinationByStopId, LocalTime arrivalAtAeroport) {
            this.arrivalAtDestinationByStopId = arrivalAtDestinationByStopId;
            this.arrivalAtAeroport = arrivalAtAeroport;
        }

        public Map<Integer, LocalTime> getArrivalAtDestinationByStopId() {
            return arrivalAtDestinationByStopId;
        }

        public LocalTime getArrivalAtAeroport() {
            return arrivalAtAeroport;
        }
    }

    public VoyageTiming getVoyageTiming(int voyageId) throws SQLException {
        Voyage voyage = voyageService.getVoyageById(voyageId);
        if (voyage == null) {
            throw new SQLException("Voyage introuvable id=" + voyageId);
        }

        Parametre parametre = parametreService.getParametreActif();
        int waitMinutes = parametre.getTempsAttenteMin();
        double vitesseKmh = parametre.getVitesseMoyenneKmh();
        if (vitesseKmh <= 0) {
            throw new SQLException("Paramètre vitesse_moyenne_kmh invalide: " + vitesseKmh);
        }

        int aeroportLieuId = getAeroportLieuId();
        List<VoyageStop> stops = voyageService.getStopsByVoyage(voyageId);

        LocalDateTime current = LocalDateTime.of(voyage.getDateVoyage(), voyage.getHeureDepart());
        int currentLieuId = aeroportLieuId;

        Map<Integer, LocalTime> arrivalByStopId = new HashMap<>();

        if (stops != null) {
            for (VoyageStop s : stops) {
                double km = distanceService.getDistanceKm(currentLieuId, s.getIdLieuDestination());
                int minutesTravel = (int) Math.ceil((km / vitesseKmh) * 60.0);
                current = current.plusMinutes(minutesTravel);
                arrivalByStopId.put(s.getId(), current.toLocalTime());

                currentLieuId = s.getIdLieuDestination();
            }
        }

        double kmBack = distanceService.getDistanceKm(currentLieuId, aeroportLieuId);
        int minutesBack = (int) Math.ceil((kmBack / vitesseKmh) * 60.0);
        LocalDateTime arrivalAeroport = current.plusMinutes(minutesBack);

        return new VoyageTiming(arrivalByStopId, arrivalAeroport.toLocalTime());
    }

    public static class VoyageTimingDateTime {
        private final Map<Integer, LocalDateTime> arrivalAtDestinationByStopId;
        private final LocalDateTime arrivalAtAeroport;

        public VoyageTimingDateTime(Map<Integer, LocalDateTime> arrivalAtDestinationByStopId, LocalDateTime arrivalAtAeroport) {
            this.arrivalAtDestinationByStopId = arrivalAtDestinationByStopId;
            this.arrivalAtAeroport = arrivalAtAeroport;
        }

        public Map<Integer, LocalDateTime> getArrivalAtDestinationByStopId() {
            return arrivalAtDestinationByStopId;
        }

        public LocalDateTime getArrivalAtAeroport() {
            return arrivalAtAeroport;
        }
    }

    public VoyageTimingDateTime getVoyageTimingDateTime(int voyageId) throws SQLException {
        Voyage voyage = voyageService.getVoyageById(voyageId);
        if (voyage == null) {
            throw new SQLException("Voyage introuvable id=" + voyageId);
        }

        Parametre parametre = parametreService.getParametreActif();
        int waitMinutes = parametre.getTempsAttenteMin();
        double vitesseKmh = parametre.getVitesseMoyenneKmh();
        if (vitesseKmh <= 0) {
            throw new SQLException("Paramètre vitesse_moyenne_kmh invalide: " + vitesseKmh);
        }

        int aeroportLieuId = getAeroportLieuId();
        List<VoyageStop> stops = voyageService.getStopsByVoyage(voyageId);

        LocalDateTime current = LocalDateTime.of(voyage.getDateVoyage(), voyage.getHeureDepart());
        int currentLieuId = aeroportLieuId;

        Map<Integer, LocalDateTime> arrivalByStopId = new HashMap<>();

        if (stops != null) {
            for (VoyageStop s : stops) {
                double km = distanceService.getDistanceKm(currentLieuId, s.getIdLieuDestination());
                int minutesTravel = (int) Math.ceil((km / vitesseKmh) * 60.0);
                current = current.plusMinutes(minutesTravel);
                arrivalByStopId.put(s.getId(), current);

                currentLieuId = s.getIdLieuDestination();
            }
        }

        double kmBack = distanceService.getDistanceKm(currentLieuId, aeroportLieuId);
        int minutesBack = (int) Math.ceil((kmBack / vitesseKmh) * 60.0);
        LocalDateTime arrivalAeroport = current.plusMinutes(minutesBack);

        return new VoyageTimingDateTime(arrivalByStopId, arrivalAeroport);
    }

    public static class VoyageLeg {
        private final String fromLabel;
        private final String toLabel;
        private final double distanceKm;
        private final LocalDateTime depart;
        private final LocalDateTime arrive;

        public VoyageLeg(String fromLabel, String toLabel, double distanceKm, LocalDateTime depart, LocalDateTime arrive) {
            this.fromLabel = fromLabel;
            this.toLabel = toLabel;
            this.distanceKm = distanceKm;
            this.depart = depart;
            this.arrive = arrive;
        }

        public String getFromLabel() {
            return fromLabel;
        }

        public String getToLabel() {
            return toLabel;
        }

        public double getDistanceKm() {
            return distanceKm;
        }

        public LocalDateTime getDepart() {
            return depart;
        }

        public LocalDateTime getArrive() {
            return arrive;
        }
    }

    public List<VoyageLeg> getVoyageLegs(int voyageId) throws SQLException {
        Voyage voyage = voyageService.getVoyageById(voyageId);
        if (voyage == null) {
            throw new SQLException("Voyage introuvable id=" + voyageId);
        }

        Parametre parametre = parametreService.getParametreActif();
        int waitMinutes = parametre.getTempsAttenteMin();
        double vitesseKmh = parametre.getVitesseMoyenneKmh();
        if (vitesseKmh <= 0) {
            throw new SQLException("Paramètre vitesse_moyenne_kmh invalide: " + vitesseKmh);
        }

        int aeroportLieuId = getAeroportLieuId();
        List<VoyageStop> stops = voyageService.getStopsByVoyage(voyageId);

        List<VoyageLeg> legs = new ArrayList<>();

        LocalDateTime current = LocalDateTime.of(voyage.getDateVoyage(), voyage.getHeureDepart());
        int currentLieuId = aeroportLieuId;
        String currentLabel = "Aeroport";

        if (stops != null) {
            for (VoyageStop s : stops) {
                String destLabel = s.getLieuLabel() != null ? s.getLieuLabel() : ("#" + s.getIdLieuDestination());
                double km = distanceService.getDistanceKm(currentLieuId, s.getIdLieuDestination());
                int minutesTravel = (int) Math.ceil((km / vitesseKmh) * 60.0);
                LocalDateTime depart = current;
                LocalDateTime arrive = current.plusMinutes(minutesTravel);
                legs.add(new VoyageLeg(currentLabel, destLabel, km, depart, arrive));

                current = arrive;
                currentLieuId = s.getIdLieuDestination();
                currentLabel = destLabel;
            }
        }

        double kmBack = distanceService.getDistanceKm(currentLieuId, aeroportLieuId);
        int minutesBack = (int) Math.ceil((kmBack / vitesseKmh) * 60.0);
        LocalDateTime departBack = current;
        LocalDateTime arriveBack = current.plusMinutes(minutesBack);
        legs.add(new VoyageLeg(currentLabel, "Aeroport", kmBack, departBack, arriveBack));

        return legs;
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
                    "rp.id_voyage as voyage_id, rp.nb_personnes_affectees " +
                    "FROM reservation_planification rp " +
                    "JOIN reservation r ON rp.id_reservation = r.id " +
                    "JOIN hotel h ON r.id_hotel = h.id " +
                    "JOIN voiture v ON rp.id_voiture = v.id " +
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
                row.put("nbPersonnesAffectees", rs.getInt("nb_personnes_affectees"));
                rows.add(row);
            }
            rs.close();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return rows;
    }

    public List<Reservation> getReservationsByDate(LocalDate date) throws SQLException {
        return reservationService.getReservationsByDate(date);
    }

    public Parametre getParametreActif() throws SQLException {
        return parametreService.getParametreActif();
    }

    public LocalTime getCreneauStart(LocalTime reservationTime) throws SQLException {
        Parametre parametre = parametreService.getParametreActif();
        int waitMinutes = parametre.getTempsAttenteMin();
        LocalTime t = reservationTime;
        if (t == null) {
            t = LocalTime.MIDNIGHT;
        }
        return getWindowStart(t, waitMinutes);
    }

    private static class Fragment {
        private final int reservationId;
        private final int lieuId;
        private final double distanceKmFromAeroport;
        private final String lieuLabel;
        private int nbPersonnesRestantes;

        private Fragment(int reservationId, int lieuId, double distanceKmFromAeroport, String lieuLabel, int nbPersonnesRestantes) {
            this.reservationId = reservationId;
            this.lieuId = lieuId;
            this.distanceKmFromAeroport = distanceKmFromAeroport;
            this.lieuLabel = lieuLabel;
            this.nbPersonnesRestantes = nbPersonnesRestantes;
        }

        private static Fragment fromReservation(Reservation r, int aeroportLieuId, DistanceService distanceService, LieuService lieuService) throws SQLException {
            int lieuId = r.getHotel().getIdLieu();
            double km = distanceService.getDistanceKm(aeroportLieuId, lieuId);
            String label = lieuService.getLieuById(lieuId) != null ? lieuService.getLieuById(lieuId).getLieu() : null;
            return new Fragment(r.getId(), lieuId, km, label, r.getNbPersonnes());
        }
    }

    private static class Assignment {
        private final int reservationId;
        private final int nbPersonnes;

        private Assignment(int reservationId, int nbPersonnes) {
            this.reservationId = reservationId;
            this.nbPersonnes = nbPersonnes;
        }
    }

    private static class StopForInsert {
        private final int reservationId;
        private final int lieuId;
        private final double distanceKm;

        private StopForInsert(int reservationId, int lieuId, double distanceKm) {
            this.reservationId = reservationId;
            this.lieuId = lieuId;
            this.distanceKm = distanceKm;
        }
    }

    private static class CandidateVoyage {
        private final Voiture voiture;
        private final LocalDateTime start;
        private final LocalDateTime end;
        private final int minutesTotal;
        private final List<Assignment> assignments;
        private final List<StopForInsert> stopsForInsert;
        private final Deque<Fragment> restesAfter;
        private final List<Fragment> poolAfter;

        private CandidateVoyage(Voiture voiture, LocalDateTime start, LocalDateTime end, int minutesTotal, List<Assignment> assignments, List<StopForInsert> stopsForInsert, Deque<Fragment> restesAfter, List<Fragment> poolAfter) {
            this.voiture = voiture;
            this.start = start;
            this.end = end;
            this.minutesTotal = minutesTotal;
            this.assignments = assignments;
            this.stopsForInsert = stopsForInsert;
            this.restesAfter = restesAfter;
            this.poolAfter = poolAfter;
        }
    }

    private CandidateVoyage buildCandidateVoyage(LocalDate date, LocalTime slotTime, Voiture v, Parametre parametre, int aeroportLieuId, LocalDateTime start, List<Interval> intervals, Deque<Fragment> restesFIFO, List<Fragment> poolNormal) throws SQLException {
        int remainingSeats = v.getNbPlace();

        Deque<Fragment> restesCopy = new ArrayDeque<>();
        for (Fragment f : restesFIFO) {
            restesCopy.addLast(new Fragment(f.reservationId, f.lieuId, f.distanceKmFromAeroport, f.lieuLabel, f.nbPersonnesRestantes));
        }
        List<Fragment> poolCopy = new ArrayList<>();
        for (Fragment f : poolNormal) {
            poolCopy.add(new Fragment(f.reservationId, f.lieuId, f.distanceKmFromAeroport, f.lieuLabel, f.nbPersonnesRestantes));
        }

        List<Assignment> assignments = new ArrayList<>();

        while (remainingSeats > 0 && !restesCopy.isEmpty()) {
            Fragment f = restesCopy.peekFirst();
            int take = Math.min(f.nbPersonnesRestantes, remainingSeats);
            assignments.add(new Assignment(f.reservationId, take));
            f.nbPersonnesRestantes -= take;
            remainingSeats -= take;
            if (f.nbPersonnesRestantes == 0) {
                restesCopy.removeFirst();
            }
        }

        poolCopy.sort((a, b) -> Integer.compare(b.nbPersonnesRestantes, a.nbPersonnesRestantes));
        List<Fragment> poolRemaining = new ArrayList<>();
        for (Fragment f : poolCopy) {
            if (remainingSeats <= 0) {
                poolRemaining.add(f);
                continue;
            }
            int take = Math.min(f.nbPersonnesRestantes, remainingSeats);
            assignments.add(new Assignment(f.reservationId, take));
            f.nbPersonnesRestantes -= take;
            remainingSeats -= take;
            if (f.nbPersonnesRestantes > 0) {
                restesCopy.addLast(f);
            }
        }

        if (assignments.isEmpty()) {
            return null;
        }

        Map<Integer, Fragment> fragmentByReservation = new HashMap<>();
        for (Fragment f : restesCopy) {
            fragmentByReservation.putIfAbsent(f.reservationId, f);
        }
        for (Fragment f : poolCopy) {
            fragmentByReservation.putIfAbsent(f.reservationId, f);
        }
        for (Fragment f : poolNormal) {
            fragmentByReservation.putIfAbsent(f.reservationId, f);
        }
        for (Fragment f : restesFIFO) {
            fragmentByReservation.putIfAbsent(f.reservationId, f);
        }

        List<StopCandidate> stopCandidates = new ArrayList<>();
        for (Assignment a : assignments) {
            Fragment f = fragmentByReservation.get(a.reservationId);
            if (f == null) {
                continue;
            }
            stopCandidates.add(new StopCandidate(a.reservationId, f.lieuId, f.distanceKmFromAeroport, f.lieuLabel));
        }

        stopCandidates.sort((s1, s2) -> {
            int c = Double.compare(s1.distanceKm, s2.distanceKm);
            if (c != 0) return c;
            String l1 = s1.lieuLabel != null ? s1.lieuLabel : "";
            String l2 = s2.lieuLabel != null ? s2.lieuLabel : "";
            c = l1.compareToIgnoreCase(l2);
            if (c != 0) return c;
            return Integer.compare(s1.lieuId, s2.lieuId);
        });

        List<Integer> distinctLieuPath = new ArrayList<>();
        for (StopCandidate s : stopCandidates) {
            if (distinctLieuPath.isEmpty() || distinctLieuPath.get(distinctLieuPath.size() - 1) != s.lieuId) {
                distinctLieuPath.add(s.lieuId);
            }
        }

        double kmTotal = 0.0;
        int currentLieuId = aeroportLieuId;
        for (Integer lieuId : distinctLieuPath) {
            kmTotal += distanceService.getDistanceKm(currentLieuId, lieuId);
            currentLieuId = lieuId;
        }
        kmTotal += distanceService.getDistanceKm(currentLieuId, aeroportLieuId);

        int minutesTrajet = (int) Math.ceil((kmTotal / parametre.getVitesseMoyenneKmh()) * 60.0);
        int minutesTotal = minutesTrajet;

        LocalDateTime end = start.plusMinutes(minutesTotal);
        if (!isAvailable(intervals, start, end)) {
            return null;
        }

        List<StopForInsert> stopsForInsert = new ArrayList<>();
        int prevLieu = aeroportLieuId;
        Set<Integer> visited = new HashSet<>();
        for (StopCandidate s : stopCandidates) {
            double km;
            if (visited.contains(s.lieuId)) {
                km = 0.0;
            } else {
                km = distanceService.getDistanceKm(prevLieu, s.lieuId);
                prevLieu = s.lieuId;
                visited.add(s.lieuId);
            }
            stopsForInsert.add(new StopForInsert(s.reservationId, s.lieuId, km));
        }

        return new CandidateVoyage(v, start, end, minutesTotal, assignments, stopsForInsert, restesCopy, poolRemaining);
    }

    private void applyCandidateVoyage(CandidateVoyage candidate, Deque<Fragment> restesFIFO, List<Fragment> poolNormal) {
        restesFIFO.clear();
        restesFIFO.addAll(candidate.restesAfter);
        poolNormal.clear();
        poolNormal.addAll(candidate.poolAfter);
    }

    private List<Reservation> materializeFragmentsAsReservations(Map<Integer, Reservation> reservationById, Deque<Fragment> restesFIFO, List<Fragment> poolNormal) {
        List<Reservation> out = new ArrayList<>();
        Map<Integer, Integer> remainingByReservationId = new LinkedHashMap<>();
        for (Fragment f : restesFIFO) {
            if (f.nbPersonnesRestantes > 0) {
                remainingByReservationId.put(f.reservationId, f.nbPersonnesRestantes);
            }
        }
        for (Fragment f : poolNormal) {
            if (f.nbPersonnesRestantes > 0) {
                remainingByReservationId.putIfAbsent(f.reservationId, f.nbPersonnesRestantes);
            }
        }

        for (Map.Entry<Integer, Integer> e : remainingByReservationId.entrySet()) {
            Integer id = e.getKey();
            Integer remaining = e.getValue();
            Reservation r = reservationById.get(id);
            if (r == null) {
                continue;
            }
            Reservation copy = new Reservation(r.getId(), r.getDateReservation(), r.getHeureReservation(), remaining, r.getIdClient(), r.getIdHotel());
            copy.setHotel(r.getHotel());
            copy.setClient(r.getClient());
            out.add(copy);
        }
        return out;
    }
}
