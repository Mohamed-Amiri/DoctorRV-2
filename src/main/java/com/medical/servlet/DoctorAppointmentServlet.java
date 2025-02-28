// DoctorAppointmentServlet.java
package com.medical.servlet;

import com.medical.dao.AppointmentDAO;
import com.medical.model.Appointment;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/doctor/appointments")
public class DoctorAppointmentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DoctorAppointmentServlet.class.getName());
    private static final String DOCTOR_ROLE = "MEDECIN";
    private static final String APPOINTMENT_STATUS_CANCELLED = "CANCELLED";
    private static final String APPOINTMENTS_JSP = "/WEB-INF/doctor/appointments.jsp";
    private static final String LOGIN_PATH = "/login";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !DOCTOR_ROLE.equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return;
        }

        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();

            // Liste des rendez-vous du médecin
            List<Appointment> appointments = appointmentDAO.getAppointmentsByDoctorId(user.getId());
            request.setAttribute("appointments", appointments);

            request.getRequestDispatcher(APPOINTMENTS_JSP).forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving appointments", e);
            request.setAttribute("error", "Erreur lors de la récupération des rendez-vous.");
            request.getRequestDispatcher(APPOINTMENTS_JSP).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !DOCTOR_ROLE.equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
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
                    boolean updated = appointmentDAO.updateAppointmentStatus(appointmentId, APPOINTMENT_STATUS_CANCELLED);

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
            LOGGER.log(Level.SEVERE, "Error processing request", e);
            request.setAttribute("error", "Erreur lors du traitement de la demande.");
            doGet(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid appointment ID format.");
            doGet(request, response);
        }
    }
}