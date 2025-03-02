package com.medical.servlet;

import com.medical.dao.MedecinDAO;
import com.medical.model.Medecin;
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

@WebServlet("/medecins/*")
public class MedecinServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(MedecinServlet.class.getName());
    private MedecinDAO medecinDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection connection = DatabaseConnection.getConnection();
            medecinDAO = new MedecinDAO(connection);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing MedecinDAO", e);
            throw new ServletException("Error initializing MedecinDAO", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Liste tous les médecins
                List<Medecin> medecins = medecinDAO.getAllMedecins();
                request.setAttribute("entity", "medecin");  // Set the entity
                request.setAttribute("medecins", medecins);
                request.getRequestDispatcher("/WEB-INF/views/medecin/list.jsp").forward(request, response);
            } else if (pathInfo.equals("/new")) {
                // Formulaire de création d'un nouveau médecin
                request.setAttribute("entity", "medecin"); // Set the entity
                request.setAttribute("action", "new");
                request.getRequestDispatcher("/WEB-INF/views/medecin/form.jsp").forward(request, response);
            } else if (pathInfo.equals("/edit")) {
                // Formulaire d'édition d'un médecin existant
                int id = Integer.parseInt(request.getParameter("id"));
                Medecin medecin = medecinDAO.getMedecinById(id);

                if (medecin != null) {
                    request.setAttribute("entity", "medecin"); // Set the entity
                    request.setAttribute("action", "edit");
                    request.setAttribute("medecin", medecin);
                    request.getRequestDispatcher("/WEB-INF/views/medecin/form.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Médecin non trouvé");
                }
            } else if (pathInfo.equals("/view")) {
                // Affiche les détails d'un médecin
                int id = Integer.parseInt(request.getParameter("id"));
                Medecin medecin = medecinDAO.getMedecinById(id);

                if (medecin != null) {
                    request.setAttribute("entity", "medecin"); // Set the entity
                    request.setAttribute("action", "view");
                    request.setAttribute("medecin", medecin);
                    request.getRequestDispatcher("/WEB-INF/views/medecin/view.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Médecin non trouvé");
                }
            } else if (pathInfo.equals("/search")) {
                // Recherche de médecins par spécialité
                String specialite = request.getParameter("specialite");
                List<Medecin> medecins;

                if (specialite != null && !specialite.trim().isEmpty()) {
                    medecins = medecinDAO.getMedecinsBySpecialite(specialite);
                    request.setAttribute("specialite", specialite);
                } else {
                    medecins = medecinDAO.getAllMedecins();
                }

                request.setAttribute("entity", "medecin"); // Set the entity
                request.setAttribute("action", "search");
                request.setAttribute("medecins", medecins);
                request.getRequestDispatcher("/WEB-INF/views/medecin/search.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error accessing doctor data", e);
            request.getSession().setAttribute("error", "An error occurred while accessing doctor data.");  // Set a user-friendly error
            response.sendRedirect(request.getContextPath() + "/medecins/"); // Redirect to the list page
            //OR
            //throw new ServletException("Error accessing doctor data", e); //Throw the exception if redirecting is not desired.
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                // Créer un nouveau médecin
                Medecin medecin = new Medecin();

                // Récupérer les données du formulaire
                medecin.setUsername(request.getParameter("nom"));
                medecin.setEmail(request.getParameter("email"));
                medecin.setMot_de_passe(request.getParameter("motDePasse"));
                medecin.setTelephone(request.getParameter("telephone"));
                medecin.setSpecialite(request.getParameter("specialite"));
                medecin.setNumeroProfessionnel(request.getParameter("numeroProfessionnel"));
                medecin.setDisponibilite(request.getParameter("disponibilite"));

                if (medecinDAO.ajouterMedecin(medecin)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("message", "Médecin créé avec succès");
                    response.sendRedirect(request.getContextPath() + "/medecins/");
                } else {
                    request.setAttribute("error", "Erreur lors de la création du médecin");
                    request.setAttribute("medecin", medecin);
                    request.getRequestDispatcher("/WEB-INF/views/medecin/form.jsp").forward(request, response);
                }
            } else if ("update".equals(action)) {
                // Mettre à jour un médecin existant
                int id = Integer.parseInt(request.getParameter("id"));
                Medecin medecin = medecinDAO.getMedecinById(id);

                if (medecin != null) {
                    // Mettre à jour les données du médecin
                    medecin.setUsername(request.getParameter("nom"));
                    medecin.setEmail(request.getParameter("email"));
                    medecin.setTelephone(request.getParameter("telephone"));
                    medecin.setSpecialite(request.getParameter("specialite"));
                    medecin.setNumeroProfessionnel(request.getParameter("numeroProfessionnel"));
                    medecin.setDisponibilite(request.getParameter("disponibilite"));

                    // Mettre à jour le mot de passe uniquement s'il est fourni
                    String nouveauMotDePasse = request.getParameter("motDePasse");
                    if (nouveauMotDePasse != null && !nouveauMotDePasse.trim().isEmpty()) {
                        medecin.setMot_de_passe(nouveauMotDePasse);
                    }

                    if (medecinDAO.mettreAJourMedecin(medecin)) {
                        HttpSession session = request.getSession();
                        session.setAttribute("message", "Médecin mis à jour avec succès");
                        response.sendRedirect(request.getContextPath() + "/medecins/");
                    } else {
                        request.setAttribute("error", "Erreur lors de la mise à jour du médecin");
                        request.setAttribute("medecin", medecin);
                        request.getRequestDispatcher("/WEB-INF/views/medecin/form.jsp").forward(request, response);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Médecin non trouvé");
                }
            } else if ("delete".equals(action)) {
                // Supprimer un médecin
                int id = Integer.parseInt(request.getParameter("id"));

                if (medecinDAO.supprimerMedecin(id)) {
                    HttpSession session = request.getSession();
                    session.setAttribute("message", "Médecin supprimé avec succès");
                } else {
                    HttpSession session = request.getSession();
                    session.setAttribute("error", "Erreur lors de la suppression du médecin");
                }

                response.sendRedirect(request.getContextPath() + "/medecins/");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action non valide");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing doctor data", e);
            request.getSession().setAttribute("error", "An error occurred while processing doctor data.");  // Set a user-friendly error
            response.sendRedirect(request.getContextPath() + "/medecins/"); // Redirect to the list page
        }
    }
}