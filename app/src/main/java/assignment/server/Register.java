package assignment.server;

import assignment.server.services.AuthServiceImplementation;
import assignment.server.services.EditUserServiceImplementation;
import assignment.server.services.ManageConsultationServiceImplementation;
import assignment.server.services.ManageScheduleServiceImplementation;
import assignment.server.services.RegisterUserServiceImplementation;
import assignment.shared.services.AuthService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Register {

  public static void start() {
    try {
      String portStr = System.getenv("SERVER_REGISTRY_PORT");
      if (portStr == null || portStr.isEmpty()) {
        throw new IllegalStateException("SERVER_REGISTRY_PORT environment variable is not set.");
      }
      int port;
      try {
        port = Integer.parseInt(portStr);
      } catch (NumberFormatException e) {
        throw new IllegalStateException(
            "SERVER_REGISTRY_PORT is not a valid integer: " + portStr, e);
      }
      // Start RMI registry
      Registry registry = LocateRegistry.createRegistry(port);
      System.out.println("RMI Registry started on port " + port);

      // Create service
      AuthService authService = new AuthServiceImplementation();
      RegisterUserServiceImplementation registerUserService =
          new RegisterUserServiceImplementation();
      EditUserServiceImplementation editUserService = new EditUserServiceImplementation();
      ManageScheduleServiceImplementation manageScheduleService =
          new ManageScheduleServiceImplementation();
      ManageConsultationServiceImplementation manageConsultationService =
          new ManageConsultationServiceImplementation();

      // Bind service
      registry.rebind("AuthService", authService);
      registry.rebind("RegisterUserService", registerUserService);
      registry.rebind("EditUserService", editUserService);
      registry.rebind("ManageScheduleService", manageScheduleService);
      registry.rebind("ManageConsultationService", manageConsultationService);

      System.out.println("AuthService bound successfully");

    } catch (Exception e) {
      System.err.println("Failed to start RMI Registry");
      e.printStackTrace();
    }
  }
}
