package com.medical.filter;

import com.medical.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/patient/*", "/doctor/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Vérifier si l'utilisateur est connecté
        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            // L'utilisateur n'est pas connecté, rediriger vers la page de connexion
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // Vérifier si l'utilisateur a accès à cette URL
        String requestURI = httpRequest.getRequestURI();

        if (requestURI.contains("/patient/") && !user.getRole().equals("PATIENT")) {
            // Un non-patient essaie d'accéder à une page patient
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        if (requestURI.contains("/doctor/") && !user.getRole().equals("MEDECIN")) {
            // Un non-médecin essaie d'accéder à une page médecin
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // L'utilisateur a l'autorisation, continuer
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Nettoyage des ressources
    }
}