package assignment.server.database;

import assignment.shared.auth.Role;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.User;
import assignment.shared.model.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

  public static void addUser(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password)
      throws SQLException {

    String sql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, firstName);
      ps.setString(2, lastName);
      ps.setString(3, userRole);
      ps.setString(4, icPassportNo);
      ps.setString(5, email);
      ps.setString(6, password);

      ps.executeUpdate();

      System.out.println("User inserted: " + email);
    }
  }

  public static void listAllUsers() throws SQLException {

    String sql = "SELECT userId, email, password, userRole FROM Users";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== USERS IN DATABASE ===");

      boolean empty = true;

      while (rs.next()) {
        empty = false;

        int id = rs.getInt("userId");
        String username = rs.getString("email");
        String role = rs.getString("userRole");
        String passwordHash = rs.getString("password");
        System.out.println(id + " | " + username + " | " + role + " | " + passwordHash);
      }

      if (empty) {
        System.out.println("(no users found)");
      }

      System.out.println("=========================");
    }
  }

  public static User getUserByEmail(String email) throws SQLException {
    String sql = "SELECT userId, email, password, userRole FROM Users WHERE email = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql); ) {

      ps.setString(1, email);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          String dbRole = rs.getString("userRole");
          return new User(
              rs.getInt("userId"),
              rs.getString("email"),
              rs.getString("password"),
              Role.valueOf(dbRole.toUpperCase()));
        }
        return null;
      }
    }
  }

  public static List<Users> getAllUsersWithRoles() throws SQLException {
    String sql =
        "SELECT u.userId, u.firstName, u.lastName, u.userRole, u.icPassportNo, u.email, u.password, "
            + "       d.doctorId, d.Specialization, "
            + "       p.patientId, p.medicalRecordId, p.contactNumber, "
            + "       a.adminId, "
            + "       r.receptionistId "
            + "FROM Users u "
            + "LEFT JOIN Doctor d ON u.userId = d.userId "
            + "LEFT JOIN Patient p ON u.userId = p.userId "
            + "LEFT JOIN ClinicAdministrator a ON u.userId = a.userId "
            + "LEFT JOIN Receptionist r ON u.userId = r.userId";

    List<Users> users = new ArrayList<>();
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
                  specialization));
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
                  contactNumber));
        } else if ("admin".equalsIgnoreCase(userRole)) {
          int adminId = rs.getInt("adminId");
          users.add(
              new ClinicAdministrator(
                  adminId, userId, firstName, lastName, userRole, icPassportNo, email, password));
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
                  password));
        }
      }
    }
    return users;
  }
}
