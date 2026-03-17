package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Voyage;
import main.java.com.entity.VoyageStop;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoyageService {

    public int createVoyage(LocalDate dateVoyage, LocalTime heureDepart, int idVoiture, int dureeMinutes) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO voyage (date_voyage, heure_depart, id_voiture, duree_minutes) VALUES (?, ?, ?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(dateVoyage));
            pstmt.setTime(2, java.sql.Time.valueOf(heureDepart));
            pstmt.setInt(3, idVoiture);
            pstmt.setInt(4, dureeMinutes);
            ResultSet rs = pstmt.executeQuery();
            int id = -1;
            if (rs.next()) {
                id = rs.getInt("id");
            }
            rs.close();
            pstmt.close();
            if (id <= 0) {
                throw new SQLException("Impossible de créer le voyage");
            }
            return id;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public Map<Integer, Integer> getVoyageCountsByVoiture(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        Map<Integer, Integer> counts = new HashMap<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id_voiture, COUNT(*) as nb FROM voyage WHERE date_voyage BETWEEN ? AND ? GROUP BY id_voiture";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(dateDebut));
            pstmt.setDate(2, java.sql.Date.valueOf(dateFin));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                counts.put(rs.getInt("id_voiture"), rs.getInt("nb"));
            }
            rs.close();
            pstmt.close();
            return counts;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void addStop(int voyageId, int ordre, int reservationId, int lieuId, double distanceKm) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO voyage_stop (id_voyage, ordre, id_reservation, id_lieu_destination, distance_km) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, voyageId);
            pstmt.setInt(2, ordre);
            pstmt.setInt(3, reservationId);
            pstmt.setInt(4, lieuId);
            pstmt.setDouble(5, distanceKm);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Voyage> getVoyagesByDateRange(LocalDate dateDebut, LocalDate dateFin) throws SQLException {
        List<Voyage> voyages = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM voyage WHERE date_voyage BETWEEN ? AND ? ORDER BY date_voyage ASC, heure_depart ASC, id ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(dateDebut));
            pstmt.setDate(2, java.sql.Date.valueOf(dateFin));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Voyage v = new Voyage(
                        rs.getInt("id"),
                        rs.getDate("date_voyage").toLocalDate(),
                        rs.getTime("heure_depart").toLocalTime(),
                        rs.getInt("id_voiture"),
                        rs.getInt("duree_minutes"),
                        rs.getTimestamp("date_creation")
                );
                voyages.add(v);
            }
            rs.close();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return voyages;
    }

    public Voyage getVoyageById(int voyageId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM voyage WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, voyageId);
            ResultSet rs = pstmt.executeQuery();
            Voyage v = null;
            if (rs.next()) {
                v = new Voyage(
                        rs.getInt("id"),
                        rs.getDate("date_voyage").toLocalDate(),
                        rs.getTime("heure_depart").toLocalTime(),
                        rs.getInt("id_voiture"),
                        rs.getInt("duree_minutes"),
                        rs.getTimestamp("date_creation")
                );
            }
            rs.close();
            pstmt.close();
            return v;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<VoyageStop> getStopsByVoyage(int voyageId) throws SQLException {
        List<VoyageStop> stops = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT vs.*, l.lieu as lieu_label FROM voyage_stop vs JOIN lieu l ON vs.id_lieu_destination = l.id WHERE vs.id_voyage = ? ORDER BY vs.ordre ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, voyageId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                VoyageStop s = new VoyageStop(
                        rs.getInt("id"),
                        rs.getInt("id_voyage"),
                        rs.getInt("ordre"),
                        rs.getInt("id_reservation"),
                        rs.getInt("id_lieu_destination"),
                        rs.getDouble("distance_km")
                );
                s.setLieuLabel(rs.getString("lieu_label"));
                stops.add(s);
            }
            rs.close();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return stops;
    }
}
