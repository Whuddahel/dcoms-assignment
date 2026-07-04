package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.shared.dto.LoginResponse;

public class ReceptionistMenu {
  public static void displayMenu(ServiceManager client, LoginResponse session) {
    while (true) {
      System.out.println("Hello " + session.getFirstName() + " " + session.getLastName());
      System.out.println("\n=== Receptionist Menu ===");
      System.out.println("[1]. Register Patient");
      System.out.println("[2]. Exit");

      int choice = InputHandler.readInt("Select an option: ");
      if (choice == 1) {
        System.out.println("an iq too low?");
      } else if (choice == 2) {
        System.out.println("Exiting Receptionist Menu...");
        break;
      } else {
        System.out.println("Invalid option. Please try again.");
      }
    }
  }
}
