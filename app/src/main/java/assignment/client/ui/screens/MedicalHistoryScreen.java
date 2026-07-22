package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.PrintHelper;
import assignment.shared.dto.AppointmentView;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import java.util.ArrayList;
import java.util.List;

public class MedicalHistoryScreen {

  public static void viewMedicalHistoryOfPatient(ServiceManager client, LoginResponse session) {
    try {
      while (true) {
        List<Patient> patients = client.getPatientsWithConsultations(session.getUserId());
        if (patients == null || patients.isEmpty()) {
          Helper.printBanner("Medical History", Helper.Theme.CYAN);
          System.out.println("No patients with completed consultations found.");
          return;
        }

        Helper.printBanner("Patients with Consultations", Helper.Theme.CYAN);
        String input =
            PrintHelper.printPatientList(
                patients, 0, "Select a patient to view history (or 'back' to return): ");
        if (input.equalsIgnoreCase("back") || input.isEmpty()) {
          return;
        }

        Patient selectedPatient = null;
        try {
          int patientId = Integer.parseInt(input);
          for (Patient p : patients) {
            if (p.getPatientId() == patientId) {
              selectedPatient = p;
              break;
            }
          }
        } catch (NumberFormatException e) {
          // ignore
        }

        if (selectedPatient == null) {
          System.out.println("Invalid selection.");
          continue;
        }

        viewPatientHistoryDetails(client, session, selectedPatient);
      }
    } catch (Exception e) {
      System.err.println("Error viewing medical history: " + e.getMessage());
    }
  }

  private static void viewPatientHistoryDetails(
      ServiceManager client, LoginResponse session, Patient patient) {
    try {
      while (true) {
        System.out.println("\n--- Patient Details ---");
        System.out.println(
            "Name:          " + patient.getFirstName() + " " + patient.getLastName());
        System.out.println("Contact:       " + patient.getContactNumber());
        System.out.println("Email:         " + patient.getEmail());
        System.out.println("IC/Passport:   " + patient.getIcPassportNo());
        System.out.println("-----------------------");

        List<Appointment> appointments =
            client.getAppointmentsWithConsultationByDoctorAndPatient(
                session.getUserId(), patient.getPatientId());
        if (appointments == null || appointments.isEmpty()) {
          System.out.println("No past appointments with consultations found for this patient.");
          return;
        }

        System.out.println("\nPast Appointments with Consultations:");
        List<AppointmentView> views = new ArrayList<>();
        for (Appointment a : appointments) {
          Schedule s = client.getScheduleByIdForDoctor(a.getScheduleId());
          String timeSlot = s != null ? s.getStartTime() + " - " + s.getEndTime() : "Unknown";
          views.add(
              new AppointmentView(
                  a.getAppointmentId(),
                  a.getAppointmentDate().toString(),
                  "",
                  "", // Patient name omitted in list to save space
                  timeSlot,
                  "Completed"));
        }

        String input =
            PrintHelper.printAppointmentList(
                views, 0, "Select an appointment for details (or 'back' to return): ");
        if (input.equalsIgnoreCase("back") || input.isEmpty()) {
          return;
        }

        Appointment selected = null;
        try {
          int appointmentId = Integer.parseInt(input);
          for (Appointment a : appointments) {
            if (a.getAppointmentId() == appointmentId) {
              selected = a;
              break;
            }
          }
        } catch (NumberFormatException e) {
          // ignore
        }

        if (selected == null) {
          System.out.println("Invalid selection.");
          continue;
        }

        AppointmentScreen.displayAppointmentDetail(client, selected);

        Consultation consultation =
            client.getConsultationByAppointmentId(selected.getAppointmentId());
        if (consultation != null) {
          System.out.println("--- Consultation Details ---");
          System.out.printf("Fee:             $%.2f\n", consultation.getFee());
          System.out.println("Content:         " + consultation.getContent());
          System.out.println("Date Recorded:   " + consultation.getCreatedAt());
          System.out.println("----------------------------");

          System.out.println("\nOptions:");
          Helper.printOption(1, "Update Consultation Detail", Helper.Theme.BLUE);
          Helper.printOption(2, "Back", Helper.Theme.GREEN);

          int choice = InputHandler.readInt("Select an option: ");
          if (choice == 1) {
            ConsultationScreen.updateConsultationDetail(client, consultation);
          }
        } else {
          System.out.println("Consultation details not found.");
        }
      }
    } catch (Exception e) {
      System.err.println("Error viewing patient history details: " + e.getMessage());
    }
  }
}
