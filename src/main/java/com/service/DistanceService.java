package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Distance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistanceService {

    public List<Distance> getAllDistances() throws SQLException {
        List<Distance> distances = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT d.*, lf.lieu as from_lieu_label, lt.lieu as to_lieu_label " +
                    "FROM distance d " +
                    "JOIN lieu lf ON d.from_lieu = lf.id " +
                    "JOIN lieu lt ON d.to_lieu = lt.id " +
                    "ORDER BY d.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Distance d = new Distance(
                        rs.getInt("id"),
                        rs.getInt("from_lieu"),
                        rs.getInt("to_lieu"),
                        rs.getDouble("distance_km")
                );
                d.setFromLieuLabel(rs.getString("from_lieu_label"));
                d.setToLieuLabel(rs.getString("to_lieu_label"));
                distances.add(d);
            }
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return distances;
    }

    public Distance getDistanceById(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT d.*, lf.lieu as from_lieu_label, lt.lieu as to_lieu_label " +
                    "FROM distance d " +
                    "JOIN lieu lf ON d.from_lieu = lf.id " +
                    "JOIN lieu lt ON d.to_lieu = lt.id " +
                    "WHERE d.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            Distance d = null;
            if (rs.next()) {
                d = new Distance(
                        rs.getInt("id"),
                        rs.getInt("from_lieu"),
                        rs.getInt("to_lieu"),
                        rs.getDouble("distance_km")
                );
                d.setFromLieuLabel(rs.getString("from_lieu_label"));
                d.setToLieuLabel(rs.getString("to_lieu_label"));
            }
            rs.close();
            pstmt.close();
            return d;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public Distance createDistance(int fromLieu, int toLieu, double distanceKm) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO distance (from_lieu, to_lieu, distance_km) VALUES (?, ?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fromLieu);
            pstmt.setInt(2, toLieu);
            pstmt.setDouble(3, distanceKm);
            ResultSet rs = pstmt.executeQuery();
            Distance d = new Distance();
            if (rs.next()) {
                d.setId(rs.getInt("id"));
            }
            d.setFromLieu(fromLieu);
            d.setToLieu(toLieu);
            d.setDistanceKm(distanceKm);
            rs.close();
            pstmt.close();
            return d;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void updateDistance(int id, int fromLieu, int toLieu, double distanceKm) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "UPDATE distance SET from_lieu = ?, to_lieu = ?, distance_km = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fromLieu);
            pstmt.setInt(2, toLieu);
            pstmt.setDouble(3, distanceKm);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void deleteDistance(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM distance WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
}
