package assignment;

import static org.junit.jupiter.api.Assertions.*;

import assignment.client.services.ServiceManager;
import assignment.client.ui.screens.EditUserScreen;
import assignment.client.ui.screens.ReportScreen;
import assignment.server.Register;
import assignment.server.database.DatabaseInitializer;
import assignment.shared.auth.Role;
import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.LoginResponse;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import assignment.shared.model.Appointment;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Consultation;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Automated End-to-End System Integration Test Workflow.
 *
 * <p>Executes a complete, sequenced lifecycle testing every feature across all user roles (Admin,
 * Receptionist, Doctor, Patient) using ServiceManager and UI Screens.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SystemWorkflowTest {

  private static ServiceManager serviceManager;

  // Stored state between ordered test steps
  private static LoginResponse adminSession;
  private static LoginResponse recepSession;
  private static LoginResponse doc1Session;
  private static LoginResponse doc2Session;
  private static LoginResponse patient1Session;
  private static LoginResponse patient2Session;

  private static Doctor createdDoc1;
  private static Doctor createdDoc2;
  private static Receptionist createdRecep;
  private static Patient createdPatient1;
  private static Patient createdPatient2;
  private static Receptionist tempUser;

  private static Schedule doc1SlotA;
  private static Schedule doc1SlotB;
  private static Schedule doc1SlotC;
  private static Schedule doc2SlotD;

  private static Appointment appt1;
  private static Appointment appt2;
  private static Appointment appt3;

  private static Consultation consultation1;

  @BeforeAll
  static void setUpSystem() throws Exception {
    String keyStorePath = System.getenv("SSL_KEYSTORE_PATH");
    String keyStorePassword = System.getenv("SSL_KEYSTORE_PASSWORD");
    if (keyStorePath != null && keyStorePassword != null) {
      System.setProperty("javax.net.ssl.keyStore", keyStorePath);
      System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    }
    String trustStorePath = System.getenv("SSL_TRUSTSTORE_PATH");
    String trustStorePassword = System.getenv("SSL_TRUSTSTORE_PASSWORD");
    if (trustStorePath != null && trustStorePassword != null) {
      System.setProperty("javax.net.ssl.trustStore", trustStorePath);
      System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
    }
    if (System.getProperty("java.rmi.server.hostname") == null) {
      String host = System.getenv("SERVER_HOST");
      System.setProperty("java.rmi.server.hostname", host != null ? host : "localhost");
    }

    // Use an isolated in-memory Derby database and dedicated test RMI port
    System.setProperty("DB_URL", "jdbc:derby:memory:testdb;create=true");
    System.setProperty("SERVER_REGISTRY_PORT", "1098");

    try {
      DatabaseInitializer.initialize();
    } catch (Exception ignored) {
    }

    try (java.sql.Connection conn = assignment.server.database.DatabaseManager.getConnection();
        java.sql.Statement stmt = conn.createStatement()) {
      String hashed = BCrypt.hashpw("admin123", BCrypt.gensalt());
      stmt.executeUpdate(
          "UPDATE Users SET password = '" + hashed + "' WHERE email = 'michael@gmail.com'");
    } catch (Exception ignored) {
    }

    try {
      Register.start();
    } catch (Exception ignored) {
    }

    serviceManager = new ServiceManager();
    assertNotNull(serviceManager, "ServiceManager client must initialize successfully");
  }

  // =========================================================================
  // Helper: Capture Terminal Output with Simulated Stdin
  // =========================================================================
  private static String captureOutputWithInput(String simulatedInput, Runnable action) {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      if (simulatedInput != null) {
        assignment.client.ui.InputHandler.setScanner(
            new java.util.Scanner(
                new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8))));
      }
      System.setOut(new PrintStream(baos));
      action.run();
    } finally {
      assignment.client.ui.InputHandler.setScanner(null);
      System.setOut(originalOut);
    }
    return baos.toString();
  }

  // =========================================================================
  // Phase 1: Admin Workflow (A1, A2, A3, A4, A5, A6, A7, A8, A12)
  // =========================================================================
  @Test
  @Order(1)
  void step1_AdminWorkflow() throws Exception {
    // 1. Login as default Admin
    adminSession = serviceManager.login("michael@gmail.com", "admin123");
    assertNotNull(adminSession, "Admin login should return a valid session");
    assertEquals(Role.ADMIN, adminSession.getRole());
    serviceManager.setToken(adminSession.getToken());

    String testPasswordHash = BCrypt.hashpw("testpass123", BCrypt.gensalt());

    // A1: Register Doctor 1 & Doctor 2
    Doctor doc1 =
        new Doctor(
            "Stephen",
            "Strange",
            "doctor",
            "DOC12345",
            "strange@clinic.com",
            testPasswordHash,
            "Cardiology");
    assertTrue(serviceManager.registerUser(doc1), "Doctor 1 registration should succeed");

    Doctor doc2 =
        new Doctor(
            "Gregory",
            "House",
            "doctor",
            "DOC67890",
            "house@clinic.com",
            testPasswordHash,
            "Diagnostics");
    assertTrue(serviceManager.registerUser(doc2), "Doctor 2 registration should succeed");

    // A4: Register Receptionist
    Receptionist recep =
        new Receptionist(
            "Pam", "Beesly", "receptionist", "REC11111", "pam@clinic.com", testPasswordHash);
    assertTrue(serviceManager.registerUser(recep), "Receptionist registration should succeed");

    // A2: Register Patient 1
    Patient pat1 =
        new Patient(
            "John",
            "Doe",
            "patient",
            "PAT10001",
            "johndoe@clinic.com",
            testPasswordHash,
            0,
            "0111111111");
    assertTrue(serviceManager.registerUser(pat1), "Patient 1 registration should succeed");

    // A3: Register Secondary Admin
    ClinicAdministrator subAdmin =
        new ClinicAdministrator(
            "Jim", "Halpert", "admin", "ADM99999", "jim@clinic.com", testPasswordHash);
    assertTrue(
        serviceManager.registerUser(subAdmin), "Secondary admin registration should succeed");

    // Temp User for deletion test
    tempUser =
        new Receptionist(
            "Temp", "User", "receptionist", "TMP00000", "tempuser@clinic.com", testPasswordHash);
    assertTrue(serviceManager.registerUser(tempUser), "Temp user registration should succeed");

    // A5: List All Users & verify presence
    List<User> allUsers = serviceManager.getAllUsers();
    assertNotNull(allUsers);
    assertTrue(allUsers.size() >= 6, "All registered users should be returned");

    for (User u : allUsers) {
      if ("strange@clinic.com".equalsIgnoreCase(u.getEmail()) && u instanceof Doctor d) {
        createdDoc1 = d;
      } else if ("house@clinic.com".equalsIgnoreCase(u.getEmail()) && u instanceof Doctor d) {
        createdDoc2 = d;
      } else if ("pam@clinic.com".equalsIgnoreCase(u.getEmail()) && u instanceof Receptionist r) {
        createdRecep = r;
      } else if ("johndoe@clinic.com".equalsIgnoreCase(u.getEmail()) && u instanceof Patient p) {
        createdPatient1 = p;
      } else if ("tempuser@clinic.com".equalsIgnoreCase(u.getEmail())
          && u instanceof Receptionist r) {
        tempUser = r;
      }
    }
    assertNotNull(createdDoc1, "Doctor 1 entity should be found in user list");
    assertNotNull(createdDoc2, "Doctor 2 entity should be found in user list");
    assertNotNull(createdRecep, "Receptionist entity should be found in user list");
    assertNotNull(createdPatient1, "Patient 1 entity should be found in user list");
    assertNotNull(tempUser, "Temp user entity should be found in user list");

    // A6: Search User by Name / Email & Terminal Output Verification
    String searchTerminalOutput =
        captureOutputWithInput(
            "s\nStephen\n5\nback\n", () -> EditUserScreen.display(serviceManager));
    assertTrue(
        searchTerminalOutput.contains("Stephen Strange")
            || searchTerminalOutput.contains("PROFILE DISPLAY"),
        "Terminal output should display user profile during search");

    // A7: Edit User details (Doctor 2 Specialization update)
    Doctor updatedDoc2 =
        new Doctor(
            createdDoc2.getDoctorId(),
            createdDoc2.getUserId(),
            createdDoc2.getFirstName(),
            createdDoc2.getLastName(),
            createdDoc2.getUserRole(),
            createdDoc2.getIcPassportNo(),
            createdDoc2.getEmail(),
            createdDoc2.getPasswordHash(),
            "Neurology",
            createdDoc2.getCreatedAt(),
            createdDoc2.isDeleted());
    assertTrue(serviceManager.editUser(updatedDoc2), "Doctor 2 specialization edit should succeed");

    // A8: Delete User (Temp User soft delete)
    assertTrue(serviceManager.deleteUser(tempUser), "Temp user deletion should succeed");

    // A12: Logout Admin
    serviceManager.clearToken();
  }

  // =========================================================================
  // Phase 2: Receptionist Workflow (R1, R2)
  // =========================================================================
  @Test
  @Order(2)
  void step2_ReceptionistWorkflow() throws Exception {
    // 1. Login as Receptionist
    recepSession = serviceManager.login("pam@clinic.com", "testpass123");
    assertNotNull(recepSession, "Receptionist login should succeed");
    assertEquals(Role.RECEPTIONIST, recepSession.getRole());
    serviceManager.setToken(recepSession.getToken());

    // R1: Register Walk-in Patient (Patient 2)
    String testPasswordHash = BCrypt.hashpw("testpass123", BCrypt.gensalt());
    Patient pat2 =
        new Patient(
            "Jane",
            "Smith",
            "patient",
            "PAT20002",
            "janesmith@clinic.com",
            testPasswordHash,
            0,
            "0222222222");
    assertTrue(
        serviceManager.registerUser(pat2), "Receptionist patient registration should succeed");

    // R2: Logout Receptionist
    serviceManager.clearToken();
  }

  // =========================================================================
  // Phase 3: Doctor Availability & Schedule Management (D1, D2, D3, D11)
  // =========================================================================
  @Test
  @Order(3)
  void step3_DoctorScheduleWorkflow() throws Exception {
    // 1. Login as Doctor 1
    doc1Session = serviceManager.login("strange@clinic.com", "testpass123");
    assertNotNull(doc1Session, "Doctor 1 login should succeed");
    assertEquals(Role.DOCTOR, doc1Session.getRole());
    serviceManager.setToken(doc1Session.getToken());

    // D2: Add Schedules for Doctor 1 (userId passed, converted to doctorId on
    // server)
    Schedule slotA =
        new Schedule(
            0,
            doc1Session.getUserId(),
            "MONDAY",
            Time.valueOf("09:00:00"),
            Time.valueOf("10:00:00"));
    assertTrue(serviceManager.addSchedule(slotA), "Adding Slot A should succeed");

    Schedule slotB =
        new Schedule(
            0,
            doc1Session.getUserId(),
            "MONDAY",
            Time.valueOf("10:00:00"),
            Time.valueOf("11:00:00"));
    assertTrue(serviceManager.addSchedule(slotB), "Adding Slot B should succeed");

    Schedule slotC =
        new Schedule(
            0,
            doc1Session.getUserId(),
            "TUESDAY",
            Time.valueOf("14:00:00"),
            Time.valueOf("15:00:00"));
    assertTrue(serviceManager.addSchedule(slotC), "Adding Slot C should succeed");

    // D1: View Schedules
    List<Schedule> doc1Schedules = serviceManager.getSchedulesByDoctor(doc1Session.getUserId());
    assertNotNull(doc1Schedules);
    assertTrue(doc1Schedules.size() >= 3, "Doctor 1 should have at least 3 schedule slots");

    for (Schedule s : doc1Schedules) {
      if ("MONDAY".equalsIgnoreCase(s.getDay())
          && s.getStartTime().toString().startsWith("09:00")) {
        doc1SlotA = s;
      } else if ("MONDAY".equalsIgnoreCase(s.getDay())
          && s.getStartTime().toString().startsWith("10:00")) {
        doc1SlotB = s;
      } else if ("TUESDAY".equalsIgnoreCase(s.getDay())) {
        doc1SlotC = s;
      }
    }
    assertNotNull(doc1SlotA);
    assertNotNull(doc1SlotB);
    assertNotNull(doc1SlotC);

    // D3: Delete Slot C
    assertTrue(
        serviceManager.deleteSchedule(doc1SlotC.getScheduleId()),
        "Deleting temporary schedule slot C should succeed");

    // D11: Logout Doctor 1
    serviceManager.clearToken();

    // 2. Login as Doctor 2 to configure their schedule
    doc2Session = serviceManager.login("house@clinic.com", "testpass123");
    assertNotNull(doc2Session, "Doctor 2 login should succeed");
    serviceManager.setToken(doc2Session.getToken());

    Schedule slotD =
        new Schedule(
            0,
            doc2Session.getUserId(),
            "WEDNESDAY",
            Time.valueOf("09:00:00"),
            Time.valueOf("10:00:00"));
    assertTrue(serviceManager.addSchedule(slotD), "Adding Doctor 2 Slot D should succeed");

    List<Schedule> doc2Schedules = serviceManager.getSchedulesByDoctor(doc2Session.getUserId());
    assertNotNull(doc2Schedules);
    assertFalse(doc2Schedules.isEmpty());
    doc2SlotD = doc2Schedules.get(0);
    assertNotNull(doc2SlotD);

    // Logout Doctor 2
    serviceManager.clearToken();
  }

  // =========================================================================
  // Phase 4: Patient Workflow (P1, P2, P3, P4, P5, P6, P7, P10)
  // =========================================================================
  @Test
  @Order(4)
  void step4_PatientAppointmentWorkflow() throws Exception {
    // 1. Login as Patient 1
    patient1Session = serviceManager.login("johndoe@clinic.com", "testpass123");
    assertNotNull(patient1Session, "Patient 1 login should succeed");
    assertEquals(Role.PATIENT, patient1Session.getRole());
    serviceManager.setToken(patient1Session.getToken());

    // P1: View Profile
    Patient currentProfile = serviceManager.getPatientByUserId(patient1Session.getUserId());
    assertNotNull(currentProfile);
    assertEquals("John", currentProfile.getFirstName());
    assertTrue(currentProfile.getMedicalRecordId() > 0);

    // P2: Update Profile
    Patient updatedProfile =
        new Patient(
            currentProfile.getPatientId(),
            currentProfile.getUserId(),
            "Johnathan",
            currentProfile.getLastName(),
            currentProfile.getUserRole(),
            currentProfile.getIcPassportNo(),
            currentProfile.getEmail(),
            currentProfile.getPasswordHash(),
            currentProfile.getMedicalRecordId(),
            "0198887777",
            currentProfile.getCreatedAt(),
            currentProfile.isDeleted());
    assertTrue(
        serviceManager.updatePatientProfile(updatedProfile), "Profile update should succeed");

    // P3: Filter & Browse Doctors
    List<Doctor> doctors = serviceManager.getAllDoctorsForPatient();
    assertNotNull(doctors);
    assertTrue(doctors.size() >= 2);

    Doctor doctor1 = null;
    Doctor doctor2 = null;
    for (Doctor d : doctors) {
      if (d.getUserId() == doc1Session.getUserId()) doctor1 = d;
      if (d.getUserId() == doc2Session.getUserId()) doctor2 = d;
    }
    assertNotNull(doctor1);
    assertNotNull(doctor2);

    // P4: View Doctor Availability & Schedules
    List<Schedule> doc1Scheds =
        serviceManager.getSchedulesByDoctorIdForPatient(doctor1.getDoctorId());
    assertNotNull(doc1Scheds);
    assertFalse(doc1Scheds.isEmpty());

    // Compute upcoming dates
    LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
    LocalDate nextWednesday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));

    Date mondayDate = Date.valueOf(nextMonday);
    Date wednesdayDate = Date.valueOf(nextWednesday);

    // Check doctor slots available for date
    List<Appointment> existingAppts =
        serviceManager.getAppointmentsByDoctorAndDate(doctor1.getDoctorId(), mondayDate);
    assertNotNull(existingAppts);

    // P5: Book Appointments
    // Appt 1: Doctor 1, Slot A (Monday 09:00)
    Appointment newAppt1 =
        new Appointment(
            doctor1.getDoctorId(),
            currentProfile.getPatientId(),
            doc1SlotA.getScheduleId(),
            mondayDate);
    assertTrue(serviceManager.bookAppointment(newAppt1), "Booking Appt 1 should succeed");

    // Appt 2: Doctor 1, Slot B (Monday 10:00) - to be cancelled by patient
    Appointment newAppt2 =
        new Appointment(
            doctor1.getDoctorId(),
            currentProfile.getPatientId(),
            doc1SlotB.getScheduleId(),
            mondayDate);
    assertTrue(serviceManager.bookAppointment(newAppt2), "Booking Appt 2 should succeed");

    // Appt 3: Doctor 2, Slot D (Wednesday 09:00) - to be cancelled by doctor
    Appointment newAppt3 =
        new Appointment(
            doctor2.getDoctorId(),
            currentProfile.getPatientId(),
            doc2SlotD.getScheduleId(),
            wednesdayDate);
    assertTrue(serviceManager.bookAppointment(newAppt3), "Booking Appt 3 should succeed");

    // P6: View Upcoming Appointments
    List<Appointment> upcoming =
        serviceManager.getUpcomingAppointments(patient1Session.getUserId());
    assertNotNull(upcoming);
    assertTrue(upcoming.size() >= 3, "Patient should have at least 3 upcoming appointments");

    for (Appointment a : upcoming) {
      if (a.getDoctorId() == doctor1.getDoctorId()
          && a.getScheduleId() == doc1SlotA.getScheduleId()) {
        appt1 = a;
      } else if (a.getDoctorId() == doctor1.getDoctorId()
          && a.getScheduleId() == doc1SlotB.getScheduleId()) {
        appt2 = a;
      } else if (a.getDoctorId() == doctor2.getDoctorId()) {
        appt3 = a;
      }
    }
    assertNotNull(appt1, "Appt 1 must be created");
    assertNotNull(appt2, "Appt 2 must be created");
    assertNotNull(appt3, "Appt 3 must be created");

    // P7: Cancel Appointment 2 (by Patient)
    assertTrue(
        serviceManager.cancelAppointment(appt2.getAppointmentId(), patient1Session.getUserId()),
        "Patient cancelling Appt 2 should succeed");

    // Verify Appt 2 is no longer in upcoming list
    List<Appointment> upcomingAfterCancel =
        serviceManager.getUpcomingAppointments(patient1Session.getUserId());
    boolean foundCancelled = false;
    for (Appointment a : upcomingAfterCancel) {
      if (a.getAppointmentId() == appt2.getAppointmentId()) {
        foundCancelled = true;
        break;
      }
    }
    assertFalse(foundCancelled, "Cancelled appointment should not appear in upcoming appointments");

    // P10: Logout Patient 1
    serviceManager.clearToken();
  }

  // =========================================================================
  // Phase 5: Doctor Appointments & Consultations (D4, D5, D6, D7, D8, D9, D10)
  // =========================================================================
  @Test
  @Order(5)
  void step5_DoctorConsultationWorkflow() throws Exception {
    // 1. Login as Doctor 2 to cancel Appointment 3
    doc2Session = serviceManager.login("house@clinic.com", "testpass123");
    serviceManager.setToken(doc2Session.getToken());

    // D4: Doctor 2 views upcoming appointments
    List<Appointment> doc2Upcoming =
        serviceManager.getUpcomingAppointmentsByDoctorUserId(doc2Session.getUserId());
    assertNotNull(doc2Upcoming);
    assertFalse(doc2Upcoming.isEmpty());

    // D5: Doctor 2 cancels Appointment 3
    assertTrue(
        serviceManager.cancelAppointmentForDoctor(
            appt3.getAppointmentId(), doc2Session.getUserId()),
        "Doctor 2 cancelling Appt 3 should succeed");
    serviceManager.clearToken();

    // 2. Login as Doctor 1
    doc1Session = serviceManager.login("strange@clinic.com", "testpass123");
    serviceManager.setToken(doc1Session.getToken());

    // D4: Doctor 1 views upcoming appointments
    List<Appointment> doc1Upcoming =
        serviceManager.getUpcomingAppointmentsByDoctorUserId(doc1Session.getUserId());
    assertNotNull(doc1Upcoming);
    assertFalse(doc1Upcoming.isEmpty());

    // D6: Initiate Consultation for Appointment 1
    Consultation newCon =
        new Consultation(
            appt1.getAppointmentId(),
            "Patient diagnosed with mild fatigue. Recommended rest and hydration.",
            85.00);
    assertTrue(
        serviceManager.addConsultation(newCon), "Recording consultation for Appt 1 should succeed");

    // D7: View Patients with Consultations
    List<Patient> consultationPatients =
        serviceManager.getPatientsWithConsultations(doc1Session.getUserId());
    assertNotNull(consultationPatients);
    assertFalse(consultationPatients.isEmpty());

    // D8: View Patient History & Consultations
    Patient treatedPatient = consultationPatients.get(0);
    List<Appointment> patientAppts =
        serviceManager.getAppointmentsWithConsultationByDoctorAndPatient(
            doc1Session.getUserId(), treatedPatient.getPatientId());
    assertNotNull(patientAppts);
    assertFalse(patientAppts.isEmpty());

    Consultation retrievedCon =
        serviceManager.getConsultationByAppointmentId(appt1.getAppointmentId());
    assertNotNull(retrievedCon);
    assertEquals(85.00, retrievedCon.getFee(), 0.001);
    consultation1 = retrievedCon;

    // D9: Manage Consultations (List all)
    List<Consultation> allConsultations = serviceManager.getAllConsultations();
    assertNotNull(allConsultations);
    assertFalse(allConsultations.isEmpty());

    // D10: Update Consultation Notes
    Consultation updatedCon =
        new Consultation(
            consultation1.getConsultationId(),
            consultation1.getAppointmentId(),
            "Follow-up: Patient condition improved significantly.",
            consultation1.getFee(),
            consultation1.getCreatedAt());
    assertTrue(
        serviceManager.updateConsultation(updatedCon),
        "Updating consultation notes should succeed");

    // Verify consultation notes were updated
    Consultation recheckCon =
        serviceManager.getConsultationByAppointmentId(appt1.getAppointmentId());
    assertEquals("Follow-up: Patient condition improved significantly.", recheckCon.getContent());

    serviceManager.clearToken();
  }

  // =========================================================================
  // Phase 6: Patient Review of History & Notes (P8, P9, P10)
  // =========================================================================
  @Test
  @Order(6)
  void step6_PatientHistoryAndNotesWorkflow() throws Exception {
    // Login as Patient 1
    patient1Session = serviceManager.login("johndoe@clinic.com", "testpass123");
    serviceManager.setToken(patient1Session.getToken());

    // P8: View Past Appointments
    List<Appointment> pastAppointments =
        serviceManager.getPastAppointments(patient1Session.getUserId());
    assertNotNull(pastAppointments);
    // Should include cancelled appointments (Appt 2 and Appt 3)
    assertTrue(
        pastAppointments.size() >= 1, "Past appointments should contain cancelled appointments");

    // P9: View Consultation Notes for Completed Visits
    List<Consultation> myConsultations =
        serviceManager.getConsultationsByPatient(patient1Session.getUserId());
    assertNotNull(myConsultations);
    assertFalse(myConsultations.isEmpty(), "Patient should have at least 1 completed consultation");
    assertEquals(
        "Follow-up: Patient condition improved significantly.",
        myConsultations.get(0).getContent());
    assertEquals(85.00, myConsultations.get(0).getFee(), 0.001);

    // P10: Logout Patient 1
    serviceManager.clearToken();
  }

  // =========================================================================
  // Phase 7: Admin Reporting & Terminal Output Verification (A9, A10, A11)
  // =========================================================================
  @Test
  @Order(7)
  void step7_AdminReportsAndTerminalVerification() throws Exception {
    // Login as Admin
    adminSession = serviceManager.login("michael@gmail.com", "admin123");
    serviceManager.setToken(adminSession.getToken());

    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();

    // A9: Monthly Appointment Report (Data Assertions)
    MonthlyAppointmentReport apptReport =
        serviceManager.getMonthlyAppointmentReport(currentYear, currentMonth);
    assertNotNull(apptReport);
    assertTrue(apptReport.getTotalMade() >= 3, "Total appointments made should be >= 3");
    assertTrue(
        apptReport.getCancelledByPatient() >= 1, "Cancelled by patient count should be >= 1");
    assertTrue(apptReport.getCancelledByDoctor() >= 1, "Cancelled by doctor count should be >= 1");

    // A9: Monthly Appointment Report Terminal Output UI Verification
    String apptReportTerminalOut =
        captureOutputWithInput(
            "current\n\n", () -> ReportScreen.displayMonthlyAppointmentReport(serviceManager));
    assertTrue(
        apptReportTerminalOut.contains("APPOINTMENT REPORT")
            && apptReportTerminalOut.contains("Total Appointments Made"),
        "Terminal output must render the monthly appointment report table");

    // A10: Doctor Consultation Report (Data Assertions)
    DoctorConsultationReport docReport =
        serviceManager.getDoctorConsultationReport(currentYear, currentMonth);
    assertNotNull(docReport);
    assertTrue(docReport.getTotalConsultations() >= 1, "Total consultations should be >= 1");
    assertTrue(
        docReport.getTotalEarning() != null && docReport.getTotalEarning().doubleValue() >= 85.00,
        "Total earnings should be >= 85.00");

    // A10: Doctor Consultation Report Terminal Output UI Verification
    String docReportTerminalOut =
        captureOutputWithInput(
            "current\n\n", () -> ReportScreen.displayDoctorConsultationReport(serviceManager));
    assertTrue(
        docReportTerminalOut.contains("DOCTOR CONSULTATION REPORT")
            && docReportTerminalOut.contains("Total Earnings"),
        "Terminal output must render the doctor consultation report table");

    // A11: Patient Visit Summary Report (Data Assertions)
    PatientVisitSummaryReport visitReport =
        serviceManager.getPatientVisitSummaryReport(currentYear, currentMonth);
    assertNotNull(visitReport);
    assertTrue(visitReport.getNewPatientsCount() >= 2, "New patients registered should be >= 2");
    assertFalse(
        visitReport.getPatientVisits().isEmpty(), "Patient visits list should not be empty");

    // A11: Patient Visit Summary Report Terminal Output UI Verification
    String visitReportTerminalOut =
        captureOutputWithInput(
            "current\n\n", () -> ReportScreen.displayPatientVisitSummary(serviceManager));
    assertTrue(
        visitReportTerminalOut.contains("PATIENT VISIT SUMMARY")
            && visitReportTerminalOut.contains("New Patients Registered"),
        "Terminal output must render the patient visit summary report table");

    // Logout Admin
    serviceManager.clearToken();
  }

  @AfterAll
  static void tearDownSystem() throws Exception {
    if (serviceManager != null) {
      serviceManager.clearToken();
    }
    try {
      java.sql.DriverManager.getConnection("jdbc:derby:memory:testdb;drop=true");
    } catch (java.sql.SQLException ignored) {
      // Derby throws expected exception (SQLState 08006) on successful drop
    }
  }
}
