package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import org.mindrot.jbcrypt.BCrypt;

/** RegisterUserScreen displays and handles the workflow for registering new users. */
public class RegisterUserScreen {

  public static void display(ServiceManager client) {
    System.out.println("\nSelect user role to register:");
    System.out.println("1. Doctor");
    System.out.println("2. Patient");
    System.out.println("3. Clinic Administrator");
    System.out.println("4. Receptionist");

    int roleChoice = InputHandler.readInt("Select an option: ");
    String role = "";
    switch (roleChoice) {
      case 1:
        role = "doctor";
        break;
      case 2:
        role = "patient";
        break;
      case 3:
        role = "admin";
        break;
      case 4:
        role = "receptionist";
        break;
      default:
        System.out.println("Invalid choice. Returning to main menu.");
        return;
    }

    System.out.println("\n--- Enter User Info ---");
    String firstName = InputHandler.readLine("First Name: ");
    String lastName = InputHandler.readLine("Last Name: ");
    String icPassportNo = InputHandler.readLine("IC/Passport Number: ");
    String email = InputHandler.readLine("Email: ");
    String password = InputHandler.readLine("Password: ");

    // Hash the password with BCrypt
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

    try {
      boolean success = false;
      switch (role) {
        case "doctor":
          String specialization = InputHandler.readLine("Specialization: ");
          Doctor doctor =
              new Doctor(
                  firstName, lastName, role, icPassportNo, email, hashedPassword, specialization);
          success = client.registerUser(doctor);
          break;
        case "patient":
          String contactNumber = InputHandler.readLine("Contact Number: ");
          Patient patient =
              new Patient(
                  firstName, lastName, role, icPassportNo, email, hashedPassword, 0, contactNumber);
          success = client.registerUser(patient);
          break;
        case "admin":
          ClinicAdministrator admin =
              new ClinicAdministrator(
                  firstName, lastName, role, icPassportNo, email, hashedPassword);
          success = client.registerUser(admin);
          break;
        case "receptionist":
          Receptionist receptionist =
              new Receptionist(firstName, lastName, role, icPassportNo, email, hashedPassword);
          success = client.registerUser(receptionist);
          break;
      }

      if (success) {
        System.out.println("User successfully registered!");
      } else {
        System.out.println("Failed to register user. Please check server logs.");
      }

    } catch (Exception e) {
      System.err.println("Error calling registerUser: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
