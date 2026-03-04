package main.java.com.entity;

import java.time.LocalDateTime;

public class Token {
    private int id;
    private String reference;
    private LocalDateTime dateExpiration;

    public Token() {
    }

    public Token(int id, String reference, LocalDateTime dateExpiration) {
        this.id = id;
        this.reference = reference;
        this.dateExpiration = dateExpiration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public boolean isValid() {
        return dateExpiration != null && dateExpiration.isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", dateExpiration=" + dateExpiration +
                ", isValid=" + isValid() +
                '}';
    }
}
