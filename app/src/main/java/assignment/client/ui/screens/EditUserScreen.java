package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.client.ui.PrintHelper;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.User;
import java.util.ArrayList;
import java.util.List;

/** EditUserScreen handles the workflow for viewing, searching, and editing existing users. */
public class EditUserScreen {

  public static void display(ServiceManager client) {
    try {
      List<User> users = client.getAllUsers();
      if (users == null || users.isEmpty()) {
        System.out.println("No users found in the system.");
        return;
      }

      PrintHelper.Paginator<User> paginator = new PrintHelper.Paginator<>(users, 10);
      boolean inSearchMode = false;

      while (true) {
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
          List<User> matches = new ArrayList<>();
          for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(query) || u.getFullName().equalsIgnoreCase(query)) {
              matches.add(u);
            }
          }
          if (matches.isEmpty()) {
            for (User u : users) {
              if (u.getEmail().toLowerCase().contains(query.toLowerCase())
                  || u.getFullName().toLowerCase().contains(query.toLowerCase())) {
                matches.add(u);
              }
            }
          }

          if (matches.isEmpty()) {
            System.out.println("No matching user found.");
          } else if (matches.size() == 1) {
            editOrDeleteUser(client, matches.get(0), users);
            inSearchMode = false; // exit search mode after select
          } else {
            System.out.println("\nMultiple users found:");
            String input =
                PrintHelper.printUserList(matches, 0, "Select a user by index (or 'cancel'): ");
            if (!input.equalsIgnoreCase("cancel") && !input.isEmpty()) {
              try {
                int userId = Integer.parseInt(input);
                User selected = null;
                for (User u : users) {
                  if (u.getUserId() == userId) {
                    selected = u;
                    break;
                  }
                }
                if (selected != null) {
                  editOrDeleteUser(client, selected, users);
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
        if (paginator.getTotalPages() > 1) {
          paginator.printPageInfo();
        }

        System.out.println("\nInput settings:");
        if (paginator.getTotalPages() > 1) {
          System.out.println("  \"<\" or \">\" to change page");
        }
        System.out.println("  \"s\" to search by Email/Name");
        System.out.println("  \"back\" to return to menu");

        String input =
            PrintHelper.printUserList(
                paginator.getCurrentPageItems(), paginator.getStartIndex(), "Input: ");

        if (input.equalsIgnoreCase("back")) {
          break;
        } else if (input.equalsIgnoreCase("s")) {
          inSearchMode = true;
        } else if (input.equals("<") && paginator.getTotalPages() > 1) {
          paginator.prevPage();
        } else if (input.equals(">") && paginator.getTotalPages() > 1) {
          paginator.nextPage();
        } else {
          try {
            int userId = Integer.parseInt(input);
            User selectedUser = null;
            for (User u : users) {
              if (u.getUserId() == userId) {
                selectedUser = u;
                break;
              }
            }
            if (selectedUser != null) {
              editOrDeleteUser(client, selectedUser, users);
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

  private static void displayProfile(User user) {
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

  private static void editOrDeleteUser(ServiceManager client, User user, List<User> allUsers) {
    while (true) {
      displayProfile(user);

      System.out.println("Select a field to edit:");
      switch (user) {
        case Doctor doc -> {
          System.out.printf("[1]. First Name: %s\n", doc.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", doc.getLastName());
          System.out.printf("[3]. Specialization: %s\n", doc.getSpecialization());
          System.out.println("[4]. Delete User");
          System.out.println("[5]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 5) {
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
                      val,
                      doc.getCreatedAt(),
                      doc.isDeleted());
              user = executeEdit(client, updated, allUsers, user);
            }
          } else if (choice == 4) {
            if (executeDelete(client, allUsers, user)) {
              return;
            }
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case Patient pat -> {
          System.out.printf("[1]. First Name: %s\n", pat.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", pat.getLastName());
          System.out.printf("[3]. Contact Number: %s\n", pat.getContactNumber());
          System.out.println("[4]. Delete User");
          System.out.println("[5]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 5) {
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
                      val,
                      pat.getCreatedAt(),
                      pat.isDeleted());
              user = executeEdit(client, updated, allUsers, user);
            }
          } else if (choice == 4) {
            if (executeDelete(client, allUsers, user)) {
              return;
            }
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case ClinicAdministrator admin -> {
          System.out.printf("[1]. First Name: %s\n", admin.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", admin.getLastName());
          System.out.println("[3]. Delete User");
          System.out.println("[4]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 4) {
            return;
          } else if (choice == 1 || choice == 2) {
            user = handleNameEdit(client, allUsers, user, choice);
          } else if (choice == 3) {
            if (executeDelete(client, allUsers, user)) {
              return;
            }
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case Receptionist recep -> {
          System.out.printf("[1]. First Name: %s\n", recep.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", recep.getLastName());
          System.out.println("[3]. Delete User");
          System.out.println("[4]. Finish editing");
          int choice = InputHandler.readInt("Select choice: ");

          if (choice == 4) {
            return;
          } else if (choice == 1 || choice == 2) {
            user = handleNameEdit(client, allUsers, user, choice);
          } else if (choice == 3) {
            if (executeDelete(client, allUsers, user)) {
              return;
            }
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

  private static User handleNameEdit(
      ServiceManager client, List<User> allUsers, User currentUser, int choice) {
    String fieldName = choice == 1 ? "First Name" : "Last Name";
    String val = InputHandler.readLine("Enter new " + fieldName + ": ", true);
    if (val.isEmpty()) {
      System.out.println(fieldName + " cannot be empty.");
      return currentUser;
    }

    String newFirstName = choice == 1 ? val : currentUser.getFirstName();
    String newLastName = choice == 2 ? val : currentUser.getLastName();

    User updated = null;
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
                  doc.getSpecialization(),
                  doc.getCreatedAt(),
                  doc.isDeleted());
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
                  pat.getContactNumber(),
                  pat.getCreatedAt(),
                  pat.isDeleted());
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
                  admin.getPasswordHash(),
                  admin.getCreatedAt(),
                  admin.isDeleted());
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
                  recep.getPasswordHash(),
                  recep.getCreatedAt(),
                  recep.isDeleted());
      default -> {}
    }

    if (updated != null) {
      return executeEdit(client, updated, allUsers, currentUser);
    }
    return currentUser;
  }

  private static User executeEdit(
      ServiceManager client, User updatedUser, List<User> allUsers, User currentUser) {
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

  private static void updateLocalUser(List<User> allUsers, User updatedUser) {
    for (int i = 0; i < allUsers.size(); i++) {
      if (allUsers.get(i).getUserId() == updatedUser.getUserId()) {
        allUsers.set(i, updatedUser);
        break;
      }
    }
  }

  private static boolean executeDelete(ServiceManager client, List<User> allUsers, User user) {
    boolean confirm = InputHandler.readYesNo("Are you sure you want to delete this user?");
    if (!confirm) {
      System.out.println("Deletion cancelled.");
      return false;
    }
    try {
      boolean success = client.deleteUser(user);
      if (success) {
        System.out.println("User deleted successfully!");
        deleteLocalUser(allUsers, user);
        return true;
      } else {
        System.out.println("Delete failed on server.");
      }
    } catch (Exception e) {
      System.err.println("Error deleting user: " + e.getMessage());
    }
    return false;
  }

  private static void deleteLocalUser(List<User> allUsers, User deletedUser) {
    allUsers.removeIf(u -> u.getUserId() == deletedUser.getUserId());
  }
}
