package main.java.com.service;

import main.java.com.database.DatabaseConnection;
import main.java.com.entity.Client;
import main.java.com.entity.Hotel;
import main.java.com.entity.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {

    public List<Client> getAllClients() throws SQLException {
        List<Client> clients = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM client ORDER BY nom, prenom";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Client client = new Client(
                    rs.getString("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email")
                );
                clients.add(client);
            }
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return clients;
    }

    public List<Hotel> getAllHotels() throws SQLException {
        List<Hotel> hotels = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM hotel ORDER BY nom";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Hotel hotel = new Hotel(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("adresse"),
                    rs.getString("ville"),
                    rs.getString("pays"),
                    rs.getInt("id_lieu")
                );
                hotels.add(hotel);
            }
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return hotels;
    }

    public Reservation createReservation(String idClient, int idHotel, LocalDate dateReservation, LocalTime heureReservation, int nbPersonnes) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO reservation (date_reservation, heure_reservation, nb_personnes, id_client, id_hotel) VALUES (?, ?, ?, ?, ?) RETURNING id";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(dateReservation));
            pstmt.setTime(2, Time.valueOf(heureReservation));
            pstmt.setInt(3, nbPersonnes);
            pstmt.setString(4, idClient);
            pstmt.setInt(5, idHotel);
            
            ResultSet rs = pstmt.executeQuery();
            Reservation reservation = new Reservation();
            if (rs.next()) {
                reservation.setId(rs.getInt("id"));
            }
            reservation.setDateReservation(dateReservation);
            reservation.setHeureReservation(heureReservation);
            reservation.setNbPersonnes(nbPersonnes);
            reservation.setIdClient(idClient);
            reservation.setIdHotel(idHotel);
            
            rs.close();
            pstmt.close();
            return reservation;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public Reservation getReservationById(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT r.*, c.nom as client_nom, c.prenom as client_prenom, c.email as client_email, " +
                        "h.nom as hotel_nom, h.adresse as hotel_adresse, h.ville as hotel_ville, h.pays as hotel_pays, h.id_lieu as hotel_id_lieu " +
                        "FROM reservation r " +
                        "JOIN client c ON r.id_client = c.id " +
                        "JOIN hotel h ON r.id_hotel = h.id " +
                        "WHERE r.id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            Reservation reservation = null;
            if (rs.next()) {
                reservation = new Reservation(
                    rs.getInt("id"),
                    rs.getDate("date_reservation").toLocalDate(),
                    rs.getTime("heure_reservation") != null ? rs.getTime("heure_reservation").toLocalTime() : null,
                    rs.getInt("nb_personnes"),
                    rs.getString("id_client"),
                    rs.getInt("id_hotel")
                );
                
                Client client = new Client(
                    rs.getString("id_client"),
                    rs.getString("client_nom"),
                    rs.getString("client_prenom"),
                    rs.getString("client_email")
                );
                reservation.setClient(client);
                
                Hotel hotel = new Hotel(
                    rs.getInt("id_hotel"),
                    rs.getString("hotel_nom"),
                    rs.getString("hotel_adresse"),
                    rs.getString("hotel_ville"),
                    rs.getString("hotel_pays"),
                    rs.getInt("hotel_id_lieu")
                );
                reservation.setHotel(hotel);
            }
            
            rs.close();
            pstmt.close();
            return reservation;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT r.*, c.nom as client_nom, c.prenom as client_prenom, c.email as client_email, " +
                        "h.nom as hotel_nom, h.adresse as hotel_adresse, h.ville as hotel_ville, h.pays as hotel_pays, h.id_lieu as hotel_id_lieu " +
                        "FROM reservation r " +
                        "JOIN client c ON r.id_client = c.id " +
                        "JOIN hotel h ON r.id_hotel = h.id " +
                        "ORDER BY r.date_reservation DESC, r.id DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Reservation reservation = new Reservation(
                    rs.getInt("id"),
                    rs.getDate("date_reservation").toLocalDate(),
                    rs.getTime("heure_reservation") != null ? rs.getTime("heure_reservation").toLocalTime() : null,
                    rs.getInt("nb_personnes"),
                    rs.getString("id_client"),
                    rs.getInt("id_hotel")
                );
                
                Client client = new Client(
                    rs.getString("id_client"),
                    rs.getString("client_nom"),
                    rs.getString("client_prenom"),
                    rs.getString("client_email")
                );
                reservation.setClient(client);
                
                Hotel hotel = new Hotel(
                    rs.getInt("id_hotel"),
                    rs.getString("hotel_nom"),
                    rs.getString("hotel_adresse"),
                    rs.getString("hotel_ville"),
                    rs.getString("hotel_pays"),
                    rs.getInt("hotel_id_lieu")
                );
                reservation.setHotel(hotel);
                
                reservations.add(reservation);
            }
            
            rs.close();
            stmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return reservations;
    }

    public List<Reservation> getReservationsByDate(LocalDate dateReservation) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT r.*, c.nom as client_nom, c.prenom as client_prenom, c.email as client_email, " +
                        "h.nom as hotel_nom, h.adresse as hotel_adresse, h.ville as hotel_ville, h.pays as hotel_pays, h.id_lieu as hotel_id_lieu " +
                        "FROM reservation r " +
                        "JOIN client c ON r.id_client = c.id " +
                        "JOIN hotel h ON r.id_hotel = h.id " +
                        "WHERE r.date_reservation = ? " +
                        "ORDER BY r.id DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, Date.valueOf(dateReservation));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Reservation reservation = new Reservation(
                    rs.getInt("id"),
                    rs.getDate("date_reservation").toLocalDate(),
                    rs.getTime("heure_reservation") != null ? rs.getTime("heure_reservation").toLocalTime() : null,
                    rs.getInt("nb_personnes"),
                    rs.getString("id_client"),
                    rs.getInt("id_hotel")
                );
                
                Client client = new Client(
                    rs.getString("id_client"),
                    rs.getString("client_nom"),
                    rs.getString("client_prenom"),
                    rs.getString("client_email")
                );
                reservation.setClient(client);
                
                Hotel hotel = new Hotel(
                    rs.getInt("id_hotel"),
                    rs.getString("hotel_nom"),
                    rs.getString("hotel_adresse"),
                    rs.getString("hotel_ville"),
                    rs.getString("hotel_pays"),
                    rs.getInt("hotel_id_lieu")
                );
                reservation.setHotel(hotel);
                
                reservations.add(reservation);
            }
            
            rs.close();
            pstmt.close();
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
        return reservations;
    }
}
