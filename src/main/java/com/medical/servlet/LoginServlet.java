package com.medical.servlet;

import com.medical.model.User;
import com.medical.filter.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String PATIENT_ROLE = "PATIENT";
    private static final String DOCTOR_ROLE = "MEDECIN";
    private static final String PATIENT_APPOINTMENTS_PATH = "/WEB-INF/patient/appointments.jsp"; // Corrected Path
    private static final String DOCTOR_APPOINTMENTS_PATH = "/WEB-INF/doctor/appointments.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Vérifier si l'utilisateur est déjà connecté
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            // Rediriger vers la page appropriée selon le rôle
            redirectBasedOnRole(user, request, response);
            return;
        }

        // Afficher la page de connexion
        request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            AuthService authService = new AuthService();
            User user = authService.login(username, password);

            if (user != null) {
                // Stocker l'utilisateur dans la session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // Rediriger vers la page appropriée selon le rôle
                redirectBasedOnRole(user, request, response);
            } else {
                // Authentification échouée
                request.setAttribute("error", "Email incorrect");  //Fixed
                request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la connexion: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/auth/login.jsp").forward(request, response);
        }
    }

    private void redirectBasedOnRole(User user, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (DOCTOR_ROLE.equals(user.getRole())) {
            // Forward to the Doctor's Appointments JSP
            request.getRequestDispatcher(DOCTOR_APPOINTMENTS_PATH).forward(request, response);
        } else if (PATIENT_ROLE.equals(user.getRole())) {
            // Forward to the Patient's Appointments JSP
            request.getRequestDispatcher(PATIENT_APPOINTMENTS_PATH).forward(request, response);
        } else {
            // Rôle inconnu
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}