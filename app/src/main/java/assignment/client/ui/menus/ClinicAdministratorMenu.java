package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.screens.EditUserScreen;
import assignment.client.ui.screens.RegisterUserScreen;
import assignment.client.ui.screens.ReportScreen;
import assignment.shared.dto.LoginResponse;

/**
 * ClinicAdministratorMenu renders the navigation menu for clinic administrators, routing tasks to
 * their respective screen classes.
 */
public class ClinicAdministratorMenu {

  public static void displayMenu(ServiceManager client, LoginResponse session) {
    while (true) {
      Helper.printBanner("Clinic Administrator Menu", Helper.Theme.BLUE);
      Helper.printOption(1, "Register User", Helper.Theme.BLUE);
      Helper.printOption(2, "Edit User", Helper.Theme.BLUE);
      Helper.printOption(3, "Generate Monthly Appointment Report", Helper.Theme.BLUE);
      Helper.printOption(4, "Generate Doctor Consultation Report", Helper.Theme.BLUE);
      Helper.printOption(5, "Generate Patient Visit Summary", Helper.Theme.BLUE);
      Helper.printLine("[6]. Logout", Helper.Theme.RED);

      int choice = InputHandler.readInt("Select an option: ");
      if (choice == 1) {
        RegisterUserScreen.display(client);
      } else if (choice == 2) {
        EditUserScreen.display(client);
      } else if (choice == 3) {
        ReportScreen.displayMonthlyAppointmentReport(client);
      } else if (choice == 4) {
        ReportScreen.displayDoctorConsultationReport(client);
      } else if (choice == 5) {
        ReportScreen.displayPatientVisitSummary(client);
      } else if (choice == 6) {
        System.out.println("Exiting Clinic Administrator Menu...");
        break;
      } else {
        System.out.println("Invalid option. Please try again.");
      }
    }
  }
}
