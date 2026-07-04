package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.auth.Role;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

  public static boolean addUser(User user) throws SQLException {
    String sql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password, createdAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setString(3, user.getUserRole());
      ps.setString(4, user.getIcPassportNo());
      ps.setString(5, user.getEmail());
      ps.setString(6, user.getPasswordHash());
      ps.setTimestamp(7, user.getCreatedAt());

      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static User getUserById(int userId) throws SQLException {
    String sql =
        "SELECT userId, firstName, lastName, userRole, icPassportNo, email, createdAt, deleted FROM Users WHERE userId = ? AND deleted = false";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new User(
              rs.getInt("userId"),
              rs.getString("firstName"),
              rs.getString("lastName"),
              Role.databaseToEnum(rs.getString("userRole")),
              rs.getString("icPassportNo"),
              rs.getString("email"),
              null,
              rs.getTimestamp("createdAt"),
              rs.getBoolean("deleted"));
        }
      }
    }
    return null;
  }

  public static User getUserByEmail(String email) throws SQLException {
    // 1. Updated the query string to pull every field required by the new User
    // constructor
    String sql =
        "SELECT userId, firstName, lastName, userRole, icPassportNo, email, password, createdAt, deleted FROM Users WHERE email = ? AND deleted = false";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql); ) {

      ps.setString(1, email);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String dbRole = rs.getString("userRole");

          return new User(
              rs.getInt("userId"),
              rs.getString("firstName"),
              rs.getString("lastName"),
              Role.valueOf(dbRole.toUpperCase()),
              rs.getString("icPassportNo"),
              rs.getString("email"),
              rs.getString("password"),
              rs.getTimestamp("createdAt"),
              rs.getBoolean("deleted"));
        }
        return null;
      }
    }
  }

  public static boolean updateUser(User user) throws SQLException {
    String sql =
        "UPDATE Users SET firstName = ?, lastName = ?, userRole = ?, icPassportNo = ?, email = ?, password = ? WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setString(3, user.getUserRole());
      ps.setString(4, user.getIcPassportNo());
      ps.setString(5, user.getEmail());
      ps.setString(6, user.getPasswordHash());
      ps.setInt(7, user.getUserId());

      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static boolean deleteUser(int userId) throws SQLException {
    String sql = "UPDATE Users SET deleted = true WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  // TODO: Remove before submission
  public static void listAllUsers() throws SQLException {

    String sql =
        "SELECT userId, firstName, lastName, userRole, icPassportNo, email FROM Users WHERE deleted = false";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== USERS IN DATABASE ===");

      boolean empty = true;

      while (rs.next()) {
        empty = false;

        int id = rs.getInt("userId");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String role = rs.getString("userRole");
        String icPassportNo = rs.getString("icPassportNo");
        String email = rs.getString("email");
        System.out.println(
            id
                + " | "
                + firstName
                + " "
                + lastName
                + " | "
                + role
                + " | "
                + icPassportNo
                + " | "
                + email);
      }

      if (empty) {
        System.out.println("(no users found)");
      }

      System.out.println("=========================");
    }
  }

  public static List<User> getAllUsers() throws SQLException {
    String sql =
        "SELECT userId, firstName, lastName, userRole, icPassportNo, email, createdAt, deleted FROM Users WHERE deleted = false";

    List<User> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new User(
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                Role.databaseToEnum(rs.getString("userRole")),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null,
                rs.getTimestamp("createdAt"),
                rs.getBoolean("deleted")));
      }
    }
    return list;
  }

  public static List<User> getAllUsersWithRoles() throws SQLException {
    String sql =
        "SELECT u.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.password, u.createdAt, u.deleted, "
            + "       d.doctorId, d.Specialization, "
            + "       p.patientId, p.medicalRecordId, p.contactNumber, "
            + "       a.adminId, "
            + "       r.receptionistId "
            + "FROM Users u "
            + "LEFT JOIN Doctor d ON u.userId = d.userId "
            + "LEFT JOIN Patient p ON u.userId = p.userId "
            + "LEFT JOIN ClinicAdministrator a ON u.userId = a.userId "
            + "LEFT JOIN Receptionist r ON u.userId = r.userId "
            + "WHERE u.deleted = false";

    List<User> users = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        int userId = rs.getInt("userId");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String userRole = rs.getString("userRole");
        String icPassportNo = rs.getString("icPassportNo");
        String email = rs.getString("email");
        String password = rs.getString("password");
        Timestamp createdAt = rs.getTimestamp("createdAt");
        boolean deleted = rs.getBoolean("deleted");

        if ("doctor".equalsIgnoreCase(userRole)) {
          int doctorId = rs.getInt("doctorId");
          String specialization = rs.getString("Specialization");
          users.add(
              new Doctor(
                  doctorId,
                  userId,
                  firstName,
                  lastName,
                  userRole,
                  icPassportNo,
                  email,
                  password,
                  specialization,
                  createdAt,
                  deleted));
        } else if ("patient".equalsIgnoreCase(userRole)) {
          int patientId = rs.getInt("patientId");
          int medicalRecordId = rs.getInt("medicalRecordId");
          String contactNumber = rs.getString("contactNumber");
          users.add(
              new Patient(
                  patientId,
                  userId,
                  firstName,
                  lastName,
                  userRole,
                  icPassportNo,
                  email,
                  password,
                  medicalRecordId,
                  contactNumber,
                  createdAt,
                  deleted));
        } else if ("admin".equalsIgnoreCase(userRole)) {
          int adminId = rs.getInt("adminId");
          users.add(
              new ClinicAdministrator(
                  adminId,
                  userId,
                  firstName,
                  lastName,
                  userRole,
                  icPassportNo,
                  email,
                  password,
                  createdAt,
                  deleted));
        } else if ("receptionist".equalsIgnoreCase(userRole)) {
          int receptionistId = rs.getInt("receptionistId");
          users.add(
              new Receptionist(
                  receptionistId,
                  userId,
                  firstName,
                  lastName,
                  userRole,
                  icPassportNo,
                  email,
                  password,
                  createdAt,
                  deleted));
        }
      }
    }
    return users;
  }
}
