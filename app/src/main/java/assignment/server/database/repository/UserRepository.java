package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.model.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

  public static boolean addUser(Users user) throws SQLException {
    String sql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setString(3, user.getUserRole());
      ps.setString(4, user.getIcPassportNo());
      ps.setString(5, user.getEmail());
      ps.setString(6, user.getPasswordHash());

      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  public static Users getUserById(int userId) throws SQLException {
    String sql =
        "SELECT userId, firstName, lastName, userRole, icPassportNo, email FROM Users WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new Users(
              rs.getInt("userId"),
              rs.getString("firstName"),
              rs.getString("lastName"),
              rs.getString("userRole"),
              rs.getString("icPassportNo"),
              rs.getString("email"),
              null);
        }
      }
    }
    return null;
  }

  public static boolean updateUser(Users user) throws SQLException {
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
    String sql = "DELETE FROM Users WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      int rows = ps.executeUpdate();
      return rows > 0;
    }
  }

  // TODO: Remove before submission
  public static void listAllUsers() throws SQLException {

    String sql = "SELECT userId, firstName, lastName, userRole, icPassportNo, email FROM Users";

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

  public static List<Users> getAllUsers() throws SQLException {
    String sql = "SELECT userId, firstName, lastName, userRole, icPassportNo, email FROM Users";

    List<Users> list = new ArrayList<>();
    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        list.add(
            new Users(
                rs.getInt("userId"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("userRole"),
                rs.getString("icPassportNo"),
                rs.getString("email"),
                null));
      }
    }
    return list;
  }
}
