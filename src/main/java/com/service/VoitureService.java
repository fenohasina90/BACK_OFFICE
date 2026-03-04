package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Voiture;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoitureService {

    public List<Voiture> getAllVoitures() throws SQLException {
        List<Voiture> voitures = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM voiture ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Voiture voiture = new Voiture(
                    rs.getInt("id"),
                    rs.getString("type_carburant"),
                    rs.getInt("nb_place")
                );
                voitures.add(voiture);
            }
            
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return voitures;
    }

    public Voiture getVoitureById(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM voiture WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            Voiture voiture = null;
            if (rs.next()) {
                voiture = new Voiture(
                    rs.getInt("id"),
                    rs.getString("type_carburant"),
                    rs.getInt("nb_place")
                );
            }
            
            rs.close();
            pstmt.close();
            return voiture;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public Voiture createVoiture(String typeCarburant, int nbPlace) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO voiture (type_carburant, nb_place) VALUES (?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typeCarburant);
            pstmt.setInt(2, nbPlace);
            
            ResultSet rs = pstmt.executeQuery();
            Voiture voiture = new Voiture();
            if (rs.next()) {
                voiture.setId(rs.getInt("id"));
            }
            voiture.setTypeCarburant(typeCarburant);
            voiture.setNbPlace(nbPlace);
            
            rs.close();
            pstmt.close();
            return voiture;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void updateVoiture(int id, String typeCarburant, int nbPlace) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE voiture SET type_carburant = ?, nb_place = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, typeCarburant);
            pstmt.setInt(2, nbPlace);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void deleteVoiture(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM voiture WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
}
