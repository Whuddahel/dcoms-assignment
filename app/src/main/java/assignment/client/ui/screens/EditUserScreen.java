package assignment.client.ui.screens;

import assignment.client.ClinicClient;
import assignment.client.ui.InputHandler;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.Users;
import java.util.ArrayList;
import java.util.List;

/** EditUserScreen handles the workflow for viewing, searching, and editing existing users. */
public class EditUserScreen {

  public static void display(ClinicClient client) {
    try {
      List<Users> users = client.getAllUsers();
      if (users == null || users.isEmpty()) {
        System.out.println("No users found in the system.");
        return;
      }

      int currentPage = 0;
      boolean inSearchMode = false;

      while (true) {
        int totalUsers = users.size();
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        if (inSearchMode) {
          System.out.println("\n--- Search Mode ---");
          String query =
              InputHandler.readLine(
                  "Enter user Email or Full Name (or 'cancel' to return): ", true);
          if (query.equalsIgnoreCase("cancel")) {
            inSearchMode = false;
            continue;
          }
          if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            continue;
          }

          // Search in entire list
          List<Users> matches = new ArrayList<>();
          for (Users u : users) {
            if (u.getEmail().equalsIgnoreCase(query) || u.getFullName().equalsIgnoreCase(query)) {
              matches.add(u);
            }
          }
          if (matches.isEmpty()) {
            for (Users u : users) {
              if (u.getEmail().toLowerCase().contains(query.toLowerCase())
                  || u.getFullName().toLowerCase().contains(query.toLowerCase())) {
                matches.add(u);
              }
            }
          }

          if (matches.isEmpty()) {
            System.out.println("No matching user found.");
          } else if (matches.size() == 1) {
            editUserFields(client, matches.get(0), users);
            inSearchMode = false; // exit search mode after select
          } else {
            System.out.println("\nMultiple users found:");
            for (int i = 0; i < matches.size(); i++) {
              Users m = matches.get(i);
              System.out.printf(
                  "[%d]. %s - %s (%s)\n", i + 1, m.getUserRole(), m.getFullName(), m.getEmail());
            }
            String matchChoiceStr =
                InputHandler.readLine("Select a user by index (or 'cancel'): ", true);
            if (!matchChoiceStr.equalsIgnoreCase("cancel") && !matchChoiceStr.isEmpty()) {
              try {
                int matchChoice = Integer.parseInt(matchChoiceStr);
                if (matchChoice >= 1 && matchChoice <= matches.size()) {
                  editUserFields(client, matches.get(matchChoice - 1), users);
                  inSearchMode = false;
                } else {
                  System.out.println("Invalid selection.");
                }
              } catch (NumberFormatException e) {
                System.out.println("Invalid selection.");
              }
            }
          }
          continue;
        }

        // Standard display mode
        System.out.println("\n=== Edit User ===");
        int startIdx = currentPage * pageSize;
        int endIdx = Math.min(startIdx + pageSize, totalUsers);

        if (totalUsers > pageSize) {
          System.out.printf("Page %d of %d\n", currentPage + 1, totalPages);
        }

        for (int i = startIdx; i < endIdx; i++) {
          Users u = users.get(i);
          int indexOnPage = i - startIdx + 1;
          System.out.printf(
              "[%d]. %s - %s %s (%s)\n",
              indexOnPage, u.getUserRole(), u.getFirstName(), u.getLastName(), u.getEmail());
        }

        System.out.println("\nInput settings:");
        if (totalUsers > pageSize) {
          System.out.println("  \"<\" or \">\" to change page");
        }
        System.out.println("  \"s\" to search by Email/Name");
        System.out.println("  \"back\" to return to menu");
        System.out.println("  Enter 1-" + (endIdx - startIdx) + " to select user");

        String input = InputHandler.readLine("Input: ", true);
        if (input.equalsIgnoreCase("back")) {
          break;
        } else if (input.equalsIgnoreCase("s")) {
          inSearchMode = true;
        } else if (input.equals("<") && totalUsers > pageSize) {
          if (currentPage > 0) {
            currentPage--;
          } else {
            System.out.println("Already on the first page.");
          }
        } else if (input.equals(">") && totalUsers > pageSize) {
          if (currentPage < totalPages - 1) {
            currentPage++;
          } else {
            System.out.println("Already on the last page.");
          }
        } else {
          try {
            int pageChoice = Integer.parseInt(input);
            int selectedIndex = startIdx + (pageChoice - 1);
            if (selectedIndex >= startIdx && selectedIndex < endIdx) {
              editUserFields(client, users.get(selectedIndex), users);
            } else {
              System.out.println("Invalid index. Please try again.");
            }
          } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error calling editUser: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void displayProfile(Users user) {
    System.out.println("\n================ PROFILE DISPLAY ================");
    System.out.printf("User ID:      %d\n", user.getUserId());
    System.out.printf("Role:         %s\n", user.getUserRole());
    System.out.printf("First Name:   %s\n", user.getFirstName());
    System.out.printf("Last Name:    %s\n", user.getLastName());
    System.out.printf("IC/Passport:  %s\n", user.getIcPassportNo());
    System.out.printf("Email:        %s\n", user.getEmail());

    switch (user) {
      case Doctor doc -> {
        System.out.printf("Doctor ID:    %d\n", doc.getDoctorId());
        System.out.printf("Specialization: %s\n", doc.getSpecialization());
      }
      case Patient pat -> {
        System.out.printf("Patient ID:   %d\n", pat.getPatientId());
        System.out.printf("Medical Rec ID: %d\n", pat.getMedicalRecordId());
        System.out.printf("Contact No:   %s\n", pat.getContactNumber());
      }
      case ClinicAdministrator admin -> {
        System.out.printf("Admin ID:     %d\n", admin.getAdminId());
      }
      case Receptionist recep -> {
        System.out.printf("Receptionist ID: %d\n", recep.getReceptionistId());
      }
      default -> {}
    }
    System.out.println("=================================================");
  }

  private static void editUserFields(ClinicClient client, Users user, List<Users> allUsers) {
    while (true) {
      displayProfile(user);

      System.out.println("Select a field to edit:");
      switch (user) {
        case Doctor doc -> {
          System.out.printf("[1]. First Name: %s\n", doc.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", doc.getLastName());
          System.out.printf("[3]. Specialization: %s\n", doc.getSpecialization());
          System.out.println("[4]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 4) {
            return;
          } else if (choice == 1 || choice == 2) {
            user = handleNameEdit(client, allUsers, user, choice);
          } else if (choice == 3) {
            String val = InputHandler.readLine("Enter new Specialization: ", true);
            if (val.isEmpty()) {
              System.out.println("Specialization cannot be empty.");
            } else {
              Doctor updated =
                  new Doctor(
                      doc.getDoctorId(),
                      doc.getUserId(),
                      doc.getFirstName(),
                      doc.getLastName(),
                      doc.getUserRole(),
                      doc.getIcPassportNo(),
                      doc.getEmail(),
                      doc.getPasswordHash(),
                      val);
              user = executeEdit(client, updated, allUsers, user);
            }
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case Patient pat -> {
          System.out.printf("[1]. First Name: %s\n", pat.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", pat.getLastName());
          System.out.printf("[3]. Contact Number: %s\n", pat.getContactNumber());
          System.out.println("[4]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 4) {
            return;
          } else if (choice == 1 || choice == 2) {
            user = handleNameEdit(client, allUsers, user, choice);
          } else if (choice == 3) {
            String val = InputHandler.readLine("Enter new Contact Number: ", true);
            if (val.isEmpty()) {
              System.out.println("Contact Number cannot be empty.");
            } else {
              Patient updated =
                  new Patient(
                      pat.getPatientId(),
                      pat.getUserId(),
                      pat.getFirstName(),
                      pat.getLastName(),
                      pat.getUserRole(),
                      pat.getIcPassportNo(),
                      pat.getEmail(),
                      pat.getPasswordHash(),
                      pat.getMedicalRecordId(),
                      val);
              user = executeEdit(client, updated, allUsers, user);
            }
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case ClinicAdministrator admin -> {
          System.out.printf("[1]. First Name: %s\n", admin.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", admin.getLastName());
          System.out.println("[3]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 3) {
            return;
          } else if (choice == 1 || choice == 2) {
            user = handleNameEdit(client, allUsers, user, choice);
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case Receptionist recep -> {
          System.out.printf("[1]. First Name: %s\n", recep.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", recep.getLastName());
          System.out.println("[3]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 3) {
            return;
          } else if (choice == 1 || choice == 2) {
            user = handleNameEdit(client, allUsers, user, choice);
          } else {
            System.out.println("Invalid choice.");
          }
        }
        default -> {
          System.out.println("Unknown user role type.");
          return;
        }
      }
    }
  }

  private static Users handleNameEdit(
      ClinicClient client, List<Users> allUsers, Users currentUser, int choice) {
    String fieldName = choice == 1 ? "First Name" : "Last Name";
    String val = InputHandler.readLine("Enter new " + fieldName + ": ", true);
    if (val.isEmpty()) {
      System.out.println(fieldName + " cannot be empty.");
      return currentUser;
    }

    String newFirstName = choice == 1 ? val : currentUser.getFirstName();
    String newLastName = choice == 2 ? val : currentUser.getLastName();

    Users updated = null;
    switch (currentUser) {
      case Doctor doc ->
          updated =
              new Doctor(
                  doc.getDoctorId(),
                  doc.getUserId(),
                  newFirstName,
                  newLastName,
                  doc.getUserRole(),
                  doc.getIcPassportNo(),
                  doc.getEmail(),
                  doc.getPasswordHash(),
                  doc.getSpecialization());
      case Patient pat ->
          updated =
              new Patient(
                  pat.getPatientId(),
                  pat.getUserId(),
                  newFirstName,
                  newLastName,
                  pat.getUserRole(),
                  pat.getIcPassportNo(),
                  pat.getEmail(),
                  pat.getPasswordHash(),
                  pat.getMedicalRecordId(),
                  pat.getContactNumber());
      case ClinicAdministrator admin ->
          updated =
              new ClinicAdministrator(
                  admin.getAdminId(),
                  admin.getUserId(),
                  newFirstName,
                  newLastName,
                  admin.getUserRole(),
                  admin.getIcPassportNo(),
                  admin.getEmail(),
                  admin.getPasswordHash());
      case Receptionist recep ->
          updated =
              new Receptionist(
                  recep.getReceptionistId(),
                  recep.getUserId(),
                  newFirstName,
                  newLastName,
                  recep.getUserRole(),
                  recep.getIcPassportNo(),
                  recep.getEmail(),
                  recep.getPasswordHash());
      default -> {}
    }

    if (updated != null) {
      return executeEdit(client, updated, allUsers, currentUser);
    }
    return currentUser;
  }

  private static Users executeEdit(
      ClinicClient client, Users updatedUser, List<Users> allUsers, Users currentUser) {
    try {
      boolean success = client.editUser(updatedUser);
      if (success) {
        System.out.println("Edit successful!");
        updateLocalUser(allUsers, updatedUser);
        return updatedUser;
      } else {
        System.out.println("Edit failed on server.");
      }
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
    return currentUser;
  }

  private static void updateLocalUser(List<Users> allUsers, Users updatedUser) {
    for (int i = 0; i < allUsers.size(); i++) {
      if (allUsers.get(i).getUserId() == updatedUser.getUserId()) {
        allUsers.set(i, updatedUser);
        break;
      }
    }
  }
}
