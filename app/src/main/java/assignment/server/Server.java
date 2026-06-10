package assignment.server;

import assignment.database.DatabaseInitializer;

public class Server {
  // Function implementations go here
  public static void main(String[] args) {
    try {
      DatabaseInitializer.initialize();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
