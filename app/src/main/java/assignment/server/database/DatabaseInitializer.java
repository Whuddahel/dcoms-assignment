package assignment.server.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

  private DatabaseInitializer() {}

  public static void initialize() throws SQLException, IOException {

    try (Connection connection = DatabaseManager.getConnection()) {
      System.out.println("Connected.");

      if (!usersTableExists(connection)) {
        System.out.println("Executing schema.sql.");
        executeSqlFile(connection, "db/schema.sql");
        System.out.println("Executing seed.sql.");
        executeSqlFile(connection, "db/seed.sql");
        System.out.println("Database initialized.");
      } else {
        System.out.println("Database exists.");
        if (!seedUserExists(connection)) {
          System.out.println("Seed user not found. Executing seed.sql.");
          executeSqlFile(connection, "db/seed.sql");
        }
      }
    }
  }

  private static boolean seedUserExists(Connection connection) {
    String sql = "SELECT 1 FROM User WHERE firstName = 'Michael' AND icPassportNo = '010101100101'";
    try (Statement stmt = connection.createStatement();
        var rs = stmt.executeQuery(sql)) {
      return rs.next();
    } catch (SQLException e) {
      return false;
    }
  }

  private static boolean usersTableExists(Connection connection) {

    try (var resultSet = connection.getMetaData().getTables(null, null, "USER", null)) {

      return resultSet.next();

    } catch (SQLException e) {
      return false;
    }
  }

  private static void executeSqlFile(Connection connection, String resourcePath)
      throws IOException, SQLException {

    try (InputStream inputStream =
        DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourcePath)) {

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
