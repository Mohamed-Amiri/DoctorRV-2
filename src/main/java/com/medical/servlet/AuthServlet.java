import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());

    // *** Database Connection (INSECURE - DO NOT USE THIS IN PRODUCTION) ***
    private static final String URL = "jdbc:mysql://localhost:3306/medical_appointments?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL non trouvé", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            showLoginPage(request, response);
        } else if (pathInfo.equals("/register")) {
            showRegistrationPage(request, response);
        } else if (pathInfo.equals("/logout")) {
            logout(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth.jsp").forward(request, response);
    }

    private void showRegistrationPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("register", true);
        request.getRequestDispatcher("/WEB-INF/views/auth.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("login".equals(action)) {
            login(request, response);
        } else if ("register".equals(action)) {
            register(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, Role FROM users WHERE Nom_utilisateur = ? AND Mot_de_passe = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password); // INSECURE - Use a proper hashing algorithm
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("userRole", rs.getString("Role"));
                    response.sendRedirect(request.getContextPath() + "/"); // Redirect to home
                } else {
                    request.setAttribute("errorMessage", "Invalid credentials");
                    showLoginPage(request, response);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Login error", e);
            request.setAttribute("errorMessage", "Database error");
            showLoginPage(request, response);
        }
    }

    private void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String telephone = request.getParameter("telephone");

        //TODO : Add validation of parameters

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (Nom_utilisateur, Mot_de_passe, Email, Role, telephone) VALUES (?, ?, ?, ?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password); // INSECURE - Use a proper hashing algorithm
            stmt.setString(3, email);
            stmt.setString(4, "patient"); // Default role
            stmt.setString(5, telephone);

            stmt.executeUpdate();
            request.setAttribute("successMessage", "Registration successful. Please login.");
            showLoginPage(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Registration error", e);
            request.setAttribute("register", true);
            request.setAttribute("errorMessage", "Registration failed: " + e.getMessage());
            showRegistrationPage(request, response);

        }
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/auth/"); // Back to login page
    }
}