package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.client.ui.PrintHelper;
import assignment.shared.dto.ConsultationView;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Consultation;
import java.util.ArrayList;
import java.util.List;

public class ManageConsultationScreen {

  public static void display(ServiceManager client, LoginResponse session) {
    while (true) {
      System.out.println("\n=== Manage Consultations ===");

      try {
        List<Consultation> consultations = client.getAllConsultations();
        if (consultations == null || consultations.isEmpty()) {
          System.out.println("(No consultations found)");
          InputHandler.readLine("Press Enter to return: ");
          break;
        }

        List<ConsultationView> views = new ArrayList<>();
        for (Consultation c : consultations) {
          views.add(
              new ConsultationView(
                  c.getConsultationId(),
                  c.getAppointmentId(),
                  c.getCreatedAt() != null ? c.getCreatedAt().toString().substring(0, 10) : "",
                  "",
                  "",
                  c.getFee(),
                  c.getContent()));
        }

        String input =
            PrintHelper.printConsultationList(
                views, 0, "Select a Consultation Record to Edit (or 'cancel' to return): ");
        if (input.equalsIgnoreCase("cancel") || input.isEmpty()) {
          break;
        }

        int consultationId = Integer.parseInt(input);
        Consultation target = null;

        for (Consultation con : consultations) {
          if (con.getConsultationId() == consultationId) {
            target = con;
            break;
          }
        }

        if (target == null) {
          System.out.println("No consultation found with that ID.");
          continue;
        }

        ConsultationScreen.updateConsultationDetail(client, target);

      } catch (NumberFormatException e) {
        System.out.println("Invalid selection. Please enter a valid number or 'cancel'.");
      } catch (Exception e) {
        System.err.println("Error pulling consultations: " + e.getMessage());
        break;
      }
    }
  }
}
