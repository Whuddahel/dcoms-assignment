package assignment.client;

import assignment.client.ui.InputHandler;
import assignment.shared.config.Config;
import assignment.shared.model.User;
import assignment.shared.services.AuthService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.mindrot.jbcrypt.BCrypt;

public class Client {
    public static void main(String[] args) {
//        System.out.println(BCrypt.hashpw("1234", BCrypt.gensalt()));

        try {
            Registry registry = LocateRegistry.getRegistry(Config.SERVER_HOST, Config.SERVER_REGISTRY_PORT);
            AuthService authService = (AuthService) registry.lookup("AuthService");
            System.out.println("Ok connected to server");

            String token = authService.login("michael@gmail.com", "$2a$10$FfXryxc7O.rzccqjn4LrK.ccX9vo82EK8RyxhFPh2MNMlfLU2JmR.");


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
                        handleLogin(authService);
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

    private static void handleLogin(AuthService authService)
    {
        System.out.println("\n--- Account Login ---");

        String enteredEmail = InputHandler.readLine("Enter Email: ");
        String enteredPassword = InputHandler.readLine("Enter Password: ");

//        System.out.println(BCrypt.hashpw("Choong The Wanking Shawn", BCrypt.gensalt()));
//      String token = authService.login("michael", "Choong The Wanking Shawn");

        try {
            // Ok here password hashing
            System.out.println(BCrypt.hashpw(enteredPassword, BCrypt.gensalt()));
            String token = authService.login(enteredEmail, enteredPassword);
            System.out.println(token);





        } catch (Exception e) {
            System.out.println("An authentication error occurred: " + e.getMessage());
        }

        //------------------------------------------------------------------------------

    }
}
