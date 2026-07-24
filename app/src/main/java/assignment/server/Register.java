package assignment.server;

import assignment.server.services.AuthServiceImplementation;
import assignment.server.services.DoctorServiceImplementation;
import assignment.server.services.EditUserServiceImplementation;
import assignment.server.services.ManageConsultationServiceImplementation;
import assignment.server.services.ManageScheduleServiceImplementation;
import assignment.server.services.PatientServiceImplementation;
import assignment.server.services.RegisterUserServiceImplementation;
import assignment.server.services.ReportServiceImplementation;
import assignment.shared.services.AuthService;
import assignment.shared.services.ReportService;
import assignment.shared.ssl.LenientSslRMIClientSocketFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.rmi.ssl.SslRMIServerSocketFactory;

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
      // Start RMI registry with SSL factories
      Registry registry =
          LocateRegistry.createRegistry(
              port,
              new LenientSslRMIClientSocketFactory(),
              new SslRMIServerSocketFactory(null, null, false));
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
      ReportService reportService = new ReportServiceImplementation();
      PatientServiceImplementation patientService = new PatientServiceImplementation();
      DoctorServiceImplementation doctorService = new DoctorServiceImplementation();

      // Bind service
      registry.rebind("AuthService", authService);
      System.out.println("AuthService bound successfully");

      registry.rebind("RegisterUserService", registerUserService);
      System.out.println("RegisterUserService bound successfully");

      registry.rebind("EditUserService", editUserService);
      System.out.println("EditUserService bound successfully");

      registry.rebind("ManageScheduleService", manageScheduleService);
      System.out.println("ManageScheduleService bound successfully");

      registry.rebind("ManageConsultationService", manageConsultationService);
      System.out.println("ManageConsultationService bound successfully");

      registry.rebind("ReportService", reportService);
      System.out.println("ReportService bound successfully");

      registry.rebind("PatientService", patientService);
      System.out.println("PatientService bound successfully");

      registry.rebind("DoctorService", doctorService);
      System.out.println("DoctorService bound successfully");

    } catch (Exception e) {
      System.err.println("Failed to start RMI Registry");
      e.printStackTrace();
    }
  }
}
