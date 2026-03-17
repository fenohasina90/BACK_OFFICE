package main.java.com.entity;

public class VoyageStop {
    private int id;
    private int idVoyage;
    private int ordre;
    private int idReservation;
    private int idLieuDestination;
    private double distanceKm;

    private String lieuLabel;

    public VoyageStop() {
    }

    public VoyageStop(int id, int idVoyage, int ordre, int idReservation, int idLieuDestination, double distanceKm) {
        this.id = id;
        this.idVoyage = idVoyage;
        this.ordre = ordre;
        this.idReservation = idReservation;
        this.idLieuDestination = idLieuDestination;
        this.distanceKm = distanceKm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(int idVoyage) {
        this.idVoyage = idVoyage;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdLieuDestination() {
        return idLieuDestination;
    }

    public void setIdLieuDestination(int idLieuDestination) {
        this.idLieuDestination = idLieuDestination;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getLieuLabel() {
        return lieuLabel;
    }

    public void setLieuLabel(String lieuLabel) {
        this.lieuLabel = lieuLabel;
    }
}
