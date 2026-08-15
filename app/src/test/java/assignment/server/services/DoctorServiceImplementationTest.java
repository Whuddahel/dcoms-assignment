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
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class DoctorServiceImplementationTest {

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

  @Test
  void getUpcomingAppointmentsByDoctorUserId_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(10);

      List<Appointment> expected =
          List.of(
              new Appointment(
                  1,
                  10,
                  20,
                  30,
                  new Date(System.currentTimeMillis()),
                  new Timestamp(System.currentTimeMillis()),
                  null));
      apptRepoMock
          .when(() -> AppointmentRepository.getUpcomingAppointmentsByDoctorId(10))
          .thenReturn(expected);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
      List<Appointment> result = service.getUpcomingAppointmentsByDoctorUserId(TOKEN, 5);

      assertEquals(expected, result);
      apptRepoMock.verify(() -> AppointmentRepository.getUpcomingAppointmentsByDoctorId(10));
    }
  }

  @Test
  void getUpcomingAppointmentsByDoctorUserId_noDoctorRecordForUser() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(-1);

      DoctorServiceImplementation service = new DoctorServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getUpcomingAppointmentsByDoctorUserId(TOKEN, 5));
      assertTrue(ex.getMessage().contains("No doctor found"));
    }
  }

  @Test
  void getUpcomingAppointmentsByDoctorUserId_dbErrorResolvingDoctorId() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock
          .when(() -> DoctorRepository.getDoctorIdByUserId(5))
          .thenThrow(new SQLException());

      DoctorServiceImplementation service = new DoctorServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getUpcomingAppointmentsByDoctorUserId(TOKEN, 5));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void getPatientsWithConsultations_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(10);

      List<Patient> expected =
          List.of(
              new Patient(
                  1,
                  2,
                  "John",
                  "Doe",
                  "patient",
                  "IC2",
                  "john@test.com",
                  null,
                  100,
                  "0123456789",
                  new Timestamp(System.currentTimeMillis()),
                  false));
      consultRepoMock
          .when(() -> ConsultationRepository.getPatientsWithConsultationsByDoctorId(10))
          .thenReturn(expected);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
      List<Patient> result = service.getPatientsWithConsultations(TOKEN, 5);

      assertEquals(expected, result);
    }
  }

  @Test
  void getAppointmentsWithConsultationByDoctorAndPatient_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(10);

      List<Appointment> expected =
          List.of(
              new Appointment(
                  1,
                  10,
                  2,
                  30,
                  new Date(System.currentTimeMillis()),
                  new Timestamp(System.currentTimeMillis()),
                  null));
      apptRepoMock
          .when(() -> AppointmentRepository.getAppointmentsByDoctorAndPatient(10, 2))
          .thenReturn(expected);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
      List<Appointment> result =
          service.getAppointmentsWithConsultationByDoctorAndPatient(TOKEN, 5, 2);

      assertEquals(expected, result);
    }
  }

  @Test
  void getConsultationByAppointmentId_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Consultation expected =
          new Consultation(1, 30, "Notes", 50.0, new Timestamp(System.currentTimeMillis()));
      consultRepoMock
          .when(() -> ConsultationRepository.getConsultationByAppointmentId(30))
          .thenReturn(expected);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
      Consultation result = service.getConsultationByAppointmentId(TOKEN, 30);

      assertEquals(expected, result);
    }
  }

  @Test
  void getConsultationByAppointmentId_notFound_returnsNull() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      consultRepoMock
          .when(() -> ConsultationRepository.getConsultationByAppointmentId(99))
          .thenReturn(null);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
      Consultation result = service.getConsultationByAppointmentId(TOKEN, 99);

      assertNull(result);
    }
  }

  @Test
  void getScheduleById_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Schedule expected =
          new Schedule(7, 10, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00"));
      scheduleRepoMock.when(() -> ScheduleRepository.getScheduleById(7)).thenReturn(expected);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
      Schedule result = service.getScheduleById(TOKEN, 7);

      assertEquals(expected, result);
    }
  }

  @Test
  void getPatientById_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Patient expected =
          new Patient(
              1,
              2,
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
      patientRepoMock.when(() -> PatientRepository.getPatientById(1)).thenReturn(expected);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
      Patient result = service.getPatientById(TOKEN, 1);

      assertEquals(expected, result);
    }
  }

  @Test
  void cancelAppointment_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<AppointmentRepository> apptRepoMock =
            mockStatic(AppointmentRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      apptRepoMock.when(() -> AppointmentRepository.cancelAppointment(30, 5)).thenReturn(true);

      DoctorServiceImplementation service = new DoctorServiceImplementation();
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
          .thenReturn(sessionWithRole(Role.DOCTOR));
      apptRepoMock
          .when(() -> AppointmentRepository.cancelAppointment(30, 5))
          .thenThrow(new SQLException());

      DoctorServiceImplementation service = new DoctorServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.cancelAppointment(TOKEN, 30, 5));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void accessDenied_whenRoleLacksPermission() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      // RECEPTIONIST does not have CONSULTATION_READ
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      DoctorServiceImplementation service = new DoctorServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getConsultationByAppointmentId(TOKEN, 30));
      assertEquals("ACCESS_DENIED", ex.getMessage());
    }
  }

  @Test
  void invalidSession_whenTokenUnknown() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock.when(() -> SessionManager.getSession(TOKEN)).thenReturn(null);

      DoctorServiceImplementation service = new DoctorServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.getScheduleById(TOKEN, 7));
      assertEquals("INVALID_SESSION", ex.getMessage());
    }
  }
}
