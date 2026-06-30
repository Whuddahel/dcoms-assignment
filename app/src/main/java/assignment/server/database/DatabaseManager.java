package assignment.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
  // private static final String URL = "jdbc:derby:data/appdb;create=true";
  private static final String URL = "jdbc:derby://localhost:1527/appdb;create=true";

  private DatabaseManager() {}

  public static Connection getConnection() throws SQLException {
    // return DriverManager.getConnection(URL);
    return DriverManager.getConnection(URL, "User", "Password");
  }
}
