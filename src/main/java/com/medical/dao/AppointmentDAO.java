// AppointmentDAO.java
package com.medical.dao;

import com.medical.model.Appointment;
import com.medical.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

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
        }

        return false;
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.*, " +
                "p.username as patient_username, p.email as patient_email, p.phone as patient_phone, " +
                "d.username as doctor_username, d.email as doctor_email " +
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
        }

        return appointments;
    }

    public List<Appointment> getAppointmentsByDoctorId(int doctorId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.*, " +
                "p.username as patient_username, p.email as patient_email, p.phone as patient_phone, " +
                "d.username as doctor_username, d.email as doctor_email " +
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
        }
    }

    public boolean deleteAppointment(int appointmentId) throws SQLException {
        String query = "DELETE FROM Appointments WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);

            return stmt.executeUpdate() > 0;
        }
    }

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
        patient.setPhone(rs.getString("patient_phone"));
        patient.setRole("PATIENT");

        User doctor = new User();
        doctor.setId(rs.getInt("doctor_id"));
        doctor.setUsername(rs.getString("doctor_username"));
        doctor.setEmail(rs.getString("doctor_email"));
        doctor.setRole("MEDECIN");

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        return appointment;
    }
}