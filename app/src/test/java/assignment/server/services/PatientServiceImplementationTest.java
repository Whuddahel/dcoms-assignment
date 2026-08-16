package assignment.server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.repository.AppointmentRepository;
import assignment.server.database.repository.ConsultationRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ScheduleRepository;
import assignment.shared.auth.Role;
import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class PatientServiceImplementationTest {

  private static final String TOKEN = "token123";

  private Session sessionWithRole(Role role) {
    User user =
        new User(
            1,
            "Jane",
            "Smith",
            role,
            "IC1",
            "jane@test.com",
            "hash",
            new Timestamp(System.currentTimeMillis()),
            false);
    return new Session(user);
  }

  private Patient patientFixture(int patientId, int userId) {
    return new Patient(
        patientId,
        userId,
        "John",
        "Doe",
        "patient",
        "IC2",
        "john@test.com",
        null,
        100,
        "0123456789",
        new Timestamp(System.currentTimeMillis()),
        false);
  }

  // ==========================================
  // Profile
  // ==========================================

  @Test
  void getPatientByUserId_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Patient expected = patientFixture(1, 5);
      patientRepoMock.when(() -> PatientRepository.getPatientByUserId(5)).thenReturn(expected);

      PatientServiceImplementation service = new PatientServiceImplementation();
      Patient result = service.getPatientByUserId(TOKEN, 5);

      assertEquals(expected, result);
    }
  }

  @Test
  void updatePatientProfile_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Patient patient = patientFixture(1, 5);
      patientRepoMock.when(() -> PatientRepository.updatePatient(patient)).thenReturn(true);

      PatientServiceImplementation service = new PatientServiceImplementation();
      boolean result = service.updatePatientProfile(TOKEN, patient);

      assertTrue(result);
    }
  }

  @Test
  void updatePatientProfile_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Patient patient = patientFixture(1, 5);
      patientRepoMock
          .when(() -> PatientRepository.updatePatient(patient))
          .thenThrow(new SQLException());

      PatientServiceImplementation service = new PatientServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.updatePatientProfile(TOKEN, patient));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  // ==========================================
  // Appointments (getUpcoming/getPast contain real filtering logic, not just passthrough)
  // ==========================================

  @Test
  void getUpcomingAppointments_filtersOutPastAndConsulted() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Patient patient = patientFixture(1, 5);
      patientRepoMock.when(() -> PatientRepository.getPatientByUserId(5)).thenReturn(patient);

      Date tomorrow = Date.valueOf(LocalDate.now().plusDays(1));
      Date yesterday = Date.valueOf(LocalDate.now().minusDays(1));
      Timestamp now = new Timestamp(System.currentTimeMillis());

      Appointment futureActive = new Appointment(1, 10, 1, 20, tomorrow, now, null); // included
      Appointment pastActive =
          new Appointment(3, 10, 1, 22, yesterday, now, null); // excluded (before today)
      Appointment futureCancelled =
          new Appointment(
              4, 10, 1, 23, tomorrow, now, 99); // included (today/future, even if cancelled)
      Appointment futureWithConsultation =
          new Appointment(5, 10, 1, 24, tomorrow, now, null); // excluded (has consultation)

      consultRepoMock
          .when(() -> ConsultationRepository.getConsultationByAppointmentId(5))
          .thenReturn(new Consultation(1, 5, "Notes", 50.0, now));

      apptRepoMock
          .when(() -> AppointmentRepository.getAppointmentsByPatientId(1))
          .thenReturn(List.of(futureActive, pastActive, futureCancelled, futureWithConsultation));

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Appointment> result = service.getUpcomingAppointments(TOKEN, 5);

      assertEquals(List.of(futureActive, futureCancelled), result);
    }
  }

  @Test
  void getUpcomingAppointments_patientNotFound_returnsEmptyList() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));
      patientRepoMock.when(() -> PatientRepository.getPatientByUserId(5)).thenReturn(null);

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Appointment> result = service.getUpcomingAppointments(TOKEN, 5);

      assertTrue(result.isEmpty());
    }
  }

  @Test
  void getPastAppointments_includesPastAndConsulted_excludesFutureWithoutConsultation()
      throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Patient patient = patientFixture(1, 5);
      patientRepoMock.when(() -> PatientRepository.getPatientByUserId(5)).thenReturn(patient);

      Date tomorrow = Date.valueOf(LocalDate.now().plusDays(1));
      Date yesterday = Date.valueOf(LocalDate.now().minusDays(1));
      Timestamp now = new Timestamp(System.currentTimeMillis());

      Appointment pastActive =
          new Appointment(1, 10, 1, 20, yesterday, now, null); // included (before today)
      Appointment futureCancelled =
          new Appointment(2, 10, 1, 21, tomorrow, now, 99); // excluded (future, shown in upcoming)
      Appointment futureActive = new Appointment(3, 10, 1, 22, tomorrow, now, null); // excluded
      Appointment futureWithConsultation =
          new Appointment(4, 10, 1, 23, tomorrow, now, null); // included (has consultation)

      consultRepoMock
          .when(() -> ConsultationRepository.getConsultationByAppointmentId(4))
          .thenReturn(new Consultation(1, 4, "Notes", 50.0, now));

      apptRepoMock
          .when(() -> AppointmentRepository.getAppointmentsByPatientId(1))
          .thenReturn(List.of(pastActive, futureCancelled, futureActive, futureWithConsultation));

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Appointment> result = service.getPastAppointments(TOKEN, 5);

      assertEquals(List.of(pastActive, futureWithConsultation), result);
    }
  }

  @Test
  void getPastAppointments_patientNotFound_returnsEmptyList() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));
      patientRepoMock.when(() -> PatientRepository.getPatientByUserId(5)).thenReturn(null);

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Appointment> result = service.getPastAppointments(TOKEN, 5);

      assertTrue(result.isEmpty());
    }
  }

  @Test
  void cancelAppointment_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));
      apptRepoMock.when(() -> AppointmentRepository.cancelAppointment(30, 5)).thenReturn(true);

      PatientServiceImplementation service = new PatientServiceImplementation();
      boolean result = service.cancelAppointment(TOKEN, 30, 5);

      assertTrue(result);
    }
  }

  @Test
  void cancelAppointment_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));
      apptRepoMock
          .when(() -> AppointmentRepository.cancelAppointment(30, 5))
          .thenThrow(new SQLException());

      PatientServiceImplementation service = new PatientServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.cancelAppointment(TOKEN, 30, 5));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void bookAppointment_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Appointment appointment =
          new Appointment(10, 1, 20, Date.valueOf(LocalDate.now().plusDays(1)));
      apptRepoMock.when(() -> AppointmentRepository.addAppointment(appointment)).thenReturn(true);

      PatientServiceImplementation service = new PatientServiceImplementation();
      boolean result = service.bookAppointment(TOKEN, appointment);

      assertTrue(result);
    }
  }

  @Test
  void bookAppointment_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Appointment appointment =
          new Appointment(10, 1, 20, Date.valueOf(LocalDate.now().plusDays(1)));
      apptRepoMock
          .when(() -> AppointmentRepository.addAppointment(appointment))
          .thenThrow(new SQLException());

      PatientServiceImplementation service = new PatientServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.bookAppointment(TOKEN, appointment));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  // ==========================================
  // Doctors & Scheduling (patient browsing, not the doctor's own management)
  // ==========================================

  @Test
  void getAllDoctors_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      List<Doctor> expected =
          List.of(
              new Doctor(
                  1,
                  2,
                  "John",
                  "Doe",
                  "doctor",
                  "IC2",
                  "john@test.com",
                  null,
                  "GP",
                  new Timestamp(System.currentTimeMillis()),
                  false));
      doctorRepoMock.when(DoctorRepository::getAllDoctors).thenReturn(expected);

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Doctor> result = service.getAllDoctors(TOKEN);

      assertEquals(expected, result);
    }
  }

  @Test
  void getSchedulesByDoctorId_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      List<Schedule> expected =
          List.of(
              new Schedule(7, 10, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00")));
      scheduleRepoMock
          .when(() -> ScheduleRepository.getSchedulesByDoctorId(10))
          .thenReturn(expected);

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Schedule> result = service.getSchedulesByDoctorId(TOKEN, 10);

      assertEquals(expected, result);
    }
  }

  @Test
  void getAppointmentsByDoctorAndDate_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Date date = Date.valueOf(LocalDate.now().plusDays(1));
      List<Appointment> expected =
          List.of(
              new Appointment(1, 10, 1, 20, date, new Timestamp(System.currentTimeMillis()), null));
      apptRepoMock
          .when(() -> AppointmentRepository.getAppointmentsByDoctorAndDate(10, date))
          .thenReturn(expected);

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Appointment> result = service.getAppointmentsByDoctorAndDate(TOKEN, 10, date);

      assertEquals(expected, result);
    }
  }

  // ==========================================
  // Consultations (read-only for patient)
  // ==========================================

  @Test
  void getConsultationsByPatient_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Patient patient = patientFixture(1, 5);
      patientRepoMock.when(() -> PatientRepository.getPatientByUserId(5)).thenReturn(patient);

      List<Consultation> expected =
          List.of(
              new Consultation(1, 30, "Notes", 50.0, new Timestamp(System.currentTimeMillis())));
      consultRepoMock
          .when(() -> ConsultationRepository.getConsultationsByPatientId(1))
          .thenReturn(expected);

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Consultation> result = service.getConsultationsByPatient(TOKEN, 5);

      assertEquals(expected, result);
    }
  }

  @Test
  void getConsultationsByPatient_patientNotFound_returnsEmptyList() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));
      patientRepoMock.when(() -> PatientRepository.getPatientByUserId(5)).thenReturn(null);

      PatientServiceImplementation service = new PatientServiceImplementation();
      List<Consultation> result = service.getConsultationsByPatient(TOKEN, 5);

      assertTrue(result.isEmpty());
    }
  }

  // ----------------------------------------------------------------------------------
  // The two methods below (getDoctorById, getScheduleById) are hosted in
  // PatientServiceImplementation, but per the Doctor-menu call trace, they are ALSO
  // invoked from the Doctor side (AppointmentScreen.displayAppointmentDetail's
  // resolveDoctorName()/resolveScheduleTime() helpers, used by both "View Patient
  // Appointments" and "View Medical History"). They're tested here since that's
  // where the methods actually live, not duplicated into DoctorServiceImplementationTest.
  // ----------------------------------------------------------------------------------

  @Test
  void getDoctorById_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Doctor expected =
          new Doctor(
              1,
              2,
              "John",
              "Doe",
              "doctor",
              "IC2",
              "john@test.com",
              null,
              "GP",
              new Timestamp(System.currentTimeMillis()),
              false);
      doctorRepoMock.when(() -> DoctorRepository.getDoctorById(1)).thenReturn(expected);

      PatientServiceImplementation service = new PatientServiceImplementation();
      Doctor result = service.getDoctorById(TOKEN, 1);

      assertEquals(expected, result);
    }
  }

  @Test
  void getScheduleById_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Schedule expected =
          new Schedule(7, 10, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00"));
      scheduleRepoMock.when(() -> ScheduleRepository.getScheduleById(7)).thenReturn(expected);

      PatientServiceImplementation service = new PatientServiceImplementation();
      Schedule result = service.getScheduleById(TOKEN, 7);

      assertEquals(expected, result);
    }
  }

  // ----------------------------------------------------------------------------------

  // ==========================================
  // Shared auth checks
  // ==========================================

  @Test
  void accessDenied_whenRoleLacksPermission() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      // RECEPTIONIST does not have APPOINTMENT_CREATE
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Appointment appointment =
          new Appointment(10, 1, 20, Date.valueOf(LocalDate.now().plusDays(1)));
      PatientServiceImplementation service = new PatientServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.bookAppointment(TOKEN, appointment));
      assertEquals("ACCESS_DENIED", ex.getMessage());
    }
  }

  @Test
  void invalidSession_whenTokenUnknown() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock.when(() -> SessionManager.getSession(TOKEN)).thenReturn(null);

      PatientServiceImplementation service = new PatientServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.getPatientByUserId(TOKEN, 5));
      assertEquals("INVALID_SESSION", ex.getMessage());
    }
  }
}
