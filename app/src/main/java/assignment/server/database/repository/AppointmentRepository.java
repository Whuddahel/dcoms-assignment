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
        "INSERT INTO Appointment (doctorId, patientId, scheduleId, appointmentDate, cancelledByUserId) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, app.getDoctorId());
      ps.setInt(2, app.getPatientId());
      ps.setInt(3, app.getScheduleId());
      ps.setDate(4, app.getAppointmentDate());
      if (app.getcancelledByUserId() != null) {
        ps.setInt(5, app.getcancelledByUserId());
      } else {
        ps.setNull(5, java.sql.Types.INTEGER);
      }
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static Appointment getAppointmentById(int appointmentId) throws SQLException {
    String sql =
        "SELECT appointmentId, doctorId, patientId, scheduleId, appointmentDate, createdAt, cancelledByUserId FROM Appointment WHERE appointmentId = ?";

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
              rs.getDate("appointmentDate"),
              rs.getTimestamp("createdAt"),
              cancelledByUserId);
        }
      }
    }
    return null;
  }

  public static boolean updateAppointment(Appointment app) throws SQLException {
    String sql =
        "UPDATE Appointment SET doctorId = ?, patientId = ?, scheduleId = ?, appointmentDate = ?, cancelledByUserId = ? WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, app.getDoctorId());
      ps.setInt(2, app.getPatientId());
      ps.setInt(3, app.getScheduleId());
      ps.setDate(4, app.getAppointmentDate());
      if (app.getcancelledByUserId() != null) {
        ps.setInt(5, app.getcancelledByUserId());
      } else {
        ps.setNull(5, java.sql.Types.INTEGER);
      }
      ps.setInt(6, app.getAppointmentId());
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
        "SELECT appointmentId, doctorId, patientId, scheduleId, appointmentDate, createdAt, cancelledByUserId FROM Appointment";

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
                rs.getDate("appointmentDate"),
                rs.getTimestamp("createdAt"),
                cancelledByUserId);
        list.add(app);
        System.out.println(
            app.getAppointmentId()
                + " | Doctor ID: "
                + app.getDoctorId()
                + " | Patient ID: "
                + app.getPatientId()
                + " | Date: "
                + app.getAppointmentDate()
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
        "SELECT appointmentId, doctorId, patientId, scheduleId, appointmentDate, createdAt, cancelledByUserId FROM Appointment";

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
                rs.getDate("appointmentDate"),
                rs.getTimestamp("createdAt"),
                cancelledByUserId));
      }
    }
    return list;
  }

  public static List<Appointment> getAppointmentsByPatientId(int patientId) throws SQLException {
    String sql =
        "SELECT appointmentId, doctorId, patientId, scheduleId, appointmentDate, createdAt, cancelledByUserId "
            + "FROM Appointment WHERE patientId = ? ORDER BY appointmentDate DESC";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, patientId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int cancelledByUserIdVal = rs.getInt("cancelledByUserId");
          Integer cancelledByUserId = rs.wasNull() ? null : cancelledByUserIdVal;
          list.add(
              new Appointment(
                  rs.getInt("appointmentId"),
                  rs.getInt("doctorId"),
                  rs.getInt("patientId"),
                  rs.getInt("scheduleId"),
                  rs.getDate("appointmentDate"),
                  rs.getTimestamp("createdAt"),
                  cancelledByUserId));
        }
      }
    }
    return list;
  }

  public static List<Appointment> getAppointmentsByDoctorAndDate(int doctorId, java.sql.Date date)
      throws SQLException {
    String sql =
        "SELECT appointmentId, doctorId, patientId, scheduleId, appointmentDate, createdAt, cancelledByUserId "
            + "FROM Appointment WHERE doctorId = ? AND appointmentDate = ? AND cancelledByUserId IS NULL";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      ps.setDate(2, date);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int cancelledByUserIdVal = rs.getInt("cancelledByUserId");
          Integer cancelledByUserId = rs.wasNull() ? null : cancelledByUserIdVal;
          list.add(
              new Appointment(
                  rs.getInt("appointmentId"),
                  rs.getInt("doctorId"),
                  rs.getInt("patientId"),
                  rs.getInt("scheduleId"),
                  rs.getDate("appointmentDate"),
                  rs.getTimestamp("createdAt"),
                  cancelledByUserId));
        }
      }
    }
    return list;
  }

  public static boolean cancelAppointment(int appointmentId, int cancelledByUserId)
      throws SQLException {
    String sql = "UPDATE Appointment SET cancelledByUserId = ? WHERE appointmentId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, cancelledByUserId);
      ps.setInt(2, appointmentId);
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static List<Appointment> getUpcomingAppointmentsByDoctorId(int doctorId)
      throws SQLException {
    String sql =
        "SELECT a.appointmentId, a.doctorId, a.patientId, a.scheduleId, a.appointmentDate, a.createdAt, a.cancelledByUserId "
            + "FROM Appointment a "
            + "JOIN Schedule s ON a.scheduleId = s.scheduleId "
            + "WHERE a.doctorId = ? AND a.cancelledByUserId IS NULL";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(
              new Appointment(
                  rs.getInt("appointmentId"),
                  rs.getInt("doctorId"),
                  rs.getInt("patientId"),
                  rs.getInt("scheduleId"),
                  rs.getDate("appointmentDate"),
                  rs.getTimestamp("createdAt"),
                  null)); // cancelledByUserId is null by definition of query
        }
      }
    }
    return list;
  }

  public static List<Appointment> getAppointmentsByDoctorAndPatient(int doctorId, int patientId)
      throws SQLException {
    String sql =
        "SELECT a.appointmentId, a.doctorId, a.patientId, a.scheduleId, a.appointmentDate, a.createdAt, a.cancelledByUserId "
            + "FROM Appointment a "
            + "JOIN Consultation c ON c.appointmentId = a.appointmentId "
            + "WHERE a.doctorId = ? AND a.patientId = ? AND a.cancelledByUserId IS NULL "
            + "ORDER BY a.appointmentDate DESC";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      ps.setInt(2, patientId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int cancelledByUserIdVal = rs.getInt("cancelledByUserId");
          Integer cancelledByUserId = rs.wasNull() ? null : cancelledByUserIdVal;
          list.add(
              new Appointment(
                  rs.getInt("appointmentId"),
                  rs.getInt("doctorId"),
                  rs.getInt("patientId"),
                  rs.getInt("scheduleId"),
                  rs.getDate("appointmentDate"),
                  rs.getTimestamp("createdAt"),
                  cancelledByUserId));
        }
      }
    }
    return list;
  }
}
