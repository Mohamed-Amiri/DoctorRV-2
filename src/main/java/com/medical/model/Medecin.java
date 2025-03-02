// Medecin.java
package com.medical.model;

public class Medecin extends User {
    private String specialite;
    private String numeroProfessionnel;
    private String disponibilite;

    // Constructeur complet
    public Medecin(int id, String nom_utilisateur, String email, String mot_de_passe,
                   String telephone, String specialite, String numeroProfessionnel,
                   String disponibilite) {
        super(id, nom_utilisateur, email, mot_de_passe, "medecin", telephone);
        this.specialite = specialite;
        this.numeroProfessionnel = numeroProfessionnel;
        this.disponibilite = disponibilite;
    }

    // Constructeur minimal
    public Medecin() {
        super();
        setRole("medecin");
    }

    // Getters et Setters
    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getNumeroProfessionnel() {
        return numeroProfessionnel;
    }

    public void setNumeroProfessionnel(String numeroProfessionnel) {
        this.numeroProfessionnel = numeroProfessionnel;
    }

    public String getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(String disponibilite) {
        this.disponibilite = disponibilite;
    }
}