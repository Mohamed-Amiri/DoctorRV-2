// Patient.java
package com.medical.model;

public class Patient extends User {
    private String numeroAssurance;
    private String groupeSanguin;
    private String antecedentsMedicaux;

    // Constructeur complet
    public Patient(int id, String nom_utilisateur, String email, String mot_de_passe,
                   String telephone, String numeroAssurance, String groupeSanguin,
                   String antecedentsMedicaux) {
        super(id, nom_utilisateur, email, mot_de_passe, "patient", telephone);
        this.numeroAssurance = numeroAssurance;
        this.groupeSanguin = groupeSanguin;
        this.antecedentsMedicaux = antecedentsMedicaux;
    }

    // Constructeur minimal
    public Patient() {
        super();
        setRole("patient");
    }

    // Getters et Setters
    public String getNumeroAssurance() {
        return numeroAssurance;
    }

    public void setNumeroAssurance(String numeroAssurance) {
        this.numeroAssurance = numeroAssurance;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public String getAntecedentsMedicaux() {
        return antecedentsMedicaux;
    }

    public void setAntecedentsMedicaux(String antecedentsMedicaux) {
        this.antecedentsMedicaux = antecedentsMedicaux;
    }
}