package assignment.server;

import assignment.server.services.AuthServiceImplementation;
import assignment.shared.AuthService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRegistry {

  public static void start() {
    try {
      // Start RMI registry
      Registry registry = LocateRegistry.createRegistry(1099);
      System.out.println("RMI Registry started on port 1099");

      // Create service
      AuthService authService = new AuthServiceImplementation();

      // Bind service
      registry.rebind("AuthService", authService);

      System.out.println("AuthService bound successfully");

    } catch (Exception e) {
      System.err.println("Failed to start RMI Registry");
      e.printStackTrace();
    }
  }
}
