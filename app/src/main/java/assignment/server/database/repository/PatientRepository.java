package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Patient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

  public static boolean addPatient(Patient patient) throws SQLException {
    String insertUserSql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
    String insertPatSql =
        "INSERT INTO Patient (userId, medicalRecordId, contactNumber) VALUES (?, NEXT VALUE FOR medical_record_seq, ?)";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        int userId;
        try (PreparedStatement psUser =
            conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
          psUser.setString(1, patient.getFirstName());
          psUser.setString(2, patient.getLastName());
          psUser.setString(3, patient.getUserRole());
          psUser.setString(4, patient.getIcPassportNo());
          psUser.setString(5, patient.getEmail());
          psUser.setString(6, patient.getPasswordHash());
          psUser.setTimestamp(7, patient.getCreatedAt());
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
          psPat.setString(2, patient.getContactNumber());
          psPat.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  public static Patient getPatientById(int patientId) throws SQLException {
    String sql =
        "SELECT p.patientId, p.userId, p.medicalRecordId, p.contactNumber, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.createdAt, u.deleted "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId "
            + "WHERE p.patientId = ? AND u.deleted = false";

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
              null,
              rs.getInt("medicalRecordId"),
              rs.getString("contactNumber"),
              rs.getTimestamp("createdAt"),
              rs.getBoolean("deleted"));
        }
      }
    }
    return null;
  }

  public static boolean updatePatient(Patient patient) throws SQLException {
    String updateUserSql =
        "UPDATE Users SET firstName = ?, lastName = ?, userRole = ?, icPassportNo = ?, email = ?, password = ? WHERE userId = ?";
    String updatePatSql = "UPDATE Patient SET contactNumber = ? WHERE patientId = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
        try (PreparedStatement psUser = conn.prepareStatement(updateUserSql)) {
          psUser.setString(1, patient.getFirstName());
          psUser.setString(2, patient.getLastName());
          psUser.setString(3, patient.getUserRole());
          psUser.setString(4, patient.getIcPassportNo());
          psUser.setString(5, patient.getEmail());
          psUser.setString(6, patient.getPasswordHash());
          psUser.setInt(7, patient.getUserId());
          psUser.executeUpdate();
        }

        try (PreparedStatement psPat = conn.prepareStatement(updatePatSql)) {
          psPat.setString(1, patient.getContactNumber());
          psPat.setInt(2, patient.getPatientId());
          psPat.executeUpdate();
        }

        conn.commit();
        return true;
      } catch (SQLException e) {
        conn.rollback();
        throw e;
      }
    }
  }

  public static boolean deletePatient(int patientId) throws SQLException {
    String selectSql = "SELECT userId FROM Patient WHERE patientId = ?";
    String softDeleteUserSql = "UPDATE Users SET deleted = true WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try {
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

        try (PreparedStatement psUser = conn.prepareStatement(softDeleteUserSql)) {
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
  public static List<Patient> listAllPatients() throws SQLException {
    String sql =
        "SELECT p.patientId, p.userId, p.medicalRecordId, p.contactNumber, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.createdAt, u.deleted "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId "
            + "WHERE u.deleted = false";

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
                null,
                rs.getInt("medicalRecordId"),
                rs.getString("contactNumber"),
                rs.getTimestamp("createdAt"),
                rs.getBoolean("deleted"));
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

  public static Patient getPatientByUserId(int userId) throws SQLException {
    String sql =
        "SELECT p.patientId, p.userId, p.medicalRecordId, p.contactNumber, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.createdAt, u.deleted "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId "
            + "WHERE p.userId = ? AND u.deleted = false";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
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
              null,
              rs.getInt("medicalRecordId"),
              rs.getString("contactNumber"),
              rs.getTimestamp("createdAt"),
              rs.getBoolean("deleted"));
        }
      }
    }
    return null;
  }

  public static List<Patient> getAllPatients() throws SQLException {
    String sql =
        "SELECT p.patientId, p.userId, p.medicalRecordId, p.contactNumber, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.createdAt, u.deleted "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId "
            + "WHERE u.deleted = false";

    List<Patient> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new Patient(
                rs.getInt("patientId"),
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null,
                rs.getInt("medicalRecordId"),
                rs.getString("contactNumber"),
                rs.getTimestamp("createdAt"),
                rs.getBoolean("deleted")));
      }
    }
    return list;
  }
}
