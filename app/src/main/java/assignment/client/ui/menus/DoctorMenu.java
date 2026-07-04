package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.screens.ManageScheduleScreen;
import assignment.shared.dto.LoginResponse;

public class DoctorMenu {
  public static void displayMenu(ServiceManager client, LoginResponse session) {
    while (true) {
      System.out.println("\nHello " + session.getFirstName() + " " + session.getLastName());
      Helper.printBanner("Doctor Menu", Helper.Theme.BLUE);
      Helper.printOption(1, "View Patient Appointments", Helper.Theme.BLUE);
      Helper.printOption(2, "Update Notes of a Consultation", Helper.Theme.BLUE);
      Helper.printOption(3, "View Medical History of Patient", Helper.Theme.BLUE);
      Helper.printOption(4, "Manage Available Time", Helper.Theme.BLUE);
      Helper.printLine("[5]. Logout", Helper.Theme.RED);

      int choice = InputHandler.readInt("Select an option: ");
      if (choice == 1) {
        // RegisterUserScreen.display(client);
        System.out.println("i dunno");
      } else if (choice == 2) {
        // EditUserScreen.display(client);
        System.out.println("I havent do");
      } else if (choice == 3) {
        System.out.println("I havent do");
      } else if (choice == 4) {
        ManageScheduleScreen.display(client, session);
        System.out.println("I havent do");
      } else if (choice == 5) {
        System.out.println("Exiting Doctor Menu...");
        break;
      } else {
        System.out.println("Invalid option. Please try again.");
      }
    }
  }
}
