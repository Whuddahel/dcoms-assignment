package assignment.client.ui;

import assignment.shared.interfaces.EditUserService;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.Users;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class ClinicAdministratorMenu {

  public static void displayMenu() {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.println("\n=== Clinic Administrator Menu ===");
      System.out.println("[1]. Register User");
      System.out.println("[2]. Edit User");
      System.out.println("[3]. Exit");
      System.out.print("Select an option: ");

      String choice = scanner.nextLine().trim();
      if (choice.equals("1")) {
        registerUserFlow(scanner);
      } else if (choice.equals("2")) {
        editUserFlow(scanner);
      } else if (choice.equals("3")) {
        System.out.println("Exiting Clinic Administrator Menu...");
        break;
      } else {
        System.out.println("Invalid option. Please try again.");
      }
    }
  }

  private static void editUserFlow(Scanner scanner) {
    try {
      Registry registry = LocateRegistry.getRegistry("localhost", 1099);
      EditUserService editService = (EditUserService) registry.lookup("EditUser");

      List<Users> users = editService.getAllUsers();
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
          System.out.print("Enter user Email or Full Name (or 'cancel' to return): ");
          String query = scanner.nextLine().trim();
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
            editUserFields(scanner, editService, matches.get(0), users);
            inSearchMode = false; // exit search mode after select
          } else {
            System.out.println("\nMultiple users found:");
            for (int i = 0; i < matches.size(); i++) {
              Users m = matches.get(i);
              System.out.printf(
                  "[%d]. %s - %s (%s)\n", i + 1, m.getUserRole(), m.getFullName(), m.getEmail());
            }
            System.out.print("Select a user by index (or 'cancel'): ");
            String matchChoiceStr = scanner.nextLine().trim();
            if (!matchChoiceStr.equalsIgnoreCase("cancel") && !matchChoiceStr.isEmpty()) {
              try {
                int matchChoice = Integer.parseInt(matchChoiceStr);
                if (matchChoice >= 1 && matchChoice <= matches.size()) {
                  editUserFields(scanner, editService, matches.get(matchChoice - 1), users);
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
        System.out.print("Input: ");

        String input = scanner.nextLine().trim();
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
              editUserFields(scanner, editService, users.get(selectedIndex), users);
            } else {
              System.out.println("Invalid index. Please try again.");
            }
          } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error calling editUser RPC: " + e.getMessage());
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

  private static void editUserFields(
      Scanner scanner, EditUserService editService, Users user, List<Users> allUsers) {
    while (true) {
      displayProfile(user);

      System.out.println("Select a field to edit:");
      switch (user) {
        case Doctor doc -> {
          System.out.printf("[1]. First Name: %s\n", doc.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", doc.getLastName());
          System.out.printf("[3]. Specialization: %s\n", doc.getSpecialization());
          System.out.println("[4]. Finish editing");
          System.out.print("Select choice: ");
          String choice = scanner.nextLine().trim();

          if (choice.equals("4")) {
            return;
          } else if (choice.equals("1") || choice.equals("2")) {
            user = handleNameEdit(scanner, editService, allUsers, user, choice);
          } else if (choice.equals("3")) {
            System.out.print("Enter new Specialization: ");
            String val = scanner.nextLine().trim();
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
              user = executeEdit(editService, updated, allUsers, user);
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
          System.out.print("Select choice: ");
          String choice = scanner.nextLine().trim();

          if (choice.equals("4")) {
            return;
          } else if (choice.equals("1") || choice.equals("2")) {
            user = handleNameEdit(scanner, editService, allUsers, user, choice);
          } else if (choice.equals("3")) {
            System.out.print("Enter new Contact Number: ");
            String val = scanner.nextLine().trim();
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
              user = executeEdit(editService, updated, allUsers, user);
            }
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case ClinicAdministrator admin -> {
          System.out.printf("[1]. First Name: %s\n", admin.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", admin.getLastName());
          System.out.println("[3]. Finish editing");
          System.out.print("Select choice: ");
          String choice = scanner.nextLine().trim();

          if (choice.equals("3")) {
            return;
          } else if (choice.equals("1") || choice.equals("2")) {
            user = handleNameEdit(scanner, editService, allUsers, user, choice);
          } else {
            System.out.println("Invalid choice.");
          }
        }
        case Receptionist recep -> {
          System.out.printf("[1]. First Name: %s\n", recep.getFirstName());
          System.out.printf("[2]. Last Name: %s\n", recep.getLastName());
          System.out.println("[3]. Finish editing");
          System.out.print("Select choice: ");
          String choice = scanner.nextLine().trim();

          if (choice.equals("3")) {
            return;
          } else if (choice.equals("1") || choice.equals("2")) {
            user = handleNameEdit(scanner, editService, allUsers, user, choice);
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
      Scanner scanner,
      EditUserService editService,
      List<Users> allUsers,
      Users currentUser,
      String choice) {
    String fieldName = choice.equals("1") ? "First Name" : "Last Name";
    System.out.print("Enter new " + fieldName + ": ");
    String val = scanner.nextLine().trim();
    if (val.isEmpty()) {
      System.out.println(fieldName + " cannot be empty.");
      return currentUser;
    }

    String newFirstName = choice.equals("1") ? val : currentUser.getFirstName();
    String newLastName = choice.equals("2") ? val : currentUser.getLastName();

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
      return executeEdit(editService, updated, allUsers, currentUser);
    }
    return currentUser;
  }

  private static Users executeEdit(
      EditUserService editService, Users updatedUser, List<Users> allUsers, Users currentUser) {
    try {
      boolean success = false;
      switch (updatedUser) {
        case Doctor doc -> success = editService.editUser(doc);
        case Patient pat -> success = editService.editUser(pat);
        case ClinicAdministrator admin -> success = editService.editUser(admin);
        case Receptionist recep -> success = editService.editUser(recep);
        default -> {}
      }
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

  private static void registerUserFlow(Scanner scanner) {
    System.out.println("\nSelect user role to register:");
    System.out.println("1. Doctor");
    System.out.println("2. Patient");
    System.out.println("3. Clinic Administrator");
    System.out.println("4. Receptionist");
    System.out.print("Select an option: ");

    String roleChoice = scanner.nextLine().trim();
    String role = "";
    switch (roleChoice) {
      case "1":
        role = "doctor";
        break;
      case "2":
        role = "patient";
        break;
      case "3":
        role = "admin";
        break;
      case "4":
        role = "receptionist";
        break;
      default:
        System.out.println("Invalid choice. Returning to main menu.");
        return;
    }

    System.out.println("\n--- Enter User Info ---");
    System.out.print("First Name: ");
    String firstName = scanner.nextLine().trim();

    System.out.print("Last Name: ");
    String lastName = scanner.nextLine().trim();

    System.out.print("IC/Passport Number: ");
    String icPassportNo = scanner.nextLine().trim();

    System.out.print("Email: ");
    String email = scanner.nextLine().trim();

    System.out.print("Password: ");
    String password = scanner.nextLine().trim();

    // Hash the password with BCrypt
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

    try {
      // Connect to the server RMI registry at port 1099
      Registry registry = LocateRegistry.getRegistry("localhost", 1099);
      RegisterUserService registerService = (RegisterUserService) registry.lookup("RegisterUser");

      boolean success = false;
      switch (role) {
        case "doctor":
          System.out.print("Specialization: ");
          String specialization = scanner.nextLine().trim();
          Doctor doctor =
              new Doctor(
                  firstName, lastName, role, icPassportNo, email, hashedPassword, specialization);
          success = registerService.registerUser(doctor);
          break;
        case "patient":
          System.out.print("Contact Number: ");
          String contactNumber = scanner.nextLine().trim();
          Patient patient =
              new Patient(
                  firstName, lastName, role, icPassportNo, email, hashedPassword, 0, contactNumber);
          success = registerService.registerUser(patient);
          break;
        case "admin":
          ClinicAdministrator admin =
              new ClinicAdministrator(
                  firstName, lastName, role, icPassportNo, email, hashedPassword);
          success = registerService.registerUser(admin);
          break;
        case "receptionist":
          Receptionist receptionist =
              new Receptionist(firstName, lastName, role, icPassportNo, email, hashedPassword);
          success = registerService.registerUser(receptionist);
          break;
      }

      if (success) {
        System.out.println("User successfully registered!");
      } else {
        System.out.println("Failed to register user. Please check server logs.");
      }

    } catch (Exception e) {
      System.err.println("Error calling registerUser RPC: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
