package main.java.com.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

public class Voyage {
    private int id;
    private LocalDate dateVoyage;
    private LocalTime heureDepart;
    private int idVoiture;
    private int dureeMinutes;
    private Timestamp dateCreation;

    public Voyage() {
    }

    public Voyage(int id, LocalDate dateVoyage, LocalTime heureDepart, int idVoiture, int dureeMinutes, Timestamp dateCreation) {
        this.id = id;
        this.dateVoyage = dateVoyage;
        this.heureDepart = heureDepart;
        this.idVoiture = idVoiture;
        this.dureeMinutes = dureeMinutes;
        this.dateCreation = dateCreation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDateVoyage() {
        return dateVoyage;
    }

    public void setDateVoyage(LocalDate dateVoyage) {
        this.dateVoyage = dateVoyage;
    }

    public LocalTime getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(LocalTime heureDepart) {
        this.heureDepart = heureDepart;
    }

    public int getIdVoiture() {
        return idVoiture;
    }

    public void setIdVoiture(int idVoiture) {
        this.idVoiture = idVoiture;
    }

    public int getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(int dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
}
