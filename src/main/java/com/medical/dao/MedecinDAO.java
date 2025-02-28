package com.medical.dao;

import com.medical.model.Medecin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinDAO {
    private Connection connection;

    // Constructeur
    public MedecinDAO(Connection connection) {
        this.connection = connection;
    }

    // Ajouter un médecin
    public boolean ajouterMedecin(Medecin medecin) throws SQLException {
        String sql = "INSERT INTO users (Nom_utilisateur, Email, Mot_de_passe, Role, Téléphone) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, medecin.getNom_utilisateur());
            pstmt.setString(2, medecin.getEmail());
            pstmt.setString(3, medecin.getMot_de_passe());
            pstmt.setString(4, medecin.getRole());
            pstmt.setString(5, medecin.getTéléphone());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // Insérer les données spécifiques au médecin
                    String medecinSql = "INSERT INTO medecins (user_id, specialite, numero_professionnel, disponibilite) " +
                            "VALUES (?, ?, ?, ?)";

                    try (PreparedStatement medecinStmt = connection.prepareStatement(medecinSql)) {
                        medecinStmt.setInt(1, userId);
                        medecinStmt.setString(2, medecin.getSpecialite());
                        medecinStmt.setString(3, medecin.getNumeroProfessionnel());
                        medecinStmt.setString(4, medecin.getDisponibilite());

                        return medecinStmt.executeUpdate() > 0;
                    }
                }
                return false;
            }
        }
    }

    // Mettre à jour un médecin
    public boolean mettreAJourMedecin(Medecin medecin) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Mettre à jour les informations de base de l'utilisateur
            String userSql = "UPDATE users SET Nom_utilisateur = ?, Email = ?, Mot_de_passe = ?, Téléphone = ? " +
                    "WHERE id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(userSql)) {
                pstmt.setString(1, medecin.getNom_utilisateur());
                pstmt.setString(2, medecin.getEmail());
                pstmt.setString(3, medecin.getMot_de_passe());
                pstmt.setString(4, medecin.getTéléphone());
                pstmt.setInt(5, medecin.getId());

                pstmt.executeUpdate();
            }

            // Mettre à jour les informations spécifiques au médecin
            String medecinSql = "UPDATE medecins SET specialite = ?, numero_professionnel = ?, disponibilite = ? " +
                    "WHERE user_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(medecinSql)) {
                pstmt.setString(1, medecin.getSpecialite());
                pstmt.setString(2, medecin.getNumeroProfessionnel());
                pstmt.setString(3, medecin.getDisponibilite());
                pstmt.setInt(4, medecin.getId());

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

    // Supprimer un médecin
    public boolean supprimerMedecin(int id) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Supprimer d'abord les données spécifiques au médecin
            String medecinSql = "DELETE FROM medecins WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(medecinSql)) {
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

    // Obtenir un médecin par ID
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
                    medecin.setNom_utilisateur(rs.getString("Nom_utilisateur"));
                    medecin.setEmail(rs.getString("Email"));
                    medecin.setMot_de_passe(rs.getString("Mot_de_passe"));
                    medecin.setRole(rs.getString("Role"));
                    medecin.setTéléphone(rs.getString("Téléphone"));
                    medecin.setSpecialite(rs.getString("specialite"));
                    medecin.setNumeroProfessionnel(rs.getString("numero_professionnel"));
                    medecin.setDisponibilite(rs.getString("disponibilite"));

                    return medecin;
                }
                return null;
            }
        }
    }

    // Obtenir tous les médecins
    public List<Medecin> getAllMedecins() throws SQLException {
        List<Medecin> medecins = new ArrayList<>();

        String sql = "SELECT u.*, m.specialite, m.numero_professionnel, m.disponibilite " +
                "FROM users u JOIN medecins m ON u.id = m.user_id " +
                "WHERE u.Role = 'medecin'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Medecin medecin = new Medecin();
                medecin.setId(rs.getInt("id"));
                medecin.setNom_utilisateur(rs.getString("Nom_utilisateur"));
                medecin.setEmail(rs.getString("Email"));
                medecin.setMot_de_passe(rs.getString("Mot_de_passe"));
                medecin.setRole(rs.getString("Role"));
                medecin.setTéléphone(rs.getString("Téléphone"));
                medecin.setSpecialite(rs.getString("specialite"));
                medecin.setNumeroProfessionnel(rs.getString("numero_professionnel"));
                medecin.setDisponibilite(rs.getString("disponibilite"));

                medecins.add(medecin);
            }
        }

        return medecins;
    }

    // Rechercher des médecins par spécialité
    public List<Medecin> getMedecinsBySpecialite(String specialite) throws SQLException {
        List<Medecin> medecins = new ArrayList<>();

        String sql = "SELECT u.*, m.specialite, m.numero_professionnel, m.disponibilite " +
                "FROM users u JOIN medecins m ON u.id = m.user_id " +
                "WHERE u.Role = 'medecin' AND m.specialite = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, specialite);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Medecin medecin = new Medecin();
                    medecin.setId(rs.getInt("id"));
                    medecin.setNom_utilisateur(rs.getString("Nom_utilisateur"));
                    medecin.setEmail(rs.getString("Email"));
                    medecin.setMot_de_passe(rs.getString("Mot_de_passe"));
                    medecin.setRole(rs.getString("Role"));
                    medecin.setTéléphone(rs.getString("Téléphone"));
                    medecin.setSpecialite(rs.getString("specialite"));
                    medecin.setNumeroProfessionnel(rs.getString("numero_professionnel"));
                    medecin.setDisponibilite(rs.getString("disponibilite"));

                    medecins.add(medecin);
                }
            }
        }

        return medecins;
    }
}