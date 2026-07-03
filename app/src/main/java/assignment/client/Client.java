package assignment.client;

import assignment.client.context.ClientContext;
import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.client.ui.menus.ClinicAdministratorMenu;
import assignment.client.ui.menus.DoctorMenu;
import assignment.client.ui.menus.PatientMenu;
import assignment.client.ui.menus.ReceptionistMenu;
import assignment.shared.auth.Role;
import assignment.shared.config.Config;
import assignment.shared.dto.LoginResponse;
import assignment.shared.services.AuthService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.mindrot.jbcrypt.BCrypt;

public class Client {
    public static void main(String[] args) {
//        System.out.println(BCrypt.hashpw("hehehehaw", BCrypt.gensalt()));

        try {
            ServiceManager serviceManager = new ServiceManager();
            System.out.println("connected to server");

            ClientContext currentContext = new ClientContext(serviceManager);

//            Registry registry = LocateRegistry.getRegistry(Config.SERVER_HOST, Config.SERVER_REGISTRY_PORT);
//            AuthService authService = (AuthService) registry.lookup("AuthService");
//            System.out.println("Ok connected to server");

//            String token = authService.login("michael@gmail.com", "1234");



            boolean running = true;
            while (running)
            {
                System.out.println("\n=================================");
                System.out.println("  WELCOME TO CLINIC MANAGEMENT   ");
                System.out.println("=================================");
                System.out.println("1. Login to Account");
                System.out.println("2. Exit System");
                System.out.print("Select an option: ");

                int choice = InputHandler.readInt("");

                switch (choice)
                {
                    case 1:
                        handleLogin(currentContext);
                        break;
                    case 2:
                        System.out.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            //Edward old login stuff
//      System.out.println(BCrypt.hashpw("Choong The Wanking Shawn", BCrypt.gensalt()));
//      String token = authService.login("michael", "Choong The Wanking Shawn");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleLogin(ClientContext currentContext)
    {
        System.out.println("\n--- Account Login ---");

        String enteredEmail = InputHandler.readLine("Enter Email: ");
        String enteredPassword = InputHandler.readLine("Enter Password: ");

//        System.out.println(BCrypt.hashpw("Choong The Wanking Shawn", BCrypt.gensalt()));
//      String token = authService.login("michael", "Choong The Wanking Shawn");

        try {
            // Ok here password hashing
//            System.out.println(BCrypt.hashpw(enteredPassword, BCrypt.gensalt()));
            LoginResponse sessionData = currentContext.getServices().login(enteredEmail, enteredPassword);
            System.out.println(sessionData);

            if (sessionData != null)
            {
                currentContext.setSession(sessionData);
                System.out.println("ok the login worked");

                Role role = sessionData.getRole();

               switch(role)
               {
                   case ADMIN ->
                   {
                       System.out.println("THe role is admin pyramid glasses guy");
                       ClinicAdministratorMenu.displayMenu(currentContext.getServices(), currentContext.getSession());
                   }
                   case DOCTOR -> {
                       System.out.println("the doctor role");
                       DoctorMenu.displayMenu(currentContext.getServices(), currentContext.getSession());
                   }
                   case RECEPTIONIST -> {
                       System.out.println("this lazy ahh havent implemented");
                       ReceptionistMenu.displayMenu(currentContext.getServices(), currentContext.getSession());
                   }
                   case PATIENT -> {
                       System.out.println("edwardih");
                       PatientMenu.displayMenu(currentContext.getServices(), currentContext.getSession());
                   }
               }

                //basically a logout
                currentContext.clearSession();
                System.out.println("the session is GONE");

            } else {
                System.out.println("Login Failed: Server returned no session authorization data.");
            }



        } catch (Exception e) {
            System.out.println("An authentication error occurred: " + e.getMessage());
        }

        //------------------------------------------------------------------------------

    }
}
