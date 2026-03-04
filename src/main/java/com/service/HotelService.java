package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Hotel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotelService {

    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT h.*, l.lieu as lieu_label FROM hotel h JOIN lieu l ON h.id_lieu = l.id ORDER BY h.nom";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Hotel h = new Hotel(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("ville"),
                        rs.getString("pays"),
                        rs.getInt("id_lieu")
                );
                h.setLieuLabel(rs.getString("lieu_label"));
                hotels.add(h);
            }
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return hotels;
    }

    public Hotel getHotelById(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT h.*, l.lieu as lieu_label FROM hotel h JOIN lieu l ON h.id_lieu = l.id WHERE h.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            Hotel h = null;
            if (rs.next()) {
                h = new Hotel(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("adresse"),
                        rs.getString("ville"),
                        rs.getString("pays"),
                        rs.getInt("id_lieu")
                );
                h.setLieuLabel(rs.getString("lieu_label"));
            }

            rs.close();
            pstmt.close();
            return h;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public Hotel createHotel(String nom, String adresse, String ville, String pays, int idLieu) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO hotel (nom, adresse, ville, pays, id_lieu) VALUES (?, ?, ?, ?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, adresse);
            pstmt.setString(3, ville);
            pstmt.setString(4, pays);
            pstmt.setInt(5, idLieu);

            ResultSet rs = pstmt.executeQuery();
            Hotel h = new Hotel();
            if (rs.next()) {
                h.setId(rs.getInt("id"));
            }
            h.setNom(nom);
            h.setAdresse(adresse);
            h.setVille(ville);
            h.setPays(pays);
            h.setIdLieu(idLieu);

            rs.close();
            pstmt.close();
            return h;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void updateHotel(int id, String nom, String adresse, String ville, String pays, int idLieu) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE hotel SET nom = ?, adresse = ?, ville = ?, pays = ?, id_lieu = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nom);
            pstmt.setString(2, adresse);
            pstmt.setString(3, ville);
            pstmt.setString(4, pays);
            pstmt.setInt(5, idLieu);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void deleteHotel(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM hotel WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
}
