package assignment.client.ui.menus;

import assignment.client.ClinicClient;
import assignment.client.ui.InputHandler;
import assignment.client.ui.screens.EditUserScreen;
import assignment.client.ui.screens.RegisterUserScreen;

/**
 * ClinicAdministratorMenu renders the navigation menu for clinic administrators, routing tasks to
 * their respective screen classes.
 */
public class ClinicAdministratorMenu {

  public static void displayMenu(ClinicClient client) {
    while (true) {
      System.out.println("\n=== Clinic Administrator Menu ===");
      System.out.println("[1]. Register User");
      System.out.println("[2]. Edit User");
      System.out.println("[3]. Exit");

      int choice = InputHandler.readInt("Select an option: ");
      if (choice == 1) {
        RegisterUserScreen.display(client);
      } else if (choice == 2) {
        EditUserScreen.display(client);
      } else if (choice == 3) {
        System.out.println("Exiting Clinic Administrator Menu...");
        break;
      } else {
        System.out.println("Invalid option. Please try again.");
      }
    }
  }
}
