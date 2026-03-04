package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Token;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TokenService {

    public Token getTokenByReference(String reference) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM token WHERE reference = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reference);
            ResultSet rs = pstmt.executeQuery();
            
            Token token = null;
            if (rs.next()) {
                token = new Token(
                    rs.getInt("id"),
                    rs.getString("reference"),
                    rs.getTimestamp("date_expiration").toLocalDateTime()
                );
            }
            
            rs.close();
            pstmt.close();
            return token;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public boolean isTokenValid(String reference) throws SQLException {
        if (reference == null || reference.trim().isEmpty()) {
            return false;
        }
        
        Token token = getTokenByReference(reference);
        return token != null && token.isValid();
    }

    public List<Token> getAllTokens() throws SQLException {
        List<Token> tokens = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM token ORDER BY date_expiration DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Token token = new Token(
                    rs.getInt("id"),
                    rs.getString("reference"),
                    rs.getTimestamp("date_expiration").toLocalDateTime()
                );
                tokens.add(token);
            }
            
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return tokens;
    }

    public Token createToken(String reference, LocalDateTime dateExpiration) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO token (reference, date_expiration) VALUES (?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, reference);
            pstmt.setTimestamp(2, Timestamp.valueOf(dateExpiration));
            
            ResultSet rs = pstmt.executeQuery();
            Token token = new Token();
            if (rs.next()) {
                token.setId(rs.getInt("id"));
            }
            token.setReference(reference);
            token.setDateExpiration(dateExpiration);
            
            rs.close();
            pstmt.close();
            return token;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public void deleteToken(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM token WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
}
