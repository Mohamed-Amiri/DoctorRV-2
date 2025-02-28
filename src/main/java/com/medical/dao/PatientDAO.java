package com.medical.dao;

import com.medical.model.Patient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    private Connection connection;

    // Constructeur
    public PatientDAO(Connection connection) {
        this.connection = connection;
    }

    // Ajouter un patient
    public boolean ajouterPatient(Patient patient) throws SQLException {
        String sql = "INSERT INTO users (Nom_utilisateur, Email, Mot_de_passe, Role, Téléphone) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, patient.getNom_utilisateur());
            pstmt.setString(2, patient.getEmail());
            pstmt.setString(3, patient.getMot_de_passe());
            pstmt.setString(4, patient.getRole());
            pstmt.setString(5, patient.getTéléphone());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // Insérer les données spécifiques au patient
                    String patientSql = "INSERT INTO patients (user_id, numero_assurance, groupe_sanguin, antecedents_medicaux) " +
                            "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement patientStmt = connection.prepareStatement(patientSql)) {
                        patientStmt.setInt(1, userId);
                        patientStmt.setString(2, patient.getNumeroAssurance());
                        patientStmt.setString(3, patient.getGroupeSanguin());
                        patientStmt.setString(4, patient.getAntecedentsMedicaux());

                        return patientStmt.executeUpdate() > 0;
                    }
                }
                return false;
            }
        }
    }

    // Mettre à jour un patient
    public boolean mettreAJourPatient(Patient patient) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Mettre à jour les informations de base de l'utilisateur
            String userSql = "UPDATE users SET Nom_utilisateur = ?, Email = ?, Mot_de_passe = ?, Téléphone = ? " +
                    "WHERE id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(userSql)) {
                pstmt.setString(1, patient.getNom_utilisateur());
                pstmt.setString(2, patient.getEmail());
                pstmt.setString(3, patient.getMot_de_passe());
                pstmt.setString(4, patient.getTéléphone());
                pstmt.setInt(5, patient.getId());

                pstmt.executeUpdate();
            }

            // Mettre à jour les informations spécifiques au patient
            String patientSql = "UPDATE patients SET numero_assurance = ?, groupe_sanguin = ?, antecedents_medicaux = ? " +
                    "WHERE user_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(patientSql)) {
                pstmt.setString(1, patient.getNumeroAssurance());
                pstmt.setString(2, patient.getGroupeSanguin());
                pstmt.setString(3, patient.getAntecedentsMedicaux());
                pstmt.setInt(4, patient.getId());

                pstmt.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Supprimer un patient
    public boolean supprimerPatient(int id) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Supprimer d'abord les données spécifiques au patient
            String patientSql = "DELETE FROM patients WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(patientSql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // Puis supprimer l'utilisateur
            String userSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(userSql)) {
                pstmt.setInt(1, id);
                int affectedRows = pstmt.executeUpdate();

                connection.commit();
                return affectedRows > 0;
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Obtenir un patient par ID
    public Patient getPatientById(int id) throws SQLException {
        String sql = "SELECT u.*, p.numero_assurance, p.groupe_sanguin, p.antecedents_medicaux " +
                "FROM users u JOIN patients p ON u.id = p.user_id " +
                "WHERE u.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Patient patient = new Patient();
                    patient.setId(rs.getInt("id"));
                    patient.setNom_utilisateur(rs.getString("Nom_utilisateur"));
                    patient.setEmail(rs.getString("Email"));
                    patient.setMot_de_passe(rs.getString("Mot_de_passe"));
                    patient.setRole(rs.getString("Role"));
                    patient.setTéléphone(rs.getString("Téléphone"));
                    patient.setNumeroAssurance(rs.getString("numero_assurance"));
                    patient.setGroupeSanguin(rs.getString("groupe_sanguin"));
                    patient.setAntecedentsMedicaux(rs.getString("antecedents_medicaux"));

                    return patient;
                }
                return null;
            }
        }
    }

    // Obtenir tous les patients
    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();

        String sql = "SELECT u.*, p.numero_assurance, p.groupe_sanguin, p.antecedents_medicaux " +
                "FROM users u JOIN patients p ON u.id = p.user_id " +
                "WHERE u.Role = 'patient'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setNom_utilisateur(rs.getString("Nom_utilisateur"));
                patient.setEmail(rs.getString("Email"));
                patient.setMot_de_passe(rs.getString("Mot_de_passe"));
                patient.setRole(rs.getString("Role"));
                patient.setTéléphone(rs.getString("Téléphone"));
                patient.setNumeroAssurance(rs.getString("numero_assurance"));
                patient.setGroupeSanguin(rs.getString("groupe_sanguin"));
                patient.setAntecedentsMedicaux(rs.getString("antecedents_medicaux"));

                patients.add(patient);
            }
        }

        return patients;
    }
}
