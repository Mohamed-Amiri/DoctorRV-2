package com.medical.servlet;

import com.medical.model.User;
import com.medical.filter.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Afficher le formulaire d'inscription avec choix du rôle
        request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String role = request.getParameter("role");

        // Validation de base
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                role == null || (!role.equals("PATIENT") && !role.equals("MEDECIN"))) {

            request.setAttribute("error", "Tous les champs sont obligatoires");
            request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
            return;
        }

        // Vérifier que les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas");
            request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            // Créer un nouvel utilisateur
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // Dans un environnement de production, il faudrait hacher le mot de passe
            user.setEmail(email);
            user.setPhone(phone);
            user.setRole(role);

            AuthService authService = new AuthService();
            boolean registered = authService.register(user);

            if (registered) {
                // Rediriger vers la page de connexion avec un message de succès
                request.setAttribute("success", "Inscription réussie ! Veuillez vous connecter.");
                request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Ce nom d'utilisateur existe déjà");
                request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'inscription: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/auth/register.jsp").forward(request, response);
        }
    }
}
