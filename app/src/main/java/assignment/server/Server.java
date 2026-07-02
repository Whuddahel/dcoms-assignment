package assignment.server;

import assignment.server.database.DatabaseInitializer;
import assignment.server.database.UserRepository;

public class Server {
  public static void main(String[] args) {
    try {
      DatabaseInitializer.initialize();
      UserRepository.listAllUsers();

      Register.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Server running...");
  }
}
