// User.java
package com.medical.model;

public class User {
    private int id;
    private String Nom_utilisateur;
    private String Email;
    private String Mot_de_passe;
    private String Role;
    private String telephone;

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.Nom_utilisateur = username;
    }

    public String getUsername() {
        return Nom_utilisateur;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public User(int id, String nom_utilisateur, String email, String mot_de_passe, String role, String telephone) {
        this.id = id;
        Nom_utilisateur = nom_utilisateur;
        Email = email;
        Mot_de_passe = mot_de_passe;
        Role = role;
        this.telephone = telephone;
    }

    public User(String role) {
        Role = role;
    }
}