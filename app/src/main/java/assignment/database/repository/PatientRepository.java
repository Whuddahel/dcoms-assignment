package assignment.database.repository;

import assignment.database.DatabaseManager;
import assignment.shared.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

  public static boolean addPatient(Patient patient) {
    String insertUserSql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password) VALUES (?, ?, ?, ?, ?, ?)";
    String insertPatSql =
        "INSERT INTO Patient (userId, medicalRecordId, contactNumber) VALUES (?, ?, ?)";

    Connection conn = null;
    try {
      conn = DatabaseManager.getConnection();
      conn.setAutoCommit(false);

      int userId;
      try (PreparedStatement psUser =
          conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
        psUser.setString(1, patient.getFirstName());
        psUser.setString(2, patient.getLastName());
        psUser.setString(3, patient.getUserRole());
        psUser.setString(4, patient.getIcPassportNo());
        psUser.setString(5, patient.getEmail());
        psUser.setString(6, patient.getPassword());
        psUser.executeUpdate();

        try (ResultSet rs = psUser.getGeneratedKeys()) {
          if (rs.next()) {
            userId = rs.getInt(1);
          } else {
            throw new SQLException("Failed to retrieve generated userId.");
          }
        }
      }

      try (PreparedStatement psPat = conn.prepareStatement(insertPatSql)) {
        psPat.setInt(1, userId);
        psPat.setString(2, patient.getMedicalRecordId());
        psPat.setString(3, patient.getContactNumber());
        psPat.executeUpdate();
      }

      conn.commit();
      System.out.println("Patient inserted successfully.");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      return false;
    } finally {
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  public static Patient getPatientById(int patientId) {
    String sql =
        "SELECT p.patientId, p.userId, p.medicalRecordId, p.contactNumber, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.password "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId "
            + "WHERE p.patientId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, patientId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Patient(
              rs.getInt("patientId"),
              rs.getInt("userId"),
              rs.getString("firstName"),
              rs.getString("lastName"),
              rs.getString("userRole"),
              rs.getString("icPassportNo"),
              rs.getString("email"),
              rs.getString("password"),
              rs.getString("medicalRecordId"),
              rs.getString("contactNumber"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean updatePatient(Patient patient) {
    String updateUserSql =
        "UPDATE Users SET firstName = ?, lastName = ?, userRole = ?, icPassportNo = ?, email = ?, password = ? "
            + "WHERE userId = (SELECT userId FROM Patient WHERE patientId = ?)";
    String updatePatSql =
        "UPDATE Patient SET medicalRecordId = ?, contactNumber = ? WHERE patientId = ?";

    Connection conn = null;
    try {
      conn = DatabaseManager.getConnection();
      conn.setAutoCommit(false);

      try (PreparedStatement psUser = conn.prepareStatement(updateUserSql)) {
        psUser.setString(1, patient.getFirstName());
        psUser.setString(2, patient.getLastName());
        psUser.setString(3, patient.getUserRole());
        psUser.setString(4, patient.getIcPassportNo());
        psUser.setString(5, patient.getEmail());
        psUser.setString(6, patient.getPassword());
        psUser.setInt(7, patient.getPatientId());
        psUser.executeUpdate();
      }

      try (PreparedStatement psPat = conn.prepareStatement(updatePatSql)) {
        psPat.setString(1, patient.getMedicalRecordId());
        psPat.setString(2, patient.getContactNumber());
        psPat.setInt(3, patient.getPatientId());
        psPat.executeUpdate();
      }

      conn.commit();
      System.out.println("Patient updated successfully.");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      return false;
    } finally {
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  public static boolean deletePatient(int patientId) {
    String selectSql = "SELECT userId FROM Patient WHERE patientId = ?";
    String deletePatSql = "DELETE FROM Patient WHERE patientId = ?";
    String deleteUserSql = "DELETE FROM Users WHERE userId = ?";

    Connection conn = null;
    try {
      conn = DatabaseManager.getConnection();
      conn.setAutoCommit(false);

      int userId = -1;
      try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
        psSelect.setInt(1, patientId);
        try (ResultSet rs = psSelect.executeQuery()) {
          if (rs.next()) {
            userId = rs.getInt("userId");
          }
        }
      }

      if (userId == -1) {
        throw new SQLException("Patient not found with patientId: " + patientId);
      }

      try (PreparedStatement psPat = conn.prepareStatement(deletePatSql)) {
        psPat.setInt(1, patientId);
        psPat.executeUpdate();
      }

      try (PreparedStatement psUser = conn.prepareStatement(deleteUserSql)) {
        psUser.setInt(1, userId);
        psUser.executeUpdate();
      }

      conn.commit();
      System.out.println("Patient deleted successfully.");
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      if (conn != null) {
        try {
          conn.rollback();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
      return false;
    } finally {
      if (conn != null) {
        try {
          conn.setAutoCommit(true);
          conn.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
    }
  }

  public static List<Patient> listAllPatients() throws SQLException {
    String sql =
        "SELECT p.patientId, p.userId, p.medicalRecordId, p.contactNumber, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.password "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId";

    List<Patient> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== PATIENTS IN DATABASE ===");
      boolean empty = true;
      while (rs.next()) {
        empty = false;
        Patient patient =
            new Patient(
                rs.getInt("patientId"),
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("medicalRecordId"),
                rs.getString("contactNumber"));
        list.add(patient);
        System.out.println(
            patient.getPatientId()
                + " | "
                + patient.getFullName()
                + " | "
                + patient.getMedicalRecordId()
                + " | "
                + patient.getEmail());
      }
      if (empty) {
        System.out.println("(no patients found)");
      }
      System.out.println("============================");
    }
    return list;
  }
}
