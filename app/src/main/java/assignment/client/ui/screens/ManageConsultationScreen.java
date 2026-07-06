package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Consultation;
import java.util.List;

public class ManageConsultationScreen {

    public static void display(ServiceManager client, LoginResponse session) {

        while (true) {
            System.out.println("\n=== Manage Consultations ===");
            System.out.println("[1]. View All Consultations");
            System.out.println("[2]. Record New Consultation");
            System.out.println("[3]. Modify Consultation Notes");
            System.out.println("[4]. Return to Menu");

            int choice = InputHandler.readInt("Select an option: ");

            if (choice == 1) {
                showConsultations(client);
            } else if (choice == 2) {
                addNewConsultation(client);
            } else if (choice == 3) {
                modifyConsultation(client);
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void showConsultations(ServiceManager client) {
        try {
            List<Consultation> consultations = client.getAllConsultations();
            System.out.println("\n--- Registered Clinic Consultations ---");
            if (consultations == null || consultations.isEmpty()) {
                System.out.println("(No consultations found)");
                return;
            }
            for (Consultation con : consultations) {
                System.out.println(
                    "ID: ["
                        + con.getConsultationId()
                        + "] | Appt ID: "
                        + con.getAppointmentId()
                        + " | Fee: $"
                        + String.format("%.2f", con.getFee())
                        + " | Notes: "
                        + con.getContent());
            }
        } catch (Exception e) {
            System.err.println("Error pulling consultations: " + e.getMessage());
        }
    }

    private static void addNewConsultation(ServiceManager client) {
        System.out.println("\n--- Record New Consultation ---");

        int appointmentId = InputHandler.readInt("Enter Appointment ID: ");
        String content = InputHandler.readLine("Enter Clinical Notes/Summary: ");

        System.out.print("Enter Fee ($): ");
        double fee = 0.0;
        try {
            fee = Double.parseDouble(InputHandler.readLine(""));
        } catch (NumberFormatException e) {
            System.out.println("Enter a valid number!");
            return;
        }

        try {
            Consultation consultation = new Consultation(0, appointmentId, content, fee, null);
            boolean success = client.addConsultation(consultation);

            if (success) {
                System.out.println("Consultation recorded successfully!");
            } else {
                System.out.println("Failed to save consultation summary.");
            }
        } catch (Exception e)
        {
            if (e.getMessage() != null && e.getMessage().contains("DUPLICATE_ERROR"))
            {
                System.out.println("A consultation has already been recorded for Appointment ID " + appointmentId + ".");
            }
            else if (e.getMessage() != null && e.getMessage().contains("INVALID_ID_ERROR"))
            {
                System.out.println("Appointment ID " + appointmentId + " does not exist in the system.");
            }
            else
            {
                System.out.println("Operation Cancelled");
            }
        }
    }

    private static void modifyConsultation(ServiceManager client) {
        System.out.println("\n--- Modify Existing Consultation Notes ---");
        int consultationId = InputHandler.readInt("Enter Consultation ID to edit: ");

        try {
            List<Consultation> consultations = client.getAllConsultations();
            Consultation target = null;

            for (Consultation con : consultations) {
                if (con.getConsultationId() == consultationId) {
                    target = con;
                    break;
                }
            }

            if (target == null) {
                System.out.println("No consultation found with that ID.");
                return;
            }

            System.out.println("Current Notes: " + target.getContent());
            String newContent = InputHandler.readLine("Enter New Clinical Notes: ");

            Consultation updated = new Consultation(
                    target.getConsultationId(),
                    target.getAppointmentId(),
                    newContent,
                    target.getFee(),
                    target.getCreatedAt()
            );

            boolean success = client.updateConsultation(updated);
            if (success) {
                System.out.println("Consultation notes modified successfully!");
            } else {
                System.out.println("Failed to modify consultation notes.");
            }

        } catch (Exception e) {
            System.err.println("Error modifying consultation: " + e.getMessage());
        }
    }
}