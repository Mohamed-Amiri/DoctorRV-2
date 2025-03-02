//PatientDAO.java
package com.medical.dao;

import com.medical.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    private final Connection connection;

    public PatientDAO(Connection connection) {
        this.connection = connection;
    }

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
                    patient.setUsername(rs.getString("Nom_utilisateur"));
                    patient.setEmail(rs.getString("Email"));
                    patient.setMot_de_passe(rs.getString("Mot_de_passe"));
                    patient.setRole(rs.getString("Role"));
                    patient.setTelephone(rs.getString("telephone"));
                    patient.setNumeroAssurance(rs.getString("numero_assurance"));
                    patient.setGroupeSanguin(rs.getString("groupe_sanguin"));
                    patient.setAntecedentsMedicaux(rs.getString("antecedents_medicaux"));

                    return patient;
                }
                return null;
            }
        }
    }

    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT u.*, p.numero_assurance, p.groupe_sanguin, p.antecedents_medicaux " +
                "FROM users u JOIN patients p ON u.id = p.user_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Patient patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setUsername(rs.getString("Nom_utilisateur"));
                patient.setEmail(rs.getString("Email"));
                patient.setMot_de_passe(rs.getString("Mot_de_passe"));
                patient.setRole(rs.getString("Role"));
                patient.setTelephone(rs.getString("telephone"));
                patient.setNumeroAssurance(rs.getString("numero_assurance"));
                patient.setGroupeSanguin(rs.getString("groupe_sanguin"));
                patient.setAntecedentsMedicaux(rs.getString("antecedents_medicaux"));
                patients.add(patient);
            }
        }
        return patients;
    }

    public boolean ajouterPatient(Patient patient) throws SQLException {
        String sqlUser = "INSERT INTO users (Nom_utilisateur, Email, Mot_de_passe, Role, telephone) VALUES (?, ?, ?, ?, ?)";
        String sqlPatient = "INSERT INTO patients (user_id, numero_assurance, groupe_sanguin, antecedents_medicaux) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            // Insert into users table
            try (PreparedStatement pstmtUser = connection.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmtUser.setString(1, patient.getUsername());
                pstmtUser.setString(2, patient.getEmail());
                pstmtUser.setString(3, patient.getMot_de_passe());
                pstmtUser.setString(4, "patient");
                pstmtUser.setString(5, patient.getTelephone());
                pstmtUser.executeUpdate();

                // Get the generated user ID
                try (ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // Insert into patients table
            try (PreparedStatement pstmtPatient = connection.prepareStatement(sqlPatient)) {
                pstmtPatient.setInt(1, patient.getId());
                pstmtPatient.setString(2, patient.getNumeroAssurance());
                pstmtPatient.setString(3, patient.getGroupeSanguin());
                pstmtPatient.setString(4, patient.getAntecedentsMedicaux());
                pstmtPatient.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean mettreAJourPatient(Patient patient) throws SQLException {
        String sqlUser = "UPDATE users SET Nom_utilisateur = ?, Email = ?, Mot_de_passe = ?, telephone = ? WHERE id = ?";
        String sqlPatient = "UPDATE patients SET numero_assurance = ?, groupe_sanguin = ?, antecedents_medicaux = ? WHERE user_id = ?";

        try {
            connection.setAutoCommit(false);

            // Update users table
            try (PreparedStatement pstmtUser = connection.prepareStatement(sqlUser)) {
                pstmtUser.setString(1, patient.getUsername());
                pstmtUser.setString(2, patient.getEmail());
                pstmtUser.setString(3, patient.getMot_de_passe());
                pstmtUser.setString(4, patient.getTelephone());
                pstmtUser.setInt(5, patient.getId());
                pstmtUser.executeUpdate();
            }

            // Update patients table
            try (PreparedStatement pstmtPatient = connection.prepareStatement(sqlPatient)) {
                pstmtPatient.setString(1, patient.getNumeroAssurance());
                pstmtPatient.setString(2, patient.getGroupeSanguin());
                pstmtPatient.setString(3, patient.getAntecedentsMedicaux());
                pstmtPatient.setInt(4, patient.getId());
                pstmtPatient.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }

    public boolean supprimerPatient(int id) throws SQLException {
        String sqlPatient = "DELETE FROM patients WHERE user_id = ?";
        String sqlUser = "DELETE FROM users WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            // Delete from patients table
            try (PreparedStatement pstmtPatient = connection.prepareStatement(sqlPatient)) {
                pstmtPatient.setInt(1, id);
                pstmtPatient.executeUpdate();
            }

            // Delete from users table
            try (PreparedStatement pstmtUser = connection.prepareStatement(sqlUser)) {
                pstmtUser.setInt(1, id);
                pstmtUser.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        }
    }
}