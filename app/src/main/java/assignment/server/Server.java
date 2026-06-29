package assignment.server;

import assignment.database.DatabaseInitializer;
import assignment.database.UserRepository;

public class Server {
  public static void main(String[] args) {
    try {
      DatabaseInitializer.initialize();
      UserRepository.listAllUsers();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
