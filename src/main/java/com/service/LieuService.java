package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Lieu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LieuService {

    public List<Lieu> getAllLieux() throws SQLException {
        List<Lieu> lieux = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM lieu ORDER BY id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Lieu l = new Lieu(rs.getInt("id"), rs.getString("lieu"));
                lieux.add(l);
            }
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return lieux;
    }

    public Lieu getLieuById(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM lieu WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            Lieu l = null;
            if (rs.next()) {
                l = new Lieu(rs.getInt("id"), rs.getString("lieu"));
            }
            rs.close();
            pstmt.close();
            return l;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public Lieu createLieu(String libelle) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO lieu (lieu) VALUES (?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, libelle);
            ResultSet rs = pstmt.executeQuery();
            Lieu l = new Lieu();
            if (rs.next()) {
                l.setId(rs.getInt("id"));
            }
            l.setLieu(libelle);
            rs.close();
            pstmt.close();
            return l;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void updateLieu(int id, String libelle) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE lieu SET lieu = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, libelle);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void deleteLieu(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM lieu WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
}
