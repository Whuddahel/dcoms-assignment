/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.shared.model.Patient;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterPatientScreen {

  public static void display(ServiceManager client) {
    System.out.println("PATIENT REGISTRATION");
    System.out.println("Enter patient details:");

    String firstName = InputHandler.readLine("First Name: ");
    String lastName = InputHandler.readLine("Last Name: ");
    String icPassportNo = InputHandler.readLine("IC/Passport Number: ");
    String email = InputHandler.readLine("Email: ");
    String contactNumber = InputHandler.readLine("Contact Number: ");
    String password = InputHandler.readLine("Password: ");

    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

    try {
      Patient patient =
          new Patient(
              firstName,
              lastName,
              "patient",
              icPassportNo,
              email,
              hashedPassword,
              0,
              contactNumber);

      boolean success = client.registerUser(patient);

      if (success) {
        System.out.println("\nPatient registered successfully!");
        System.out.println("Medical Record ID will be assigned by the system.");
      } else {
        System.out.println("\nFailed to register patient. Please check server logs.");
      }

    } catch (Exception e) {
      if (e.getMessage() != null && e.getMessage().contains("EMAIL_ALREADY_EXISTS")) {
        System.out.println("\nRegistration failed: An account with this email already exists.");
      } else {
        Helper.printError("Error registering patient", e);
      }
    }
  }
}
