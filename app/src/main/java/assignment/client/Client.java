package assignment.client;

import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import java.rmi.Naming;
import java.util.Scanner;

public class Client {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    try {
      RegisterUserService registerUser =
          (RegisterUserService) Naming.lookup("rmi://localhost:1099/registerUser");

      while (true) {
        System.out.println("\n1. Register Doctor");
        System.out.println("2. Register Patient");
        System.out.println("3. Register Clinic Administrator");
        System.out.println("4. Register Receptionist");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");

        String choice = sc.nextLine();
        if (choice.equals("5")) {
          System.out.println("Goodbye!");
          break;
        }

        if (choice.equals("1")) {
          System.out.print("Enter first name: ");
          String firstName = sc.nextLine();
          System.out.print("Enter last name: ");
          String lastName = sc.nextLine();
          System.out.print("Enter IC/Passport number: ");
          String icPassportNo = sc.nextLine();
          System.out.print("Enter email: ");
          String email = sc.nextLine();
          System.out.print("Enter password: ");
          String password = sc.nextLine();
          System.out.print("Enter specialization: ");
          String specialization = sc.nextLine();
          Doctor doctor =
              new Doctor(
                  firstName, lastName, "doctor", icPassportNo, email, password, specialization);
          if (registerUser.registerUser(doctor)) {
            System.out.println("Doctor registered successfully.");
          } else {
            System.out.println("Failed to register doctor.");
          }
        } else if (choice.equals("2")) {
          System.out.print("Enter first name: ");
          String firstName = sc.nextLine();
          System.out.print("Enter last name: ");
          String lastName = sc.nextLine();
          System.out.print("Enter IC/Passport number: ");
          String icPassportNo = sc.nextLine();
          System.out.print("Enter email: ");
          String email = sc.nextLine();
          System.out.print("Enter password: ");
          String password = sc.nextLine();
          System.out.print("Enter medical record ID: ");
          String medicalRecordId = sc.nextLine();
          System.out.print("Enter contact number: ");
          String contactNumber = sc.nextLine();
          Patient patient =
              new Patient(
                  firstName,
                  lastName,
                  "patient",
                  icPassportNo,
                  email,
                  password,
                  medicalRecordId,
                  contactNumber);
          if (registerUser.registerUser(patient)) {
            System.out.println("Patient registered successfully.");
          } else {
            System.out.println("Failed to register patient.");
          }
        } else if (choice.equals("3")) {
          System.out.print("Enter first name: ");
          String firstName = sc.nextLine();
          System.out.print("Enter last name: ");
          String lastName = sc.nextLine();
          System.out.print("Enter IC/Passport number: ");
          String icPassportNo = sc.nextLine();
          System.out.print("Enter email: ");
          String email = sc.nextLine();
          System.out.print("Enter password: ");
          String password = sc.nextLine();
          ClinicAdministrator admin =
              new ClinicAdministrator(firstName, lastName, "admin", icPassportNo, email, password);
          if (registerUser.registerUser(admin)) {
            System.out.println("Clinic Administrator registered successfully.");
          } else {
            System.out.println("Failed to register clinic administrator.");
          }
        } else if (choice.equals("4")) {
          System.out.print("Enter first name: ");
          String firstName = sc.nextLine();
          System.out.print("Enter last name: ");
          String lastName = sc.nextLine();
          System.out.print("Enter IC/Passport number: ");
          String icPassportNo = sc.nextLine();
          System.out.print("Enter email: ");
          String email = sc.nextLine();
          System.out.print("Enter password: ");
          String password = sc.nextLine();
          Receptionist receptionist =
              new Receptionist(firstName, lastName, "receptionist", icPassportNo, email, password);
          if (registerUser.registerUser(receptionist)) {
            System.out.println("Receptionist registered successfully.");
          } else {
            System.out.println("Failed to register receptionist.");
          }
        } else {
          System.out.println("Invalid choice. Please try again.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sc.close();
    }
  }
}
