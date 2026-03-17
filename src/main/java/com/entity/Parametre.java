package main.java.com.entity;

public class Parametre {
    private int id;
    private double vitesseMoyenneKmh;
    private int tempsAttenteMin;

    public Parametre() {
    }

    public Parametre(int id, double vitesseMoyenneKmh, int tempsAttenteMin) {
        this.id = id;
        this.vitesseMoyenneKmh = vitesseMoyenneKmh;
        this.tempsAttenteMin = tempsAttenteMin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getVitesseMoyenneKmh() {
        return vitesseMoyenneKmh;
    }

    public void setVitesseMoyenneKmh(double vitesseMoyenneKmh) {
        this.vitesseMoyenneKmh = vitesseMoyenneKmh;
    }

    public int getTempsAttenteMin() {
        return tempsAttenteMin;
    }

    public void setTempsAttenteMin(int tempsAttenteMin) {
        this.tempsAttenteMin = tempsAttenteMin;
    }
}
