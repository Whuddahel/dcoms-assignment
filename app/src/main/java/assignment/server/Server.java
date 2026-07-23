package assignment.server;

import assignment.server.database.DatabaseInitializer;
import assignment.server.database.repository.UserRepository;

public class Server {
  public static void main(String[] args) {
    System.setProperty("java.rmi.server.hostname", System.getenv("SERVER_HOST"));

    // Set SSL KeyStore properties
    String keyStorePath = System.getenv("SSL_KEYSTORE_PATH");
    String keyStorePassword = System.getenv("SSL_KEYSTORE_PASSWORD");
    if (keyStorePath != null && keyStorePassword != null) {
      System.setProperty("javax.net.ssl.keyStore", keyStorePath);
      System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    }

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
