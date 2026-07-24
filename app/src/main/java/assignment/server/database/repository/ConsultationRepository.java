package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Consultation;
import assignment.shared.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepository {

  public static boolean addConsultation(Consultation con) throws SQLException {
    String sql = "INSERT INTO Consultation (appointmentId, content, fee) VALUES (?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, con.getAppointmentId());
      ps.setString(2, con.getContent());
      ps.setDouble(3, con.getFee());
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static Consultation getConsultationById(int consultationId) throws SQLException {
    String sql =
        "SELECT consultationId, appointmentId, content, fee, createdAt FROM Consultation WHERE consultationId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, consultationId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Consultation(
              rs.getInt("consultationId"),
              rs.getInt("appointmentId"),
              rs.getString("content"),
              rs.getDouble("fee"),
              rs.getTimestamp("createdAt"));
        }
      }
    }
    return null;
  }

  public static boolean updateConsultation(Consultation con) throws SQLException {
    String sql =
        "UPDATE Consultation SET appointmentId = ?, content = ?, fee = ? WHERE consultationId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, con.getAppointmentId());
      ps.setString(2, con.getContent());
      ps.setDouble(3, con.getFee());
      ps.setInt(4, con.getConsultationId());
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static boolean deleteConsultation(int consultationId) throws SQLException {
    String sql = "DELETE FROM Consultation WHERE consultationId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, consultationId);
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  // TODO: Remove before submission
  public static List<Consultation> listAllConsultations() throws SQLException {
    String sql = "SELECT consultationId, appointmentId, content, fee, createdAt FROM Consultation";

    List<Consultation> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== CONSULTATIONS IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        Consultation con =
            new Consultation(
                rs.getInt("consultationId"),
                rs.getInt("appointmentId"),
                rs.getString("content"),
                rs.getDouble("fee"),
                rs.getTimestamp("createdAt"));
        list.add(con);
        System.out.println(
            con.getConsultationId()
                + " | Appointment ID: "
                + con.getAppointmentId()
                + " | Fee: "
                + con.getFee()
                + " | Created: "
                + con.getCreatedAt());
      }
      if (empty) {
        System.out.println("(no consultations found)");
      }
      System.out.println("=================================");
    }
    return list;
  }

  public static List<Consultation> getAllConsultations() throws SQLException {
    String sql = "SELECT consultationId, appointmentId, content, fee, createdAt FROM Consultation";

    List<Consultation> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new Consultation(
                rs.getInt("consultationId"),
                rs.getInt("appointmentId"),
                rs.getString("content"),
                rs.getDouble("fee"),
                rs.getTimestamp("createdAt")));
      }
    }
    return list;
  }

  public static List<Consultation> getConsultationsByPatientId(int patientId) throws SQLException {
    String sql =
        "SELECT c.consultationId, c.appointmentId, c.content, c.fee, c.createdAt "
            + "FROM Consultation c "
            + "JOIN Appointment a ON c.appointmentId = a.appointmentId "
            + "WHERE a.patientId = ? "
            + "ORDER BY c.createdAt DESC";

    List<Consultation> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, patientId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(
              new Consultation(
                  rs.getInt("consultationId"),
                  rs.getInt("appointmentId"),
                  rs.getString("content"),
                  rs.getDouble("fee"),
                  rs.getTimestamp("createdAt")));
        }
      }
    }
    return list;
  }

  public static Consultation getConsultationByAppointmentId(int appointmentId) throws SQLException {
    String sql =
        "SELECT consultationId, appointmentId, content, fee, createdAt FROM Consultation WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, appointmentId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Consultation(
              rs.getInt("consultationId"),
              rs.getInt("appointmentId"),
              rs.getString("content"),
              rs.getDouble("fee"),
              rs.getTimestamp("createdAt"));
        }
      }
    }
    return null;
  }

  public static List<Patient> getPatientsWithConsultationsByDoctorId(int doctorId)
      throws SQLException {
    String sql =
        "SELECT DISTINCT p.patientId, p.userId, p.medicalRecordId, p.contactNumber, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.createdAt, u.deleted "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId "
            + "JOIN Appointment a ON a.patientId = p.patientId "
            + "JOIN Consultation c ON c.appointmentId = a.appointmentId "
            + "WHERE a.doctorId = ? AND u.deleted = false";

    List<Patient> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(
              new Patient(
                  rs.getInt("patientId"),
                  rs.getInt("userId"),
                  rs.getString("firstName"),
                  rs.getString("lastName"),
                  rs.getString("userRole"),
                  rs.getString("icPassportNo"),
                  rs.getString("email"),
                  null, // password not needed here
                  rs.getInt("medicalRecordId"),
                  rs.getString("contactNumber"),
                  rs.getTimestamp("createdAt"),
                  rs.getBoolean("deleted")));
        }
      }
    }
    return list;
  }
}
