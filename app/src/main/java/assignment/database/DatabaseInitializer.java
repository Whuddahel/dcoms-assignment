package assignment.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

  private DatabaseInitializer() {
  }

  public static void initialize() throws SQLException, IOException {

    try (Connection connection = DatabaseManager.getConnection()) {
      System.out.println("Connected.");

      if (usersTableExists(connection)) {
        System.out.println("Database exists.");
        return;
      }

      System.out.println("Executing schema.sql.");
      executeSqlFile(connection, "db/schema.sql");
      System.out.println("Executing seed.sql.");
      executeSqlFile(connection, "db/seed.sql");
      System.out.println("Database initialized.");
    }
  }

  private static boolean usersTableExists(Connection connection) {

    try (var resultSet = connection.getMetaData().getTables(null, null, "USERS", null)) {

      return resultSet.next();

    } catch (SQLException e) {
      return false;
    }
  }

  private static void executeSqlFile(Connection connection, String resourcePath)
      throws IOException, SQLException {

    try (InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourcePath)) {

      if (inputStream == null) {
        throw new IOException("SQL file not found: " + resourcePath);
      }

      String sql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      try (Statement statement = connection.createStatement()) {
        String[] commands = sql.split(";");

        for (String command : commands) {
          command = command.trim();

          if (!command.isEmpty()) {
            statement.executeUpdate(command);
          }
        }
      }
    }
  }
}
