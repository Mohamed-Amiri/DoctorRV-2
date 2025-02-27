package com.medical.filter;

import com.medical.dao.UserDAO;
import com.medical.model.User;
import java.sql.SQLException;

public class AuthService {

    private UserDAO userDAO;

    public AuthService() {
        userDAO = new UserDAO();
    }

    public User login(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);

        if (user != null && password.equals(user.getPassword())) {
            // Note: Dans un environnement de production, utilisez un hachage sécurisé pour les mots de passe
            return user;
        }

        return null;
    }

    public boolean register(User user) throws SQLException {
        // Vérifier si l'utilisateur existe déjà
        User existingUser = userDAO.getUserByUsername(user.getUsername());

        if (existingUser != null) {
            return false;
        }

        // Dans un environnement de production, hachez le mot de passe avant de l'enregistrer
        return userDAO.createUser(user);
    }
}
