package assignment.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

  private DatabaseManager() {}

  public static Connection getConnection() throws SQLException {
    String dbUrl = System.getProperty("DB_URL");
    if (dbUrl == null || dbUrl.trim().isEmpty()) {
      dbUrl = System.getenv("DB_URL");
    }
    if (dbUrl == null || dbUrl.trim().isEmpty()) {
      String dbHost = System.getProperty("DB_HOST");
      if (dbHost == null || dbHost.isEmpty()) {
        dbHost = System.getenv("DB_HOST");
      }
      if (dbHost == null || dbHost.isEmpty()) {
        throw new IllegalStateException("DB_HOST environment variable is not set.");
      }
      dbUrl = "jdbc:derby://" + dbHost + ":1527/appdb;create=true";
    }

    String dbUser = System.getProperty("DB_USER");
    if (dbUser == null || dbUser.isEmpty()) {
      dbUser = System.getenv("DB_USER");
    }
    if (dbUser == null || dbUser.isEmpty()) {
      throw new IllegalStateException("DB_USER environment variable is not set.");
    }

    String dbPassword = System.getProperty("DB_PASSWORD");
    if (dbPassword == null || dbPassword.isEmpty()) {
      dbPassword = System.getenv("DB_PASSWORD");
    }
    if (dbPassword == null || dbPassword.isEmpty()) {
      throw new IllegalStateException("DB_PASSWORD environment variable is not set.");
    }

    return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
  }
}
