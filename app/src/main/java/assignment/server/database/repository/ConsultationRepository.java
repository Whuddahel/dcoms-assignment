package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Consultation;
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
}
