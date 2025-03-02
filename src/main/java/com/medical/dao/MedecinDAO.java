//MedecinDAO.java
package com.medical.dao;

import com.medical.model.Medecin;
import com.medical.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {

    private final Connection connection;

    public MedecinDAO(Connection connection) {
        this.connection = connection;
    }

    public Medecin getMedecinById(int id) throws SQLException {
        String sql = "SELECT u.*, m.specialite, m.numero_professionnel, m.disponibilite " +
                "FROM users u JOIN medecins m ON u.id = m.user_id " +
                "WHERE u.id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Medecin medecin = new Medecin();
                    medecin.setId(rs.getInt("id"));
                    medecin.setUsername(rs.getString("Nom_utilisateur")); // Corrected setter
                    medecin.setEmail(rs.getString("Email"));
                    medecin.setMot_de_passe(rs.getString("Mot_de_passe"));
                    medecin.setRole(rs.getString("Role"));
                    medecin.setTelephone(rs.getString("telephone")); // Corrected setter
                    medecin.setSpecialite(rs.getString("specialite"));
                    medecin.setNumeroProfessionnel(rs.getString("numero_professionnel"));
                    medecin.setDisponibilite(rs.getString("disponibilite"));

                    return medecin;
                }
                return null;
            }
        }
    }

    public List<Medecin> getAllMedecins() throws SQLException {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT u.*, m.specialite, m.numero_professionnel, m.disponibilite " +
                "FROM users u JOIN medecins m ON u.id = m.user_id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Medecin medecin = new Medecin();
                medecin.setId(rs.getInt("id"));
                medecin.setUsername(rs.getString("Nom_utilisateur"));
                medecin.setEmail(rs.getString("Email"));
                medecin.setMot_de_passe(rs.getString("Mot_de_passe"));
                medecin.setRole(rs.getString("Role"));
                medecin.setTelephone(rs.getString("telephone"));
                medecin.setSpecialite(rs.getString("specialite"));
                medecin.setNumeroProfessionnel(rs.getString("numero_professionnel"));
                medecin.setDisponibilite(rs.getString("disponibilite"));
                medecins.add(medecin);
            }
        }
        return medecins;
    }

    public List<Medecin> getMedecinsBySpecialite(String specialite) throws SQLException {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT u.*, m.specialite, m.numero_professionnel, m.disponibilite " +
                "FROM users u JOIN medecins m ON u.id = m.user_id " +
                "WHERE m.specialite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, specialite);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medecin medecin = new Medecin();
                    medecin.setId(rs.getInt("id"));
                    medecin.setUsername(rs.getString("Nom_utilisateur"));
                    medecin.setEmail(rs.getString("Email"));
                    medecin.setMot_de_passe(rs.getString("Mot_de_passe"));
                    medecin.setRole(rs.getString("Role"));
                    medecin.setTelephone(rs.getString("telephone"));
                    medecin.setSpecialite(rs.getString("specialite"));
                    medecin.setNumeroProfessionnel(rs.getString("numero_professionnel"));
                    medecin.setDisponibilite(rs.getString("disponibilite"));
                    medecins.add(medecin);
                }
            }
        }
        return medecins;
    }

    public boolean ajouterMedecin(Medecin medecin) throws SQLException {
        String sqlUser = "INSERT INTO users (Nom_utilisateur, Email, Mot_de_passe, Role, telephone) VALUES (?, ?, ?, ?, ?)";
        String sqlMedecin = "INSERT INTO medecins (user_id, specialite, numero_professionnel, disponibilite) VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            // Insert into users table
            try (PreparedStatement pstmtUser = connection.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmtUser.setString(1, medecin.getUsername());
                pstmtUser.setString(2, medecin.getEmail());
                pstmtUser.setString(3, medecin.getMot_de_passe());
                pstmtUser.setString(4, "medecin");
                pstmtUser.setString(5, medecin.getTelephone());
                pstmtUser.executeUpdate();

                // Get the generated user ID
                try (ResultSet generatedKeys = pstmtUser.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medecin.setId(generatedKeys.getInt(1));
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // Insert into medecins table
            try (PreparedStatement pstmtMedecin = connection.prepareStatement(sqlMedecin)) {
                pstmtMedecin.setInt(1, medecin.getId());
                pstmtMedecin.setString(2, medecin.getSpecialite());
                pstmtMedecin.setString(3, medecin.getNumeroProfessionnel());
                pstmtMedecin.setString(4, medecin.getDisponibilite());
                pstmtMedecin.executeUpdate();
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

    public boolean mettreAJourMedecin(Medecin medecin) throws SQLException {
        String sqlUser = "UPDATE users SET Nom_utilisateur = ?, Email = ?, Mot_de_passe = ?, telephone = ? WHERE id = ?";
        String sqlMedecin = "UPDATE medecins SET specialite = ?, numero_professionnel = ?, disponibilite = ? WHERE user_id = ?";

        try {
            connection.setAutoCommit(false);

            // Update users table
            try (PreparedStatement pstmtUser = connection.prepareStatement(sqlUser)) {
                pstmtUser.setString(1, medecin.getUsername());
                pstmtUser.setString(2, medecin.getEmail());
                pstmtUser.setString(3, medecin.getMot_de_passe());
                pstmtUser.setString(4, medecin.getTelephone());
                pstmtUser.setInt(5, medecin.getId());
                pstmtUser.executeUpdate();
            }

            // Update medecins table
            try (PreparedStatement pstmtMedecin = connection.prepareStatement(sqlMedecin)) {
                pstmtMedecin.setString(1, medecin.getSpecialite());
                pstmtMedecin.setString(2, medecin.getNumeroProfessionnel());
                pstmtMedecin.setString(3, medecin.getDisponibilite());
                pstmtMedecin.setInt(4, medecin.getId());
                pstmtMedecin.executeUpdate();
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

    public boolean supprimerMedecin(int id) throws SQLException {
        String sqlMedecin = "DELETE FROM medecins WHERE user_id = ?";
        String sqlUser = "DELETE FROM users WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            // Delete from medecins table
            try (PreparedStatement pstmtMedecin = connection.prepareStatement(sqlMedecin)) {
                pstmtMedecin.setInt(1, id);
                pstmtMedecin.executeUpdate();
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