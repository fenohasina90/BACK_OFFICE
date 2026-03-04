package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Reservation;
import main.java.com.entity.Voiture;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class PlanificationService {

    private final ReservationService reservationService = new ReservationService();
    private final VoitureService voitureService = new VoitureService();

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

    public List<Map<String, Object>> getReservationsPlanifiees(LocalDate date) throws SQLException {
        return getReservationsPlanifiees(date, date);
    }

    public List<Map<String, Object>> getReservationsPlanifiees(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT r.id as reservation_id, r.date_reservation, r.heure_reservation, r.nb_personnes, r.id_client, " +
                    "h.nom as hotel_nom, rp.date_planification, v.id as voiture_id, v.type_carburant, v.nb_place " +
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
