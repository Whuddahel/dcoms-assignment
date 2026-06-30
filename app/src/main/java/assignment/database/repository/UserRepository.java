package assignment.database.repository;

import assignment.database.DatabaseManager;
import assignment.shared.model.Users;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

  public static boolean addUser(Users user) {
    String sql =
        "INSERT INTO Users (firstName, lastName, userRole, icPassportNo, email, password) VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setString(3, user.getUserRole());
      ps.setString(4, user.getIcPassportNo());
      ps.setString(5, user.getEmail());
      ps.setString(6, user.getPassword());

      int rows = ps.executeUpdate();
      System.out.println("User inserted: " + user.getFullName());
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static Users getUserById(int userId) {
    String sql =
        "SELECT userId, firstName, lastName, userRole, icPassportNo, email, password FROM Users WHERE userId = ?";

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
              rs.getString("password"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean updateUser(Users user) {
    String sql =
        "UPDATE Users SET firstName = ?, lastName = ?, userRole = ?, icPassportNo = ?, email = ?, password = ? WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setString(3, user.getUserRole());
      ps.setString(4, user.getIcPassportNo());
      ps.setString(5, user.getEmail());
      ps.setString(6, user.getPassword());
      ps.setInt(7, user.getUserId());

      int rows = ps.executeUpdate();
      System.out.println("User updated: " + user.getFullName());
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean deleteUser(int userId) {
    String sql = "DELETE FROM Users WHERE userId = ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, userId);
      int rows = ps.executeUpdate();
      System.out.println("User deleted with ID: " + userId);
      return rows > 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

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
}
