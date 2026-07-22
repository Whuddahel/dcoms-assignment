package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.screens.AppointmentScreen;
import assignment.client.ui.screens.ManageConsultationScreen;
import assignment.client.ui.screens.ManageScheduleScreen;
import assignment.client.ui.screens.MedicalHistoryScreen;
import assignment.shared.dto.LoginResponse;

public class DoctorMenu {
  public static void displayMenu(ServiceManager client, LoginResponse session) {
    while (true) {
      Helper.printBanner("Doctor Menu", Helper.Theme.BLUE);
      Helper.printOption(1, "View Patient Appointments", Helper.Theme.BLUE);
      Helper.printOption(2, "Manage Consultations", Helper.Theme.BLUE);
      Helper.printOption(3, "View Medical History of Patient", Helper.Theme.BLUE);
      Helper.printOption(4, "Manage Available Time", Helper.Theme.BLUE);
      Helper.printLine("[5]. Logout", Helper.Theme.RED);

      int choice = InputHandler.readInt("Select an option: ");
      if (choice == 1) {
        AppointmentScreen.viewPatientAppointments(client, session);
      } else if (choice == 2) {
        System.out.println("Entering Consultation Management");
        ManageConsultationScreen.display(client, session);
      } else if (choice == 3) {
        MedicalHistoryScreen.viewMedicalHistoryOfPatient(client, session);
      } else if (choice == 4) {
        System.out.println("Entering Schedule Management");
        ManageScheduleScreen.display(client, session);
      } else if (choice == 5) {
        System.out.println("Exiting Doctor Menu...");
        break;
      } else {
        System.out.println("Invalid option. Please try again.");
      }
    }
  }
}
