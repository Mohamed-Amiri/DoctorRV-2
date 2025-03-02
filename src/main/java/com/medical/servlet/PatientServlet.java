// PatientServlet.java
package com.medical.servlet;

import com.medical.dao.PatientDAO;
import com.medical.model.Patient;
import com.medical.dao.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/patients/*")
public class PatientServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PatientServlet.class.getName());
    private PatientDAO patientDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection connection = DatabaseConnection.getConnection();
            patientDAO = new PatientDAO(connection);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing PatientDAO", e);
            throw new ServletException("Error initializing PatientDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Liste tous les patients
                List<Patient> patients = patientDAO.getAllPatients();
                request.setAttribute("patients", patients);
                request.getRequestDispatcher("/WEB-INF/views/patient/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                // Formulaire de création d'un nouveau patient
                request.getRequestDispatcher("/WEB-INF/views/patient/form.jsp").forward(request, response);
            } else if (pathInfo.equals("/edit")) {
                // Formulaire d'édition d'un patient existant
                int id = Integer.parseInt(request.getParameter("id"));
                Patient patient = patientDAO.getPatientById(id);

                if (patient != null) {
                    request.setAttribute("patient", patient);
                    request.getRequestDispatcher("/WEB-INF/views/patient/form.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Patient non trouvé");
                }
            } else if (pathInfo.equals("/view")) {
                // Affiche les détails d'un patient
                int id = Integer.parseInt(request.getParameter("id"));
                Patient patient = patientDAO.getPatientById(id);

                if (patient != null) {
                    request.setAttribute("patient", patient);
                    request.getRequestDispatcher("/WEB-INF/views/patient/view.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Patient non trouvé");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error accessing patient data", e);
            throw new ServletException("Error accessing patient data", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                // Créer un nouveau patient
                Patient patient = new Patient();

                // Récupérer les données du formulaire
                patient.setUsername(request.getParameter("nom"));
                patient.setEmail(request.getParameter("email"));
                patient.setMot_de_passe(request.getParameter("motDePasse"));
                patient.setTelephone(request.getParameter("telephone"));
                patient.setNumeroAssurance(request.getParameter("numeroAssurance"));
                patient.setGroupeSanguin(request.getParameter("groupeSanguin"));
                patient.setAntecedentsMedicaux(request.getParameter("antecedentsMedicaux"));

                if (patientDAO.ajouterPatient(patient)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("message", "Patient créé avec succès");
                    response.sendRedirect(request.getContextPath() + "/patients/");
                } else {
                    request.setAttribute("error", "Erreur lors de la création du patient");
                    request.setAttribute("patient", patient);
                    request.getRequestDispatcher("/WEB-INF/views/patient/form.jsp").forward(request, response);
                }
            } else if ("update".equals(action)) {
                // Mettre à jour un patient existant
                int id = Integer.parseInt(request.getParameter("id"));
                Patient patient = patientDAO.getPatientById(id);

                if (patient != null) {
                    // Mettre à jour les données du patient
                    patient.setUsername(request.getParameter("nom"));
                    patient.setEmail(request.getParameter("email"));
                    patient.setTelephone(request.getParameter("telephone"));
                    patient.setNumeroAssurance(request.getParameter("numeroAssurance"));
                    patient.setGroupeSanguin(request.getParameter("groupeSanguin"));
                    patient.setAntecedentsMedicaux(request.getParameter("antecedentsMedicaux"));

                    // Mettre à jour le mot de passe uniquement s'il est fourni
                    String nouveauMotDePasse = request.getParameter("motDePasse");
                    if (nouveauMotDePasse != null && !nouveauMotDePasse.trim().isEmpty()) {
                        patient.setMot_de_passe(nouveauMotDePasse);
                    }

                    if (patientDAO.mettreAJourPatient(patient)) {
                        HttpSession session = request.getSession();
                        session.setAttribute("message", "Patient mis à jour avec succès");
                        response.sendRedirect(request.getContextPath() + "/patients/");
                    } else {
                        request.setAttribute("error", "Erreur lors de la mise à jour du patient");
                        request.setAttribute("patient", patient);
                        request.getRequestDispatcher("/WEB-INF/views/patient/form.jsp").forward(request, response);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Patient non trouvé");
                }
            } else if ("delete".equals(action)) {
                // Supprimer un patient
                int id = Integer.parseInt(request.getParameter("id"));

                if (patientDAO.supprimerPatient(id)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("message", "Patient supprimé avec succès");
                } else {
                    HttpSession session = request.getSession();
                    session.setAttribute("error", "Erreur lors de la suppression du patient");
                }

                response.sendRedirect(request.getContextPath() + "/patients/");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non valide");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing patient data", e);
            throw new ServletException("Error processing patient data", e);
        }
    }
}