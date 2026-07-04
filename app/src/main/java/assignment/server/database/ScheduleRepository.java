package assignment.server.database;

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
        String sql = "INSERT INTO Schedule (doctorId, day, startTime, endTime) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, schedule.getDoctorId());
            ps.setString(2, schedule.getDay());
            ps.setTime(3, schedule.getStartTime());
            ps.setTime(4, schedule.getEndTime());
            return ps.executeUpdate() > 0;
        }
    }

    public static List<Schedule> getSchedulesByDoctorId(int doctorId) throws SQLException
    {
        String sql = "SELECT scheduleId, doctorId, day, startTime, endTime FROM Schedule WHERE doctorId = ?";
        List<Schedule> list = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    list.add(new Schedule(
                            rs.getInt("scheduleId"),
                            rs.getInt("doctorId"),
                            rs.getString("day"),
                            rs.getTime("startTime"),
                            rs.getTime("endTime")
                    ));
                }
            }
        }
        return list;
    }

    public static boolean deleteSchedule(int scheduleId) throws SQLException
    {
        String sql = "DELETE FROM Schedule WHERE scheduleId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, scheduleId);
            return ps.executeUpdate() > 0;
        }
    }

    //This to help make sure no patient appointments exist within the schedule before cancel
    public static int countLinkedAppointments(int scheduleId) throws SQLException
    {
        String sql = "SELECT COUNT(*) FROM Appointment WHERE scheduleId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, scheduleId);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    //Because userID is used for session, we need to infer doctorID from userID
    public static int getDoctorIdByUserId(int userId) throws SQLException
    {
        String sql = "SELECT doctorId FROM Doctor WHERE userId = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt("doctorId");
                }
            }
        }
        return -1;
    }
}