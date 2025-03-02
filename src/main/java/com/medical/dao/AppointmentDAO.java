// AppointmentDAO.java
package com.medical.dao;
import com.medical.model.User;

import com.medical.model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentDAO {

    private static final Logger LOGGER = Logger.getLogger(AppointmentDAO.class.getName());

    public boolean createAppointment(Appointment appointment) throws SQLException {
        String query = "INSERT INTO Appointments (patient_id, doctor_id, date, time, reason, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, appointment.getDate());
            stmt.setTime(4, appointment.getTime());
            stmt.setString(5, appointment.getReason());
            stmt.setString(6, appointment.getStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating appointment", e);
            throw e; // Re-throw the exception so the servlet can handle it
        }

        return false;
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.*, " +
                "p.username as patient_username, p.email as patient_email, p.phone as patient_phone, p.role as patient_role, " +
                "d.username as doctor_username, d.email as doctor_email, d.phone as doctor_phone, d.role as doctor_role " +
                "FROM Appointments a " +
                "JOIN Users p ON a.patient_id = p.id " +
                "JOIN Users d ON a.doctor_id = d.id " +
                "WHERE a.patient_id = ? " +
                "ORDER BY a.date, a.time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(extractAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting appointments by patient ID", e);
            throw e;
        }

        return appointments;
    }

    public List<Appointment> getAppointmentsByDoctorId(int doctorId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.*, " +
                "p.username as patient_username, p.email as patient_email, p.phone as patient_phone, p.role as patient_role, " +
                "d.username as doctor_username, d.email as doctor_email, d.phone as doctor_phone, d.role as doctor_role " +
                "FROM Appointments a " +
                "JOIN Users p ON a.patient_id = p.id " +
                "JOIN Users d ON a.doctor_id = d.id " +
                "WHERE a.doctor_id = ? " +
                "ORDER BY a.date, a.time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    appointments.add(extractAppointmentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting appointments by doctor ID", e);
            throw e;
        }

        return appointments;
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) throws SQLException {
        String query = "UPDATE Appointments SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating appointment status", e);
            throw e;
        }
    }

    public boolean deleteAppointment(int appointmentId) throws SQLException {
        String query = "DELETE FROM Appointments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting appointment", e);
            throw e;
        }
    }

    // AppointmentDAO.java (Corrected section)
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setPatientId(rs.getInt("patient_id"));
        appointment.setDoctorId(rs.getInt("doctor_id"));
        appointment.setDate(rs.getDate("date"));
        appointment.setTime(rs.getTime("time"));
        appointment.setReason(rs.getString("reason"));
        appointment.setStatus(rs.getString("status"));

        // Création des objets User pour l'affichage
        User patient = new User();
        patient.setId(rs.getInt("patient_id"));
        patient.setUsername(rs.getString("patient_username"));
        patient.setEmail(rs.getString("patient_email"));
        patient.setTelephone(rs.getString("patient_phone")); // Corrected method call
        patient.setRole(rs.getString("patient_role"));

        User doctor = new User();
        doctor.setId(rs.getInt("doctor_id"));
        doctor.setUsername(rs.getString("doctor_username"));
        doctor.setEmail(rs.getString("doctor_email"));
        doctor.setTelephone(rs.getString("doctor_phone")); // Corrected method call
        doctor.setRole(rs.getString("doctor_role"));

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        return appointment;
    }}