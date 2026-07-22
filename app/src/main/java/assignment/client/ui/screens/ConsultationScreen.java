package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.PrintHelper;
import assignment.shared.dto.ConsultationView;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Doctor;
import java.util.ArrayList;
import java.util.List;

public class ConsultationScreen {

  public static void initiateConsultation(ServiceManager client, int appointmentId) {
    System.out.println("\n--- Record New Consultation ---");
    System.out.println("For Appointment ID: " + appointmentId);
    String content = InputHandler.readLine("Enter Clinical Notes/Summary: ");

    System.out.print("Enter Fee ($): ");
    double fee = 0.0;
    try {
      fee = Double.parseDouble(InputHandler.readLine("", false));
    } catch (NumberFormatException e) {
      System.out.println("Enter a valid number!");
      return;
    }

    try {
      Consultation consultation = new Consultation(0, appointmentId, content, fee, null);
      boolean success = client.addConsultation(consultation);

      if (success) {
        Helper.printLine("Consultation recorded successfully!", Helper.Theme.GREEN);
      } else {
        Helper.printLine("Failed to save consultation summary.", Helper.Theme.RED);
      }
    } catch (Exception e) {
      if (e.getMessage() != null && e.getMessage().contains("DUPLICATE_ERROR")) {
        System.out.println(
            "A consultation has already been recorded for Appointment ID " + appointmentId + ".");
      } else if (e.getMessage() != null && e.getMessage().contains("INVALID_ID_ERROR")) {
        System.out.println("Appointment ID " + appointmentId + " does not exist in the system.");
      } else {
        System.out.println("Operation Cancelled: " + e.getMessage());
      }
    }
  }

  public static void updateConsultationDetail(ServiceManager client, Consultation target) {
    System.out.println("\n--- Modify Existing Consultation Notes ---");
    try {
      System.out.println("Current Notes: " + target.getContent());
      String newContent =
          InputHandler.readLine(
              "Enter New Clinical Notes (or leave empty to keep current): ", true);
      if (newContent.isEmpty()) {
        newContent = target.getContent();
      }

      System.out.println("Fee (Fixed): $" + String.format("%.2f", target.getFee()));

      Consultation updated =
          new Consultation(
              target.getConsultationId(),
              target.getAppointmentId(),
              newContent,
              target.getFee(),
              target.getCreatedAt());

      boolean success = client.updateConsultation(updated);
      if (success) {
        Helper.printLine("Consultation notes modified successfully!", Helper.Theme.GREEN);
        // Mutate target locally to reflect changes immediately
        target = updated;
      } else {
        Helper.printLine("Failed to modify consultation notes.", Helper.Theme.RED);
      }
    } catch (Exception e) {
      System.err.println("Error modifying consultation: " + e.getMessage());
    }
  }

  public static void viewConsultationNotes(ServiceManager client, LoginResponse session) {
    try {
      List<Consultation> consultations = client.getConsultationsByPatient(session.getUserId());
      if (consultations == null || consultations.isEmpty()) {
        Helper.printBanner("Consultation Notes", Helper.Theme.CYAN);
        System.out.println("You have no consultation records.");
        return;
      }

      Helper.printBanner("Consultation Notes", Helper.Theme.CYAN);
      List<ConsultationView> views = new ArrayList<>();
      for (Consultation c : consultations) {
        String doctorName = "Unknown";
        try {
          Appointment a = findAppointmentForConsultation(client, c, session.getUserId());
          if (a != null) {
            doctorName = resolveDoctorName(client, a.getDoctorId());
          }
        } catch (Exception ignored) {
          // Silently fall back to "Unknown" if lookup fails
        }

        views.add(
            new ConsultationView(
                c.getConsultationId(),
                c.getAppointmentId(),
                c.getCreatedAt() != null ? c.getCreatedAt().toString().substring(0, 10) : "N/A",
                doctorName,
                "",
                c.getFee(),
                ""));
      }

      String input =
          PrintHelper.printConsultationList(
              views, 0, "Select a consultation to view (or 'cancel' to return): ");
      if (input.equalsIgnoreCase("cancel") || input.isEmpty()) {
        return;
      }

      Consultation selected = null;
      try {
        int consultationId = Integer.parseInt(input);
        for (Consultation c : consultations) {
          if (c.getConsultationId() == consultationId) {
            selected = c;
            break;
          }
        }
      } catch (NumberFormatException e) {
      }

      if (selected == null) {
        System.out.println("Invalid selection.");
        return;
      }
      Helper.printBanner("Consultation Detail", Helper.Theme.CYAN);
      System.out.printf("Consultation ID: %d\n", selected.getConsultationId());
      System.out.printf("Appointment ID:  %d\n", selected.getAppointmentId());
      System.out.printf("Date:            %s\n", selected.getCreatedAt());
      System.out.printf("Fee:             $%.2f\n", selected.getFee());
      System.out.println("--- Notes ---");
      System.out.println(selected.getContent());
      System.out.println("-------------");

    } catch (Exception e) {
      System.err.println("Error loading consultation notes: " + e.getMessage());
    }
  }

  private static Appointment findAppointmentForConsultation(
      ServiceManager client, Consultation consultation, int userId) throws Exception {
    // We need to find the appointment from the patient's history
    List<Appointment> pastAppointments = client.getPastAppointments(userId);
    List<Appointment> upcomingAppointments = client.getUpcomingAppointments(userId);
    for (Appointment a : pastAppointments) {
      if (a.getAppointmentId() == consultation.getAppointmentId()) {
        return a;
      }
    }
    for (Appointment a : upcomingAppointments) {
      if (a.getAppointmentId() == consultation.getAppointmentId()) {
        return a;
      }
    }
    return null;
  }

  private static String resolveDoctorName(ServiceManager client, int doctorId) {
    try {
      Doctor doc = client.getDoctorByIdForPatient(doctorId);
      if (doc != null) {
        return doc.getFirstName() + " " + doc.getLastName();
      }
    } catch (Exception ignored) {
      // Fall through to default
    }
    return "Unknown (ID: " + doctorId + ")";
  }
}
