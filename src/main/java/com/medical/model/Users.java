package com.medical.model;

public class Users {
    private int id;
    private String Nom_utilisateur;
    private String Email;
    private String Mot_de_passe;
    private String Role;
    private String Téléphone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_utilisateur() {
        return Nom_utilisateur;
    }

    public void setNom_utilisateur(String nom_utilisateur) {
        Nom_utilisateur = nom_utilisateur;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMot_de_passe() {
        return Mot_de_passe;
    }

    public void setMot_de_passe(String mot_de_passe) {
        Mot_de_passe = mot_de_passe;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getTéléphone() {
        return Téléphone;
    }

    public void setTéléphone(String téléphone) {
        Téléphone = téléphone;
    }

    public Users(int id, String nom_utilisateur, String email, String mot_de_passe, String role, String téléphone) {
        this.id = id;
        Nom_utilisateur = nom_utilisateur;
        Email = email;
        Mot_de_passe = mot_de_passe;
        Role = role;
        Téléphone = téléphone;
    }
    public Users(String role) {
        Role = role;
    }
}
