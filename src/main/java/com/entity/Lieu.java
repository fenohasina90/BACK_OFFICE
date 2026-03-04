package main.java.com.entity;

public class Lieu {
    private int id;
    private String lieu;

    public Lieu() {
    }

    public Lieu(int id, String lieu) {
        this.id = id;
        this.lieu = lieu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    @Override
    public String toString() {
        return "Lieu{" +
                "id=" + id +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}
