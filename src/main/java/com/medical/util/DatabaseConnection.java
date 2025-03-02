package com.medical.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Chargement du driver JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Paramètres de connexion
                String url = "jdbc:mysql://localhost:3306/medical_db?useSSL=false";
                String username = "root";
                String password = "";

                // Établir la connexion
                connection = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL introuvable", e);
            }
        }

        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Logger l'erreur
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
}