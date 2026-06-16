package assignment.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
}
