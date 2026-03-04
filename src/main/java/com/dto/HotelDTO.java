package main.java.com.dto;

public class HotelDTO {
    private int id;
    private String nom;
    private String adresse;
    private String ville;
    private String pays;
    private int idLieu;
    private String lieuLabel;

    public HotelDTO() {
    }

    public HotelDTO(int id, String nom, String adresse, String ville, String pays, int idLieu) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.pays = pays;
        this.idLieu = idLieu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public int getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(int idLieu) {
        this.idLieu = idLieu;
    }

    public String getLieuLabel() {
        return lieuLabel;
    }

    public void setLieuLabel(String lieuLabel) {
        this.lieuLabel = lieuLabel;
    }
}
