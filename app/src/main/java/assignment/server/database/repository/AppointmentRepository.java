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
    String lockScheduleSql =
        "SELECT startTime, endTime FROM Schedule WHERE scheduleId = ? AND deleted = false FOR UPDATE";
    String checkDoctorConflictSql =
        "SELECT COUNT(*) FROM Appointment a JOIN Schedule s ON a.scheduleId = s.scheduleId "
            + "WHERE a.doctorId = ? AND a.appointmentDate = ? AND a.cancelledByUserId IS NULL "
            + "AND s.startTime < ? AND s.endTime > ?";
    String checkPatientConflictSql =
        "SELECT COUNT(*) FROM Appointment a JOIN Schedule s ON a.scheduleId = s.scheduleId "
            + "WHERE a.patientId = ? AND a.appointmentDate = ? AND a.cancelledByUserId IS NULL "
            + "AND s.startTime < ? AND s.endTime > ?";
    String insertSql =
        "INSERT INTO Appointment (doctorId, patientId, scheduleId, appointmentDate, cancelledByUserId) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        // 1. Lock the schedule row to prevent concurrent bookings/deletions on this slot and
        // retrieve its times
        java.sql.Time startTime;
        java.sql.Time endTime;
        try (PreparedStatement psLock = conn.prepareStatement(lockScheduleSql)) {
          psLock.setInt(1, app.getScheduleId());
          try (ResultSet rsLock = psLock.executeQuery()) {
            if (!rsLock.next()) {
              conn.rollback();
              throw new IllegalStateException(
                  "The selected schedule slot is no longer available or was removed.");
            }
            startTime = rsLock.getTime("startTime");
            endTime = rsLock.getTime("endTime");
          }
        }

        // 2. Validate against booking past dates or past time slots on today's date
        java.time.LocalDate apptDate = app.getAppointmentDate().toLocalDate();
        java.time.LocalDate today = java.time.LocalDate.now();
        if (apptDate.isBefore(today)) {
          conn.rollback();
          throw new IllegalStateException("Cannot book an appointment for a past date.");
        }
        if (apptDate.isEqual(today)) {
          java.time.LocalTime nowTime = java.time.LocalTime.now();
          if (startTime.toLocalTime().isBefore(nowTime)) {
            conn.rollback();
            throw new IllegalStateException("The selected time slot has already passed for today.");
          }
        }

        // 3. Check if doctor already has an active overlapping appointment on this date
        try (PreparedStatement psDoc = conn.prepareStatement(checkDoctorConflictSql)) {
          psDoc.setInt(1, app.getDoctorId());
          psDoc.setDate(2, app.getAppointmentDate());
          psDoc.setTime(3, endTime);
          psDoc.setTime(4, startTime);
          try (ResultSet rsDoc = psDoc.executeQuery()) {
            if (rsDoc.next() && rsDoc.getInt(1) > 0) {
              conn.rollback();
              throw new IllegalStateException(
                  "The doctor already has an appointment booked in an overlapping time slot.");
            }
          }
        }

        // 4. Check if patient already has an active overlapping appointment on this date
        try (PreparedStatement psPat = conn.prepareStatement(checkPatientConflictSql)) {
          psPat.setInt(1, app.getPatientId());
          psPat.setDate(2, app.getAppointmentDate());
          psPat.setTime(3, endTime);
          psPat.setTime(4, startTime);
          try (ResultSet rsPat = psPat.executeQuery()) {
            if (rsPat.next() && rsPat.getInt(1) > 0) {
              conn.rollback();
              throw new IllegalStateException(
                  "You already have an active appointment scheduled in an overlapping time slot on this date.");
            }
          }
        }

        // 5. Insert appointment
        try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
          psInsert.setInt(1, app.getDoctorId());
          psInsert.setInt(2, app.getPatientId());
          psInsert.setInt(3, app.getScheduleId());
          psInsert.setDate(4, app.getAppointmentDate());
          if (app.getcancelledByUserId() != null) {
            psInsert.setInt(5, app.getcancelledByUserId());
          } else {
            psInsert.setNull(5, java.sql.Types.INTEGER);
          }
          int rows = psInsert.executeUpdate();
          conn.commit();
          return rows > 0;
        }
      } catch (SQLException | IllegalStateException e) {
        conn.rollback();
        throw e;
      }
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
    String sql =
        "UPDATE Appointment SET cancelledByUserId = ? WHERE appointmentId = ? AND cancelledByUserId IS NULL";

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
            + "WHERE a.doctorId = ? AND a.appointmentDate >= CURRENT_DATE "
            + "ORDER BY a.appointmentDate ASC, s.startTime ASC";

    List<Appointment> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
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
