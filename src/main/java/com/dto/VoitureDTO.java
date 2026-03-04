package main.java.com.dto;

public class VoitureDTO {
    private int id;
    private String typeCarburant;
    private String typeCarburantLibelle;
    private int nbPlace;

    public VoitureDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeCarburant() {
        return typeCarburant;
    }

    public void setTypeCarburant(String typeCarburant) {
        this.typeCarburant = typeCarburant;
    }

    public String getTypeCarburantLibelle() {
        return typeCarburantLibelle;
    }

    public void setTypeCarburantLibelle(String typeCarburantLibelle) {
        this.typeCarburantLibelle = typeCarburantLibelle;
    }

    public int getNbPlace() {
        return nbPlace;
    }

    public void setNbPlace(int nbPlace) {
        this.nbPlace = nbPlace;
    }
}
