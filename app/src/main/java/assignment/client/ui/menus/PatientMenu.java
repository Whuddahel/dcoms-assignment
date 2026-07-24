package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.screens.AppointmentScreen;
import assignment.client.ui.screens.ConsultationScreen;
import assignment.client.ui.screens.ProfileScreen;
import assignment.shared.dto.LoginResponse;

public class PatientMenu {
  public static void displayMenu(ServiceManager client, LoginResponse session) {
    while (true) {
      Helper.printBanner("Patient Menu", Helper.Theme.BLUE);
      Helper.printOption(1, "Update Personal Information", Helper.Theme.BLUE);
      Helper.printOption(2, "View Upcoming Appointments/Cancel", Helper.Theme.BLUE);
      Helper.printOption(3, "Book Appointment", Helper.Theme.BLUE);
      Helper.printOption(4, "View Past Appointments", Helper.Theme.BLUE);
      Helper.printOption(5, "View Consultation Notes", Helper.Theme.BLUE);
      Helper.printLine("[6]. Logout", Helper.Theme.RED);

      int choice = InputHandler.readInt("Select an option: ");
      if (choice == 1) {
        ProfileScreen.updatePersonalInfo(client, session);
      } else if (choice == 2) {
        AppointmentScreen.viewUpcomingAppointments(client, session);
      } else if (choice == 3) {
        AppointmentScreen.bookAppointment(client, session);
      } else if (choice == 4) {
        AppointmentScreen.viewAppointmentHistory(client, session);
      } else if (choice == 5) {
        ConsultationScreen.viewConsultationNotes(client, session);
      } else if (choice == 6) {
        System.out.println("Exiting Patient Menu...");
        break;
      } else {
        System.out.println("Invalid option. Please try again.");
      }
    }
  }
}
