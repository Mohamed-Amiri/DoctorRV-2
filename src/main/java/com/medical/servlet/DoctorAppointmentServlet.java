// DoctorAppointmentServlet.java
package com.medical.servlet;

import com.medical.dao.AppointmentDAO;
import com.medical.model.Appointment;
import com.medical.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/doctor/appointments")
public class DoctorAppointmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"MEDECIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();

            // Liste des rendez-vous du médecin
            List<Appointment> appointments = appointmentDAO.getAppointmentsByDoctorId(user.getId());
            request.setAttribute("appointments", appointments);

            request.getRequestDispatcher("/WEB-INF/doctor/appointments.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la récupération des rendez-vous: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/doctor/appointments.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !"MEDECIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();

            if ("cancel".equals(action)) {
                // Annulation d'un rendez-vous
                String appointmentIdParam = request.getParameter("appointmentId");
                if (appointmentIdParam != null && !appointmentIdParam.isEmpty()) {
                    int appointmentId = Integer.parseInt(appointmentIdParam);
                    boolean updated = appointmentDAO.updateAppointmentStatus(appointmentId, "CANCELLED");

                    if (updated) {
                        request.setAttribute("success", "Rendez-vous annulé avec succès!");
                    } else {
                        request.setAttribute("error", "Erreur lors de l'annulation du rendez-vous.");
                    }
                } else {
                    request.setAttribute("error", "Appointment ID is missing or invalid.");
                }
            }

            // Rediriger vers la page des rendez-vous avec un message
            response.sendRedirect(request.getContextPath() + "/doctor/appointments");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du traitement de la demande: " + e.getMessage());
            doGet(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID format.");
            doGet(request, response);
        }
    }
}