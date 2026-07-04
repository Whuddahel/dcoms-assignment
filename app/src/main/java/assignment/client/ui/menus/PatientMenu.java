package assignment.client.ui.menus;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.shared.dto.LoginResponse;

public class PatientMenu
{
    public static void displayMenu(ServiceManager client, LoginResponse session)
    {
        while (true)
        {
            System.out.println("Hello " + session.getFirstName() + " " + session.getLastName());
            System.out.println("\n=== Patient Menu ===");
            System.out.println("[1]. Update Personal Information");
            System.out.println("[2]. View Upcoming Appointments/Cancel");
            System.out.println("[3]. Book Appointment");
            System.out.println("[4]. View Past Appointments");
            System.out.println("[5]. View Consultation Notes");
            System.out.println("[6]. Exit");

            int choice = InputHandler.readInt("Select an option: ");
            if (choice == 1)
            {
                System.out.println("i love marvel rivals");
            }
            else if (choice == 2)
            {
                System.out.println("i love marvel rivals");
            }
            else if (choice == 3)
            {
                System.out.println("i love marvel rivals");
            }
            else if (choice == 4)
            {
                System.out.println("i love marvel rivals");
            }
            else if (choice == 5)
            {
                System.out.println("i love marvel rivals");
            }
            else if (choice == 6)
            {
                System.out.println("Exiting Patient Menu...");
                break;
            }
            else
            {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
