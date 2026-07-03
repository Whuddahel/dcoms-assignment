package assignment.server.database;

import assignment.shared.config.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

  private DatabaseManager() {}

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASSWORD);
  }
}
