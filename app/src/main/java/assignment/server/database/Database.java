package assignment.server.database;

import java.io.IOException;
import java.sql.SQLException;

public class Database {
  public static void main(String[] args) throws IOException {
    try {
      DatabaseInitializer.initialize();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    DatabaseRegistry.startRegistry();
    System.out.println("Database Server Running...");
  }
}
