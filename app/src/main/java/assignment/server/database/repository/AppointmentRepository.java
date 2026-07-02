package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Appointment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

  public static boolean addAppointment(Appointment app) {
    String sql =
        "INSERT INTO Appointment (doctorId, medicalRecordId, scheduleId, cancelledBy) VALUES (?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, app.getDoctorId());
      ps.setInt(2, app.getMedicalRecordId());
      ps.setInt(3, app.getScheduleId());
      if (app.getCancelledBy() != null) {
        ps.setInt(4, app.getCancelledBy());
      } else {
        ps.setNull(4, java.sql.Types.INTEGER);
      }
      int rows = ps.executeUpdate();
      System.out.println("Appointment inserted successfully.");
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Appointment getAppointmentById(int appointmentId) {
    String sql =
        "SELECT appointmentId, doctorId, medicalRecordId, scheduleId, createdAt, cancelledBy FROM Appointment WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, appointmentId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          int cancelledByVal = rs.getInt("cancelledBy");
          Integer cancelledBy = rs.wasNull() ? null : cancelledByVal;
          return new Appointment(
              rs.getInt("appointmentId"),
              rs.getInt("doctorId"),
              rs.getInt("medicalRecordId"),
              rs.getInt("scheduleId"),
              rs.getTimestamp("createdAt"),
              cancelledBy);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean updateAppointment(Appointment app) {
    String sql =
        "UPDATE Appointment SET doctorId = ?, medicalRecordId = ?, scheduleId = ?, cancelledBy = ? WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, app.getDoctorId());
      ps.setInt(2, app.getMedicalRecordId());
      ps.setInt(3, app.getScheduleId());
      if (app.getCancelledBy() != null) {
        ps.setInt(4, app.getCancelledBy());
      } else {
        ps.setNull(4, java.sql.Types.INTEGER);
      }
      ps.setInt(5, app.getAppointmentId());
      int rows = ps.executeUpdate();
      System.out.println("Appointment updated successfully.");
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean deleteAppointment(int appointmentId) {
    String sql = "DELETE FROM Appointment WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, appointmentId);
      int rows = ps.executeUpdate();
      System.out.println("Appointment deleted successfully.");
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static List<Appointment> listAllAppointments() throws SQLException {
    String sql =
        "SELECT appointmentId, doctorId, medicalRecordId, scheduleId, createdAt, cancelledBy FROM Appointment";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== APPOINTMENTS IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        int cancelledByVal = rs.getInt("cancelledBy");
        Integer cancelledBy = rs.wasNull() ? null : cancelledByVal;
        Appointment app =
            new Appointment(
                rs.getInt("appointmentId"),
                rs.getInt("doctorId"),
                rs.getInt("medicalRecordId"),
                rs.getInt("scheduleId"),
                rs.getTimestamp("createdAt"),
                cancelledBy);
        list.add(app);
        System.out.println(
            app.getAppointmentId()
                + " | Doctor ID: "
                + app.getDoctorId()
                + " | Patient ID: "
                + app.getMedicalRecordId()
                + " | Created: "
                + app.getCreatedAt()
                + " | Cancelled By: "
                + app.getCancelledBy());
      }
      if (empty) {
        System.out.println("(no appointments found)");
      }
      System.out.println("================================");
    }
    return list;
  }
}
