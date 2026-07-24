package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.PrintHelper;
import assignment.shared.dto.AppointmentView;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Appointment;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppointmentScreen {

  public static void viewUpcomingAppointments(ServiceManager client, LoginResponse session) {
    try {
      List<Appointment> appointments = client.getUpcomingAppointments(session.getUserId());
      if (appointments == null || appointments.isEmpty()) {
        Helper.printBanner("Upcoming Appointments", Helper.Theme.CYAN);
        System.out.println("You have no upcoming appointments.");
        return;
      }

      while (true) {
        // Re-fetch each loop iteration to reflect cancellations
        appointments = client.getUpcomingAppointments(session.getUserId());
        if (appointments == null || appointments.isEmpty()) {
          System.out.println("You have no more upcoming appointments.");
          return;
        }

        Helper.printBanner("Upcoming Appointments", Helper.Theme.CYAN);
        List<AppointmentView> views = new ArrayList<>();
        for (Appointment a : appointments) {
          String doctorName = resolveDoctorName(client, a.getDoctorId());
          String timeSlot = resolveScheduleTime(client, a.getScheduleId());
          views.add(
              new AppointmentView(
                  a.getAppointmentId(),
                  a.getAppointmentDate().toString(),
                  doctorName,
                  "",
                  timeSlot,
                  a.getcancelledByUserId() != null ? "Cancelled" : "Active"));
        }

        String input =
            PrintHelper.printAppointmentList(
                views, 0, "Select an appointment to view/cancel (or 'cancel' to return): ");
        if (input.equalsIgnoreCase("cancel") || input.isEmpty()) {
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

        displayAppointmentDetail(client, selected);

        boolean wantCancel = InputHandler.readYesNo("Would you like to cancel this appointment?");
        if (wantCancel) {
          boolean confirmCancel =
              InputHandler.readYesNo("Are you sure? This action cannot be undone.");
          if (confirmCancel) {
            boolean success =
                client.cancelAppointment(selected.getAppointmentId(), session.getUserId());
            if (success) {
              Helper.printLine("Appointment cancelled successfully.", Helper.Theme.GREEN);
            } else {
              Helper.printLine("Failed to cancel appointment.", Helper.Theme.RED);
            }
          } else {
            System.out.println("Cancellation aborted.");
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error loading appointments: " + e.getMessage());
    }
  }

  public static void bookAppointment(ServiceManager client, LoginResponse session) {
    try {
      // Step 1: Get patient info
      Patient patient = client.getPatientByUserId(session.getUserId());
      if (patient == null) {
        Helper.printLine("Could not load your patient record.", Helper.Theme.RED);
        return;
      }

      // Step 2: List doctors (with optional specialization filter)
      Doctor selectedDoctor = selectDoctor(client);
      if (selectedDoctor == null) {
        return; // user cancelled
      }

      // Step 3: Book with the selected doctor
      bookWithDoctor(client, patient, selectedDoctor);

    } catch (Exception e) {
      System.err.println("Error during booking: " + e.getMessage());
    }
  }

  public static void viewAppointmentHistory(ServiceManager client, LoginResponse session) {
    try {
      List<Appointment> appointments = client.getPastAppointments(session.getUserId());
      if (appointments == null || appointments.isEmpty()) {
        Helper.printBanner("Appointment History", Helper.Theme.CYAN);
        System.out.println("You have no past appointments.");
        return;
      }

      Helper.printBanner("Appointment History", Helper.Theme.CYAN);
      List<AppointmentView> views = new ArrayList<>();
      for (Appointment a : appointments) {
        String doctorName = resolveDoctorName(client, a.getDoctorId());
        String status = a.getcancelledByUserId() != null ? "Cancelled" : "Completed";
        String timeSlot = resolveScheduleTime(client, a.getScheduleId());
        views.add(
            new AppointmentView(
                a.getAppointmentId(),
                a.getAppointmentDate().toString(),
                doctorName,
                "",
                timeSlot,
                status));
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
        return;
      }
      displayAppointmentDetail(client, selected);

      // Show doctor's current availability
      Doctor doctor = client.getDoctorByIdForPatient(selected.getDoctorId());
      if (doctor != null) {
        System.out.println("\n--- Dr. " + doctor.getFullName() + "'s Current Availability ---");
        List<Schedule> docSchedules = client.getSchedulesByDoctorIdForPatient(doctor.getDoctorId());
        if (docSchedules == null || docSchedules.isEmpty()) {
          System.out.println("(No availability slots currently set)");
        } else {
          java.util.Collections.sort(docSchedules);
          for (Schedule s : docSchedules) {
            System.out.println(
                "  " + s.getDay() + " | " + s.getStartTime() + " - " + s.getEndTime());
          }
        }

        // Offer rebook
        boolean wantRebook =
            InputHandler.readYesNo("Would you like to book a new appointment with this doctor?");
        if (wantRebook) {
          Patient patient = client.getPatientByUserId(session.getUserId());
          if (patient != null) {
            bookWithDoctor(client, patient, doctor);
          }
        }
      }

    } catch (Exception e) {
      System.err.println("Error loading appointment history: " + e.getMessage());
    }
  }

  public static void viewPatientAppointments(ServiceManager client, LoginResponse session) {
    try {
      while (true) {
        List<Appointment> appointments =
            client.getUpcomingAppointmentsByDoctorUserId(session.getUserId());
        if (appointments == null || appointments.isEmpty()) {
          Helper.printBanner("Patient Appointments", Helper.Theme.CYAN);
          System.out.println("You have no active appointments.");
          return;
        }

        List<Appointment> notEnded = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Appointment a : appointments) {
          Schedule s = client.getScheduleByIdForDoctor(a.getScheduleId());
          if (s != null) {
            LocalDateTime endDateTime =
                a.getAppointmentDate().toLocalDate().atTime(s.getEndTime().toLocalTime());
            if (endDateTime.isAfter(now)) {
              notEnded.add(a);
            }
          }
        }

        if (notEnded.isEmpty()) {
          Helper.printBanner("Patient Appointments", Helper.Theme.CYAN);
          System.out.println("You have no active appointments that haven't ended yet.");
          return;
        }

        Helper.printBanner("Patient Appointments", Helper.Theme.CYAN);
        List<AppointmentView> views = new ArrayList<>();
        for (Appointment a : notEnded) {
          Patient p = client.getPatientByIdForDoctor(a.getPatientId());
          Schedule s = client.getScheduleByIdForDoctor(a.getScheduleId());
          String patientName = p != null ? p.getFirstName() + " " + p.getLastName() : "Unknown";
          String timeSlot = s != null ? s.getStartTime() + " - " + s.getEndTime() : "Unknown";
          views.add(
              new AppointmentView(
                  a.getAppointmentId(),
                  a.getAppointmentDate().toString(),
                  "",
                  patientName,
                  timeSlot,
                  "Active"));
        }

        String input =
            PrintHelper.printAppointmentList(
                views, 0, "Select an appointment (or 'back' to return): ");
        if (input.equalsIgnoreCase("back") || input.isEmpty()) {
          return;
        }

        Appointment selected = null;
        try {
          int appointmentId = Integer.parseInt(input);
          for (Appointment a : notEnded) {
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

        displayAppointmentDetail(client, selected);

        System.out.println("\nOptions:");
        Helper.printOption(1, "Initiate Consultation", Helper.Theme.BLUE);
        Schedule s = client.getScheduleByIdForDoctor(selected.getScheduleId());
        boolean canCancel = false;
        if (s != null) {
          LocalDateTime startDateTime =
              selected.getAppointmentDate().toLocalDate().atTime(s.getStartTime().toLocalTime());
          if (now.isBefore(startDateTime)) {
            canCancel = true;
          }
        }

        if (canCancel) {
          Helper.printOption(2, "Cancel Appointment", Helper.Theme.RED);
        }
        Helper.printOption(3, "Back", Helper.Theme.GREEN);

        int choice = InputHandler.readInt("Select an option: ");
        if (choice == 1) {
          ConsultationScreen.initiateConsultation(client, selected.getAppointmentId());
        } else if (choice == 2 && canCancel) {
          boolean confirm =
              InputHandler.readYesNo("Are you sure you want to cancel this appointment?");
          if (confirm) {
            boolean success =
                client.cancelAppointmentForDoctor(selected.getAppointmentId(), session.getUserId());
            if (success) {
              Helper.printLine("Appointment cancelled successfully.", Helper.Theme.GREEN);
            } else {
              Helper.printLine("Failed to cancel appointment.", Helper.Theme.RED);
            }
          }
        } else if (choice == 3) {
          // just go back
        } else {
          System.out.println("Invalid option.");
        }
      }
    } catch (Exception e) {
      System.err.println("Error viewing appointments: " + e.getMessage());
    }
  }

  private static Doctor selectDoctor(ServiceManager client) throws Exception {
    List<Doctor> allDoctors = client.getAllDoctorsForPatient();
    if (allDoctors == null || allDoctors.isEmpty()) {
      System.out.println("No doctors are currently available in the system.");
      return null;
    }

    Set<String> specs = new HashSet<>();
    for (Doctor d : allDoctors) {
      if (d.getSpecialization() != null && !d.getSpecialization().trim().isEmpty()) {
        specs.add(d.getSpecialization().trim());
      }
    }
    List<String> specList = new ArrayList<>(specs);
    specList.sort(String::compareToIgnoreCase);

    System.out.println("\n--- Available Specializations ---");
    System.out.println("[0]. Show All Doctors");
    for (int i = 0; i < specList.size(); i++) {
      System.out.println("[" + (i + 1) + "]. " + specList.get(i));
    }

    String specInput = InputHandler.readLine("Select a specialization (or 'cancel'): ", true);
    if (specInput.equalsIgnoreCase("cancel")) {
      return null;
    }

    List<Doctor> filtered = new ArrayList<>();
    if (specInput.isEmpty() || specInput.equals("0")) {
      filtered = allDoctors;
    } else {
      try {
        int choice = Integer.parseInt(specInput);
        if (choice > 0 && choice <= specList.size()) {
          String chosenSpec = specList.get(choice - 1);
          for (Doctor d : allDoctors) {
            if (d.getSpecialization() != null
                && d.getSpecialization().trim().equalsIgnoreCase(chosenSpec)) {
              filtered.add(d);
            }
          }
        } else {
          System.out.println("Invalid choice. Showing all doctors.");
          filtered = allDoctors;
        }
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Showing all doctors.");
        filtered = allDoctors;
      }
    }

    if (filtered.isEmpty()) {
      System.out.println("No doctors found for this specialization.");
      return null;
    }

    Helper.printBanner("Available Doctors", Helper.Theme.CYAN);
    String input = PrintHelper.printDoctorList(filtered, 0, "Select a doctor (or 'cancel'): ");
    if (input.equalsIgnoreCase("cancel") || input.isEmpty()) {
      return null;
    }

    try {
      int doctorId = Integer.parseInt(input);
      for (Doctor d : filtered) {
        if (d.getDoctorId() == doctorId) {
          return d;
        }
      }
    } catch (NumberFormatException e) {
      System.out.println("Invalid selection.");
    }
    return null;
  }

  private static void bookWithDoctor(ServiceManager client, Patient patient, Doctor doctor)
      throws Exception {
    // Fetch the doctor's schedule
    List<Schedule> schedules = client.getSchedulesByDoctorIdForPatient(doctor.getDoctorId());
    if (schedules == null || schedules.isEmpty()) {
      System.out.println(
          "Dr. " + doctor.getFullName() + " does not have any available time slots.");
      return;
    }
    java.util.Collections.sort(schedules);

    // Show doctor's weekly schedule for reference
    System.out.println("\nDr. " + doctor.getFullName() + "'s weekly schedule:");
    Set<String> availableDays = new HashSet<>();
    for (Schedule s : schedules) {
      availableDays.add(s.getDay().toUpperCase());
      System.out.println("  " + s.getDay() + " | " + s.getStartTime() + " - " + s.getEndTime());
    }

    // Step 3a: Enter date
    LocalDate selectedDate = null;
    while (selectedDate == null) {
      String dateStr = InputHandler.readLine("Enter appointment date (YYYY-MM-DD) or 'cancel': ");
      if (dateStr.equalsIgnoreCase("cancel")) {
        return;
      }
      try {
        selectedDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
      } catch (DateTimeParseException e) {
        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        continue;
      }
      if (selectedDate.isBefore(LocalDate.now())) {
        System.out.println("Cannot book an appointment in the past.");
        selectedDate = null;
        continue;
      }

      // Check if the selected date's day-of-week matches the doctor's schedule
      DayOfWeek dow = selectedDate.getDayOfWeek();
      String dayName = dow.name(); // e.g. "MONDAY"
      if (!availableDays.contains(dayName)) {
        System.out.println("Dr. " + doctor.getFullName() + " is not available on " + dayName + ".");
        System.out.println("Available days: " + availableDays);
        selectedDate = null;
      }
    }

    // Step 3b: Find available slots for that date
    DayOfWeek dow = selectedDate.getDayOfWeek();
    String dayName = dow.name();

    // Get schedule slots that match the day
    List<Schedule> daySlots = new ArrayList<>();
    for (Schedule s : schedules) {
      if (s.getDay().equalsIgnoreCase(dayName)) {
        daySlots.add(s);
      }
    }

    // Get existing appointments for that doctor on that date
    Date sqlDate = Date.valueOf(selectedDate);
    List<Appointment> existingAppointments =
        client.getAppointmentsByDoctorAndDate(doctor.getDoctorId(), sqlDate);

    // Determine which schedule slots are already booked
    Set<Integer> bookedScheduleIds = new HashSet<>();
    for (Appointment a : existingAppointments) {
      bookedScheduleIds.add(a.getScheduleId());
    }

    // Filter to available slots
    List<Schedule> availableSlots = new ArrayList<>();
    for (Schedule s : daySlots) {
      if (!bookedScheduleIds.contains(s.getScheduleId())) {
        availableSlots.add(s);
      }
    }

    if (availableSlots.isEmpty()) {
      System.out.println(
          "No available slots on "
              + selectedDate
              + " for Dr. "
              + doctor.getFullName()
              + ". All slots are booked.");
      return;
    }

    // Step 3c: Pick a slot
    Helper.printBanner("Available Slots - " + selectedDate, Helper.Theme.CYAN);
    String input =
        PrintHelper.printScheduleList(availableSlots, 0, "Select a time slot (or 'cancel'): ");
    if (input.equalsIgnoreCase("cancel") || input.isEmpty()) {
      return;
    }

    Schedule chosenSlot = null;
    try {
      int scheduleId = Integer.parseInt(input);
      for (Schedule s : availableSlots) {
        if (s.getScheduleId() == scheduleId) {
          chosenSlot = s;
          break;
        }
      }
    } catch (NumberFormatException e) {
      // ignore
    }

    if (chosenSlot == null) {
      System.out.println("Invalid selection.");
      return;
    }

    // Step 3d: Confirm
    System.out.println("\n--- Booking Summary ---");
    System.out.println("Doctor:    Dr. " + doctor.getFullName());
    System.out.println("Date:      " + selectedDate);
    System.out.println("Time:      " + chosenSlot.getStartTime() + " - " + chosenSlot.getEndTime());

    boolean confirm = InputHandler.readYesNo("Confirm this booking?");
    if (!confirm) {
      System.out.println("Booking cancelled.");
      return;
    }

    Appointment newAppointment =
        new Appointment(
            doctor.getDoctorId(), patient.getPatientId(), chosenSlot.getScheduleId(), sqlDate);
    boolean success = client.bookAppointment(newAppointment);
    if (success) {
      Helper.printLine("Appointment booked successfully!", Helper.Theme.GREEN);
    } else {
      Helper.printLine("Failed to book appointment.", Helper.Theme.RED);
    }
  }

  public static void displayAppointmentDetail(ServiceManager client, Appointment appointment) {
    System.out.println("\n--- Appointment Detail ---");
    System.out.printf("Appointment ID:  %d\n", appointment.getAppointmentId());
    System.out.printf("Date:            %s\n", appointment.getAppointmentDate());

    // Unifying info for both Doctor and Patient views
    try {
      Patient p = client.getPatientByIdForDoctor(appointment.getPatientId());
      if (p != null) {
        System.out.printf("Patient:         %s %s\n", p.getFirstName(), p.getLastName());
      }
    } catch (Exception e) {
      // ignore
    }

    String doctorName = resolveDoctorName(client, appointment.getDoctorId());
    if (!doctorName.startsWith("Unknown")) {
      System.out.printf("Doctor:          Dr. %s\n", doctorName);
    }

    try {
      Schedule s = client.getScheduleByIdForDoctor(appointment.getScheduleId());
      if (s != null) {
        System.out.printf("Time Slot:       %s - %s\n", s.getStartTime(), s.getEndTime());
      } else {
        System.out.printf(
            "Time Slot:       %s\n", resolveScheduleTime(client, appointment.getScheduleId()));
      }
    } catch (Exception e) {
      System.out.printf(
          "Time Slot:       %s\n", resolveScheduleTime(client, appointment.getScheduleId()));
    }

    System.out.printf(
        "Status:          %s\n",
        appointment.getcancelledByUserId() != null ? "Cancelled" : "Active");
    System.out.println("--------------------------");
  }

  private static String resolveDoctorName(ServiceManager client, int doctorId) {
    try {
      Doctor doc = client.getDoctorByIdForPatient(doctorId);
      if (doc != null) {
        return doc.getFirstName() + " " + doc.getLastName();
      }
    } catch (Exception ignored) {
      // Fall through to default
    }
    return "Unknown (ID: " + doctorId + ")";
  }

  private static String resolveScheduleTime(ServiceManager client, int scheduleId) {
    try {
      Schedule s = client.getScheduleByIdForPatient(scheduleId);
      if (s != null) {
        return s.getStartTime() + " - " + s.getEndTime();
      }
    } catch (Exception ignored) {
      // Fall through to default
    }
    return "Unknown time";
  }
}
