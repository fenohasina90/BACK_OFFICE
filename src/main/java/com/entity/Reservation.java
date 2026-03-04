package main.java.com.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {
    private int id;
    private LocalDate dateReservation;
    private LocalTime heureReservation;
    private int nbPersonnes;
    private String idClient;
    private int idHotel;

    // Pour afficher les détails
    private Client client;
    private Hotel hotel;

    public Reservation() {
    }

    public Reservation(int id, LocalDate dateReservation, int nbPersonnes, String idClient, int idHotel) {
        this(id, dateReservation, null, nbPersonnes, idClient, idHotel);
    }

    public Reservation(int id, LocalDate dateReservation, LocalTime heureReservation, int nbPersonnes, String idClient, int idHotel) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.heureReservation = heureReservation;
        this.nbPersonnes = nbPersonnes;
        this.idClient = idClient;
        this.idHotel = idHotel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdReservation() {
        return getId();
    }

    public void setIdReservation(int idReservation) {
        setId(idReservation);
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public LocalTime getHeureReservation() {
        return heureReservation;
    }

    public void setHeureReservation(LocalTime heureReservation) {
        this.heureReservation = heureReservation;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(int nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", dateReservation=" + dateReservation +
                ", heureReservation=" + heureReservation +
                ", nbPersonnes=" + nbPersonnes +
                ", idClient='" + idClient + '\'' +
                ", idHotel=" + idHotel +
                '}';
    }
}
