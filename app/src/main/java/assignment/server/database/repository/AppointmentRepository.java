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

  public static boolean addAppointment(Appointment app) throws SQLException {
    String sql =
        "INSERT INTO Appointment (doctorId, patientId, scheduleId, cancelledByUserId) VALUES (?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, app.getDoctorId());
      ps.setInt(2, app.getPatientId());
      ps.setInt(3, app.getScheduleId());
      if (app.getcancelledByUserId() != null) {
        ps.setInt(4, app.getcancelledByUserId());
      } else {
        ps.setNull(4, java.sql.Types.INTEGER);
      }
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static Appointment getAppointmentById(int appointmentId) throws SQLException {
    String sql =
        "SELECT appointmentId, doctorId, patientId, scheduleId, createdAt, cancelledByUserId FROM Appointment WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, appointmentId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          int cancelledByUserIdVal = rs.getInt("cancelledByUserId");
          Integer cancelledByUserId = rs.wasNull() ? null : cancelledByUserIdVal;
          return new Appointment(
              rs.getInt("appointmentId"),
              rs.getInt("doctorId"),
              rs.getInt("patientId"),
              rs.getInt("scheduleId"),
              rs.getTimestamp("createdAt"),
              cancelledByUserId);
        }
      }
    }
    return null;
  }

  public static boolean updateAppointment(Appointment app) throws SQLException {
    String sql =
        "UPDATE Appointment SET doctorId = ?, patientId = ?, scheduleId = ?, cancelledByUserId = ? WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, app.getDoctorId());
      ps.setInt(2, app.getPatientId());
      ps.setInt(3, app.getScheduleId());
      if (app.getcancelledByUserId() != null) {
        ps.setInt(4, app.getcancelledByUserId());
      } else {
        ps.setNull(4, java.sql.Types.INTEGER);
      }
      ps.setInt(5, app.getAppointmentId());
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static boolean deleteAppointment(int appointmentId) throws SQLException {
    String sql = "DELETE FROM Appointment WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, appointmentId);
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  // TODO: Remove before submission
  public static List<Appointment> listAllAppointments() throws SQLException {
    String sql =
        "SELECT appointmentId, doctorId, patientId, scheduleId, createdAt, cancelledByUserId FROM Appointment";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== APPOINTMENTS IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        int cancelledByUserIdVal = rs.getInt("cancelledByUserId");
        Integer cancelledByUserId = rs.wasNull() ? null : cancelledByUserIdVal;
        Appointment app =
            new Appointment(
                rs.getInt("appointmentId"),
                rs.getInt("doctorId"),
                rs.getInt("patientId"),
                rs.getInt("scheduleId"),
                rs.getTimestamp("createdAt"),
                cancelledByUserId);
        list.add(app);
        System.out.println(
            app.getAppointmentId()
                + " | Doctor ID: "
                + app.getDoctorId()
                + " | Patient ID: "
                + app.getPatientId()
                + " | Created: "
                + app.getCreatedAt()
                + " | Cancelled By: "
                + app.getcancelledByUserId());
      }
      if (empty) {
        System.out.println("(no appointments found)");
      }
      System.out.println("================================");
    }
    return list;
  }

  public static List<Appointment> getAllAppointments() throws SQLException {
    String sql =
        "SELECT appointmentId, doctorId, patientId, scheduleId, createdAt, cancelledByUserId FROM Appointment";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        int cancelledByUserIdVal = rs.getInt("cancelledByUserId");
        Integer cancelledByUserId = rs.wasNull() ? null : cancelledByUserIdVal;
        list.add(
            new Appointment(
                rs.getInt("appointmentId"),
                rs.getInt("doctorId"),
                rs.getInt("patientId"),
                rs.getInt("scheduleId"),
                rs.getTimestamp("createdAt"),
                cancelledByUserId));
      }
    }
    return list;
  }
}
