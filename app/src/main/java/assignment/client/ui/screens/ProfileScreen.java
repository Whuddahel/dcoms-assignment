package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Patient;

public class ProfileScreen {

  public static void updatePersonalInfo(ServiceManager client, LoginResponse session) {
    try {
      Patient patient = client.getPatientByUserId(session.getUserId());
      if (patient == null) {
        Helper.printLine("Could not load your profile. Please try again later.", Helper.Theme.RED);
        return;
      }

      while (true) {
        Helper.printBanner("Your Profile", Helper.Theme.CYAN);
        System.out.printf("  First Name:    %s\n", patient.getFirstName());
        System.out.printf("  Last Name:     %s\n", patient.getLastName());
        System.out.printf("  Email:         %s\n", patient.getEmail());
        System.out.printf("  Contact No:    %s\n", patient.getContactNumber());
        System.out.printf("  IC/Passport:   %s\n", patient.getIcPassportNo());

        System.out.println("\nSelect a field to edit:");
        Helper.printOption(1, "First Name: " + patient.getFirstName(), Helper.Theme.BLUE);
        Helper.printOption(2, "Last Name: " + patient.getLastName(), Helper.Theme.BLUE);
        Helper.printOption(3, "Contact Number: " + patient.getContactNumber(), Helper.Theme.BLUE);
        Helper.printLine("[4]. Finish editing", Helper.Theme.GREEN);

        int choice = InputHandler.readInt("Select choice: ");

        if (choice == 4) {
          return;
        } else if (choice == 1 || choice == 2) {
          String fieldName = choice == 1 ? "First Name" : "Last Name";
          String val = InputHandler.readLine("Enter new " + fieldName + ": ", true);
          if (val.isEmpty()) {
            System.out.println(fieldName + " cannot be empty.");
            continue;
          }
          String newFirst = choice == 1 ? val : patient.getFirstName();
          String newLast = choice == 2 ? val : patient.getLastName();
          Patient updated =
              new Patient(
                  patient.getPatientId(),
                  patient.getUserId(),
                  newFirst,
                  newLast,
                  patient.getUserRole(),
                  patient.getIcPassportNo(),
                  patient.getEmail(),
                  patient.getPasswordHash(),
                  patient.getMedicalRecordId(),
                  patient.getContactNumber(),
                  patient.getCreatedAt(),
                  patient.isDeleted());
          patient = executeProfileUpdate(client, updated, patient);
        } else if (choice == 3) {
          String val = InputHandler.readLine("Enter new Contact Number: ", true);
          if (val.isEmpty()) {
            System.out.println("Contact Number cannot be empty.");
            continue;
          }
          Patient updated =
              new Patient(
                  patient.getPatientId(),
                  patient.getUserId(),
                  patient.getFirstName(),
                  patient.getLastName(),
                  patient.getUserRole(),
                  patient.getIcPassportNo(),
                  patient.getEmail(),
                  patient.getPasswordHash(),
                  patient.getMedicalRecordId(),
                  val,
                  patient.getCreatedAt(),
                  patient.isDeleted());
          patient = executeProfileUpdate(client, updated, patient);
        } else {
          System.out.println("Invalid choice.");
        }
      }
    } catch (Exception e) {
      System.err.println("Error loading profile: " + e.getMessage());
    }
  }

  private static Patient executeProfileUpdate(
      ServiceManager client, Patient updated, Patient current) {
    try {
      boolean success = client.updatePatientProfile(updated);
      if (success) {
        Helper.printLine("Profile updated successfully!", Helper.Theme.GREEN);
        return updated;
      } else {
        Helper.printLine("Update failed on server.", Helper.Theme.RED);
      }
    } catch (Exception e) {
      System.err.println("Error updating profile: " + e.getMessage());
    }
    return current;
  }
}
