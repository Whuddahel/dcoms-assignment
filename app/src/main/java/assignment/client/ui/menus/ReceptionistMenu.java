package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.shared.dto.LoginResponse;

public class ReceptionistMenu {
  public static void displayMenu(ServiceManager client, LoginResponse session) {
    while (true) {
      System.out.println("\nHello " + session.getFirstName() + " " + session.getLastName());
      Helper.printBanner("Receptionist Menu", Helper.Theme.BLUE);
      Helper.printOption(1, "Register Patient", Helper.Theme.BLUE);
      Helper.printLine("[2]. Logout", Helper.Theme.RED);

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
