package assignment.server;

import assignment.server.database.DatabaseInitializer;
import assignment.server.database.repository.UserRepository;
import assignment.shared.config.Config;

public class Server {
  public static void main(String[] args) {
    System.setProperty("java.rmi.server.hostname", Config.SERVER_HOST);

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
