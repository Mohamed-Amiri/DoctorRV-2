// PatientAppointmentServlet.java
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
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/patient/appointments")
public class PatientAppointmentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PatientAppointmentServlet.class.getName());
    private static final String PATIENT_ROLE = "PATIENT";
    private static final String APPOINTMENT_STATUS_SCHEDULED = "SCHEDULED";
    private static final String APPOINTMENT_STATUS_CANCELLED = "CANCELLED";
    private static final String APPOINTMENTS_JSP = "/WEB-INF/patient/appointments.jsp";
    private static final String LOGIN_PATH = "/login";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null || !PATIENT_ROLE.equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return;
        }

        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();
            UserDAO userDAO = new UserDAO();

            // Liste des rendez-vous du patient
            List<Appointment> appointments = appointmentDAO.getAppointmentsByPatientId(user.getId());
            request.setAttribute("appointments", appointments);

            // Liste des médecins pour le formulaire de prise de rendez-vous
            List<User> doctors = userDAO.getAllDoctors();
            request.setAttribute("doctors", doctors);

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

        if (user == null || !PATIENT_ROLE.equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + LOGIN_PATH);
            return;
        }

        String action = request.getParameter("action");

        try {
            AppointmentDAO appointmentDAO = new AppointmentDAO();

            if ("create".equals(action)) {
                // Création d'un nouveau rendez-vous
                String doctorIdParam = request.getParameter("doctorId");
                String dateStr = request.getParameter("date");
                String timeStr = request.getParameter("time");
                String reason = request.getParameter("reason");

                if (doctorIdParam != null && !doctorIdParam.isEmpty() && dateStr != null && !dateStr.isEmpty() && timeStr != null && !timeStr.isEmpty() && reason != null && !reason.isEmpty()) {
                    int doctorId = Integer.parseInt(doctorIdParam);

                    Appointment appointment = new Appointment();
                    appointment.setPatientId(user.getId());
                    appointment.setDoctorId(doctorId);
                    appointment.setDate(Date.valueOf(dateStr));
                    appointment.setTime(Time.valueOf(timeStr + ":00"));
                    appointment.setReason(reason);
                    appointment.setStatus(APPOINTMENT_STATUS_SCHEDULED);

                    boolean created = appointmentDAO.createAppointment(appointment);

                    if (created) {
                        request.setAttribute("success", "Rendez-vous créé avec succès!");
                    } else {
                        request.setAttribute("error", "Erreur lors de la création du rendez-vous.");
                    }
                } else {
                    request.setAttribute("error", "Missing or invalid parameters for creating an appointment.");
                }

            } else if ("cancel".equals(action)) {
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
            response.sendRedirect(request.getContextPath() + "/patient/appointments");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing request", e);
            request.setAttribute("error", "Erreur lors du traitement de la demande.");
            doGet(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid format for doctorId or appointmentId.");
            doGet(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Invalid date or time format.");
            doGet(request, response);
        }
    }
}