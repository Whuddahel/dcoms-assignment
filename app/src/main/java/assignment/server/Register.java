package assignment.server;

import assignment.server.services.AuthServiceImplementation;
import assignment.server.services.EditUserServiceImplementation;
import assignment.server.services.ManageScheduleServiceImplementation;
import assignment.server.services.RegisterUserServiceImplementation;
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
      RegisterUserServiceImplementation registerUserService =
          new RegisterUserServiceImplementation();
      EditUserServiceImplementation editUserService = new EditUserServiceImplementation();
      ManageScheduleServiceImplementation manageScheduleService =
          new ManageScheduleServiceImplementation();

      // Bind service
      registry.rebind("AuthService", authService);
      registry.rebind("RegisterUserService", registerUserService);
      registry.rebind("EditUserService", editUserService);
      registry.rebind("ManageScheduleService", manageScheduleService);

      System.out.println("AuthService bound successfully");

    } catch (Exception e) {
      System.err.println("Failed to start RMI Registry");
      e.printStackTrace();
    }
  }
}
