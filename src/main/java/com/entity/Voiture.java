package main.java.com.entity;

public class Voiture {
    private int id;
    private String typeCarburant;
    private int nbPlace;

    public Voiture() {
    }

    public Voiture(int id, String typeCarburant, int nbPlace) {
        this.id = id;
        this.typeCarburant = typeCarburant;
        this.nbPlace = nbPlace;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVoiture() {
        return getId();
    }

    public void setIdVoiture(int idVoiture) {
        setId(idVoiture);
    }

    public String getTypeCarburant() {
        return typeCarburant;
    }

    public void setTypeCarburant(String typeCarburant) {
        this.typeCarburant = typeCarburant;
    }

    public int getNbPlace() {
        return nbPlace;
    }

    public void setNbPlace(int nbPlace) {
        this.nbPlace = nbPlace;
    }

    public String getTypeCarburantLibelle() {
        switch (typeCarburant) {
            case "E": return "Essence";
            case "D": return "Diesel";
            case "El": return "Électrique";
            case "H": return "Hybride";
            default: return typeCarburant;
        }
    }

    @Override
    public String toString() {
        return "Voiture{" +
                "id=" + id +
                ", typeCarburant='" + typeCarburant + '\'' +
                ", nbPlace=" + nbPlace +
                '}';
    }
}
