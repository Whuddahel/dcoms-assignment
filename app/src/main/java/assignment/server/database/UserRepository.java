package assignment.server.database;

import assignment.shared.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

  public static void addUser(String username, String passwordHash, String role)
      throws SQLException {

    String sql = "INSERT INTO USERS (username, password_hash, role) VALUES (?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, username);
      ps.setString(2, passwordHash);
      ps.setString(3, role);

      ps.executeUpdate();

      System.out.println("User inserted: " + username);
    }
  }

  public static void listAllUsers() throws SQLException {

    String sql = "SELECT user_id, username, password_hash, role FROM users";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {

      System.out.println("=== USERS IN DATABASE ===");

      boolean empty = true;

      while (rs.next()) {
        empty = false;

        int id = rs.getInt("user_id");
        String username = rs.getString("username");
        String role = rs.getString("role");
        String passwordHash = rs.getString("password_hash");
        System.out.println(id + " | " + username + " | " + role + " | " + passwordHash);
      }

      if (empty) {
        System.out.println("(no users found)");
      }

      System.out.println("=========================");
    }
  }

  public static User getUserByUsername(String username) throws SQLException {
    String sql = "SELECT user_id, username, password_hash, role FROM users WHERE username = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql); ) {

      ps.setString(1, username);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return new User(
              rs.getInt("user_id"),
              rs.getString("username"),
              rs.getString("password_hash"),
              rs.getString("role"));
        }
        return null;
      }
    }
  }
}
