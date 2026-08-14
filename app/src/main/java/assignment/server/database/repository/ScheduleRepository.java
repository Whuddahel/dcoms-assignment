package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Schedule;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScheduleRepository {

  public static boolean addSchedule(Schedule schedule) throws SQLException {
    String checkSql =
        "SELECT COUNT(*) FROM Schedule WHERE doctorId = ? AND day = ? AND deleted = false "
            + "AND startTime < ? AND endTime > ?";
    String sql = "INSERT INTO Schedule (doctorId, day, startTime, endTime) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
          psCheck.setInt(1, schedule.getDoctorId());
          psCheck.setString(2, schedule.getDay());
          psCheck.setTime(3, schedule.getEndTime());
          psCheck.setTime(4, schedule.getStartTime());
          try (ResultSet rs = psCheck.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
              conn.rollback();
              return false; // Overlapping schedule slot
            }
          }
        }
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
          ps.setInt(1, schedule.getDoctorId());
          ps.setString(2, schedule.getDay());
          ps.setTime(3, schedule.getStartTime());
          ps.setTime(4, schedule.getEndTime());
          int rows = ps.executeUpdate();
          conn.commit();
          return rows > 0;
        }
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  public static List<Schedule> getSchedulesByDoctorId(int doctorId) throws SQLException {
    String sql =
        "SELECT scheduleId, doctorId, day, startTime, endTime FROM Schedule WHERE doctorId = ? AND deleted = false";
    List<Schedule> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          list.add(
              new Schedule(
                  rs.getInt("scheduleId"),
                  rs.getInt("doctorId"),
                  rs.getString("day"),
                  rs.getTime("startTime"),
                  rs.getTime("endTime")));
        }
      }
    }
    return list;
  }

  // Soft delete: atomic with lock and active appointment check
  public static boolean deleteSchedule(int scheduleId) throws SQLException {
    String lockSql =
        "SELECT scheduleId FROM Schedule WHERE scheduleId = ? AND deleted = false FOR UPDATE";
    String countSql =
        "SELECT COUNT(*) "
            + "FROM Appointment a "
            + "LEFT JOIN Consultation c ON a.appointmentId = c.appointmentId "
            + "WHERE a.scheduleId = ? "
            + "  AND a.cancelledByUserId IS NULL "
            + "  AND c.appointmentId IS NULL";
    String updateSql = "UPDATE Schedule SET deleted = true WHERE scheduleId = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement psLock = conn.prepareStatement(lockSql)) {
          psLock.setInt(1, scheduleId);
          try (ResultSet rsLock = psLock.executeQuery()) {
            if (!rsLock.next()) {
              conn.rollback();
              return false; // Not found or already deleted
            }
          }
        }

        try (PreparedStatement psCount = conn.prepareStatement(countSql)) {
          psCount.setInt(1, scheduleId);
          try (ResultSet rsCount = psCount.executeQuery()) {
            if (rsCount.next() && rsCount.getInt(1) > 0) {
              conn.rollback();
              throw new IllegalStateException(
                  "CANNOT_DELETE: A patient has an upcoming appointment booked in this time slot.");
            }
          }
        }

        try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
          psUpdate.setInt(1, scheduleId);
          int rows = psUpdate.executeUpdate();
          conn.commit();
          return rows > 0;
        }
      } catch (SQLException | IllegalStateException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  // Only counts appointments that still need this slot: not cancelled, and not yet happened.
  // Past or cancelled history no longer blocks deletion.
  public static int countActiveUpcomingAppointments(int scheduleId) throws SQLException {
    String sql =
        "SELECT COUNT(*) "
            + "FROM Appointment a "
            + "LEFT JOIN Consultation c ON a.appointmentId = c.appointmentId "
            + "WHERE a.scheduleId = ? "
            + "  AND a.cancelledByUserId IS NULL "
            +
            //            "  AND a.appointmentDate >= CURRENT_DATE " +
            "  AND c.appointmentId IS NULL";
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, scheduleId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
      }
    }
    return 0;
  }

  public static Schedule getScheduleById(int scheduleId) throws SQLException {
    String sql =
        "SELECT scheduleId, doctorId, day, startTime, endTime FROM Schedule WHERE scheduleId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, scheduleId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Schedule(
              rs.getInt("scheduleId"),
              rs.getInt("doctorId"),
              rs.getString("day"),
              rs.getTime("startTime"),
              rs.getTime("endTime"));
        }
      }
    }
    return null;
  }

  public static boolean updateSchedule(Schedule schedule) throws SQLException {
    String sql =
        "UPDATE Schedule SET doctorId = ?, day = ?, startTime = ?, endTime = ? WHERE scheduleId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, schedule.getDoctorId());
      ps.setString(2, schedule.getDay());
      ps.setTime(3, schedule.getStartTime());
      ps.setTime(4, schedule.getEndTime());
      ps.setInt(5, schedule.getScheduleId());
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  // TODO: Remove before submission
  public static List<Schedule> listAllSchedules() throws SQLException {
    String sql = "SELECT scheduleId, doctorId, day, startTime, endTime FROM Schedule";

    List<Schedule> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== SCHEDULES IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        Schedule schedule =
            new Schedule(
                rs.getInt("scheduleId"),
                rs.getInt("doctorId"),
                rs.getString("day"),
                rs.getTime("startTime"),
                rs.getTime("endTime"));
        list.add(schedule);
        System.out.println(
            schedule.getScheduleId()
                + " | Doctor ID: "
                + schedule.getDoctorId()
                + " | "
                + schedule.getDay()
                + " ("
                + schedule.getStartTime()
                + " - "
                + schedule.getEndTime()
                + ")");
      }
      if (empty) {
        System.out.println("(no schedules found)");
      }
      System.out.println("=============================");
    }
    return list;
  }

  public static List<Schedule> getAllSchedules() throws SQLException {
    String sql = "SELECT scheduleId, doctorId, day, startTime, endTime FROM Schedule";

    List<Schedule> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new Schedule(
                rs.getInt("scheduleId"),
                rs.getInt("doctorId"),
                rs.getString("day"),
                rs.getTime("startTime"),
                rs.getTime("endTime")));
      }
    }
    return list;
  }
}
