package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {

  public static boolean addDoctor(Doctor doctor) throws SQLException {
    String insertUserSql =
        "INSERT INTO User (firstName, lastName, userRole, icPassportNo, email, password) VALUES (?, ?, ?, ?, ?, ?)";
    String insertDocSql = "INSERT INTO Doctor (userId, Specialization) VALUES (?, ?)";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int userId;
        try (PreparedStatement psUser =
            conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
          psUser.setString(1, doctor.getFirstName());
          psUser.setString(2, doctor.getLastName());
          psUser.setString(3, doctor.getUserRole());
          psUser.setString(4, doctor.getIcPassportNo());
          psUser.setString(5, doctor.getEmail());
          psUser.setString(6, doctor.getPasswordHash());
          psUser.executeUpdate();

          try (ResultSet rs = psUser.getGeneratedKeys()) {
            if (rs.next()) {
              userId = rs.getInt(1);
            } else {
              throw new SQLException("Failed to retrieve generated userId.");
            }
          }
        }

        try (PreparedStatement psDoc = conn.prepareStatement(insertDocSql)) {
          psDoc.setInt(1, userId);
          psDoc.setString(2, doctor.getSpecialization());
          psDoc.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  public static Doctor getDoctorById(int doctorId) throws SQLException {
    String sql =
        "SELECT d.doctorId, d.userId, d.Specialization, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM Doctor d "
            + "JOIN User u ON d.userId = u.userId "
            + "WHERE d.doctorId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Doctor(
              rs.getInt("doctorId"),
              rs.getInt("userId"),
              rs.getString("firstName"),
              rs.getString("lastName"),
              rs.getString("userRole"),
              rs.getString("icPassportNo"),
              rs.getString("email"),
              null,
              rs.getString("Specialization"));
        }
      }
    }
    return null;
  }

  public static boolean updateDoctor(Doctor doctor) throws SQLException {
    String updateUserSql =
        "UPDATE User SET firstName = ?, lastName = ?, userRole = ?, icPassportNo = ?, email = ?, password = ? "
            + "WHERE userId = (SELECT userId FROM Doctor WHERE doctorId = ?)";
    String updateDocSql = "UPDATE Doctor SET Specialization = ? WHERE doctorId = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement psUser = conn.prepareStatement(updateUserSql)) {
          psUser.setString(1, doctor.getFirstName());
          psUser.setString(2, doctor.getLastName());
          psUser.setString(3, doctor.getUserRole());
          psUser.setString(4, doctor.getIcPassportNo());
          psUser.setString(5, doctor.getEmail());
          psUser.setString(6, doctor.getPasswordHash());
          psUser.setInt(7, doctor.getDoctorId());
          psUser.executeUpdate();
        }

        try (PreparedStatement psDoc = conn.prepareStatement(updateDocSql)) {
          psDoc.setString(1, doctor.getSpecialization());
          psDoc.setInt(2, doctor.getDoctorId());
          psDoc.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  public static boolean deleteDoctor(int doctorId) throws SQLException {
    String selectSql = "SELECT userId FROM Doctor WHERE doctorId = ?";
    String deleteDocSql = "DELETE FROM Doctor WHERE doctorId = ?";
    String deleteUserSql = "DELETE FROM User WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int userId = -1;
        try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
          psSelect.setInt(1, doctorId);
          try (ResultSet rs = psSelect.executeQuery()) {
            if (rs.next()) {
              userId = rs.getInt("userId");
            }
          }
        }

        if (userId == -1) {
          throw new SQLException("Doctor not found with doctorId: " + doctorId);
        }

        try (PreparedStatement psDoc = conn.prepareStatement(deleteDocSql)) {
          psDoc.setInt(1, doctorId);
          psDoc.executeUpdate();
        }

        try (PreparedStatement psUser = conn.prepareStatement(deleteUserSql)) {
          psUser.setInt(1, userId);
          psUser.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  // TODO: Remove before submission
  public static List<Doctor> listAllDoctors() throws SQLException {
    String sql =
        "SELECT d.doctorId, d.userId, d.Specialization, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM Doctor d "
            + "JOIN User u ON d.userId = u.userId";

    List<Doctor> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== DOCTORS IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        Doctor doctor =
            new Doctor(
                rs.getInt("doctorId"),
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null,
                rs.getString("Specialization"));
        list.add(doctor);
        System.out.println(
            doctor.getDoctorId()
                + " | "
                + doctor.getFullName()
                + " | "
                + doctor.getSpecialization()
                + " | "
                + doctor.getEmail());
      }
      if (empty) {
        System.out.println("(no doctors found)");
      }
      System.out.println("===========================");
    }
    return list;
  }

  public static List<Doctor> getAllDoctors() throws SQLException {
    String sql =
        "SELECT d.doctorId, d.userId, d.Specialization, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email "
            + "FROM Doctor d "
            + "JOIN User u ON d.userId = u.userId";

    List<Doctor> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new Doctor(
                rs.getInt("doctorId"),
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null,
                rs.getString("Specialization")));
      }
    }
    return list;
  }
}
