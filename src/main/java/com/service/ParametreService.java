package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Parametre;

import java.sql.*;

public class ParametreService {

    public Parametre getParametreActif() throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM parametre ORDER BY id DESC LIMIT 1";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Parametre p = null;
            if (rs.next()) {
                p = new Parametre(
                        rs.getInt("id"),
                        rs.getDouble("vitesse_moyenne_kmh"),
                        rs.getInt("temps_attente_min")
                );
            }
            rs.close();
            stmt.close();
            if (p == null) {
                throw new SQLException("Aucun parametre trouvé");
            }
            return p;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }
}
