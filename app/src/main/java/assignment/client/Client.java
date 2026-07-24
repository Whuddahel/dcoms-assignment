package assignment.client;

import assignment.client.context.ClientContext;
import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.menus.ClinicAdministratorMenu;
import assignment.client.ui.menus.DoctorMenu;
import assignment.client.ui.menus.PatientMenu;
import assignment.client.ui.menus.ReceptionistMenu;
import assignment.shared.auth.Role;
import assignment.shared.dto.LoginResponse;

public class Client {
  public static void main(String[] args) {
    // Enable SSL debugging (Only uncomment code below when you need to proof that
    // the TLS is working)
    // System.setProperty("javax.net.debug", "ssl:handshake");
    // Set SSL TrustStore properties
    String trustStorePath = System.getenv("SSL_TRUSTSTORE_PATH");
    String trustStorePassword = System.getenv("SSL_TRUSTSTORE_PASSWORD");
    if (trustStorePath != null && trustStorePassword != null) {
      System.setProperty("javax.net.ssl.trustStore", trustStorePath);
      System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }

    try {
      ServiceManager serviceManager = new ServiceManager();
      System.out.println("connected to server");

      ClientContext currentContext = new ClientContext(serviceManager);

      boolean running = true;
      while (running) {
        System.out.print("\n");
        Helper.printBanner("BRIGHTCARE MEDICAL SYSTEM", Helper.Theme.BLUE);
        System.out.print("\n");
        System.out.println("Enter Email:    _______________");
        System.out.println("Enter Password: _______________");
        System.out.println("\n");
        System.out.println("(Type 'exit' to terminate)");
        String enteredEmail = InputHandler.readLine("Enter Email: ");
        if (enteredEmail.equalsIgnoreCase("exit")) {
          System.out.println("Goodbye!");
          running = false;
          break;
        }
        String enteredPassword = InputHandler.readLine("Enter Password: ");

        try {
          LoginResponse sessionData = serviceManager.login(enteredEmail, enteredPassword);

          if (sessionData != null) {
            currentContext.setSession(sessionData);
            serviceManager.setToken(sessionData.getToken());
            Helper.printLine("Login successful!", Helper.Theme.GREEN);

            Role role = sessionData.getRole();

            switch (role) {
              case ADMIN -> {
                ClinicAdministratorMenu.displayMenu(
                    currentContext.getServices(), currentContext.getSession());
              }
              case DOCTOR -> {
                DoctorMenu.displayMenu(currentContext.getServices(), currentContext.getSession());
              }
              case RECEPTIONIST -> {
                ReceptionistMenu.displayMenu(
                    currentContext.getServices(), currentContext.getSession());
              }
              case PATIENT -> {
                PatientMenu.displayMenu(currentContext.getServices(), currentContext.getSession());
              }
            }

            // logout
            currentContext.clearSession();
            serviceManager.clearToken();
            System.out.println("Session closed successfully.");

          } else {
            Helper.printLine(
                "Login Failed: Server returned no session authorization data.", Helper.Theme.RED);
          }

        } catch (Exception e) {
          Helper.printLine("An authentication error occurred: " + e.getMessage(), Helper.Theme.RED);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
