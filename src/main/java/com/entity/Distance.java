package main.java.com.entity;

public class Distance {
    private int id;
    private int fromLieu;
    private int toLieu;
    private double distanceKm;

    private String fromLieuLabel;
    private String toLieuLabel;

    public Distance() {
    }

    public Distance(int id, int fromLieu, int toLieu, double distanceKm) {
        this.id = id;
        this.fromLieu = fromLieu;
        this.toLieu = toLieu;
        this.distanceKm = distanceKm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFromLieu() {
        return fromLieu;
    }

    public void setFromLieu(int fromLieu) {
        this.fromLieu = fromLieu;
    }

    public int getToLieu() {
        return toLieu;
    }

    public void setToLieu(int toLieu) {
        this.toLieu = toLieu;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getFromLieuLabel() {
        return fromLieuLabel;
    }

    public void setFromLieuLabel(String fromLieuLabel) {
        this.fromLieuLabel = fromLieuLabel;
    }

    public String getToLieuLabel() {
        return toLieuLabel;
    }

    public void setToLieuLabel(String toLieuLabel) {
        this.toLieuLabel = toLieuLabel;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "id=" + id +
                ", fromLieu=" + fromLieu +
                ", toLieu=" + toLieu +
                ", distanceKm=" + distanceKm +
                '}';
    }
}
