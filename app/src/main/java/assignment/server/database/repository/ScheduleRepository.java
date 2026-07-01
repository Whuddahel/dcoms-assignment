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

  public static boolean addSchedule(Schedule schedule) {
    String sql = "INSERT INTO Schedule (doctorId, day, startTime, endTime) VALUES (?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, schedule.getDoctorId());
      ps.setString(2, schedule.getDay());
      ps.setTime(3, schedule.getStartTime());
      ps.setTime(4, schedule.getEndTime());
      int rows = ps.executeUpdate();
      System.out.println("Schedule inserted successfully.");
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Schedule getScheduleById(int scheduleId) {
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
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean updateSchedule(Schedule schedule) {
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
      System.out.println("Schedule updated successfully.");
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean deleteSchedule(int scheduleId) {
    String sql = "DELETE FROM Schedule WHERE scheduleId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, scheduleId);
      int rows = ps.executeUpdate();
      System.out.println("Schedule deleted successfully.");
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

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
}
