package assignment.server;

import assignment.server.services.AuthServiceImplementation;
import assignment.shared.config.Config;
import assignment.shared.services.AuthService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Register {

  public static void start() {
    try {
      // Start RMI registry
      Registry registry = LocateRegistry.createRegistry(Config.SERVER_REGISTRY_PORT);
      System.out.println("RMI Registry started on port " + Config.SERVER_REGISTRY_PORT);

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
