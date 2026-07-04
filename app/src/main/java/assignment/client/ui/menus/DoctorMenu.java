package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.client.ui.screens.ManageScheduleScreen;
import assignment.shared.dto.LoginResponse;

public class DoctorMenu {
  public static void displayMenu(ServiceManager client, LoginResponse session) {
    while (true) {
      System.out.println("Hello " + session.getFirstName() + " " + session.getLastName());
      System.out.println("\n=== Doctor Menu ===");
      System.out.println("[1]. View Patient Appointments");
      System.out.println("[2]. Update Notes of a Consultation");
      System.out.println("[3]. View Medical History of Patient");
      System.out.println("[4]. Manage Available Time");
      System.out.println("[5]. Exit");

      int choice = InputHandler.readInt("Select an option: ");
      if (choice == 1) {
        //                RegisterUserScreen.display(client);
        System.out.println("i dunno");
      } else if (choice == 2) {
        //                EditUserScreen.display(client);
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
