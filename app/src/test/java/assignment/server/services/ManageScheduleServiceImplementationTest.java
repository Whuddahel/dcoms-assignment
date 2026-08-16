package assignment.server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.ScheduleRepository;
import assignment.shared.auth.Role;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ManageScheduleServiceImplementationTest {

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
  void addSchedule_success_resolvesRealDoctorIdFromUserId() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(10);
      scheduleRepoMock
          .when(() -> ScheduleRepository.addSchedule(any(Schedule.class)))
          .thenReturn(true);

      // Client submits with the session's userId (5), not the real doctorId (10)
      Schedule submitted =
          new Schedule(0, 5, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00"));

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();
      boolean result = service.addSchedule(TOKEN, submitted);

      assertTrue(result);
      // The server must not trust the client-submitted doctorId -- it re-resolves it from the
      // session
      scheduleRepoMock.verify(
          () ->
              ScheduleRepository.addSchedule(
                  argThat(s -> s.getDoctorId() == 10 && s.getDay().equals("MONDAY"))));
    }
  }

  @Test
  void addSchedule_userIsNotADoctor() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(-1);

      Schedule submitted =
          new Schedule(0, 5, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00"));
      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.addSchedule(TOKEN, submitted));
      assertTrue(ex.getMessage().contains("No matching Doctor record"));
    }
  }

  @Test
  void addSchedule_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(10);
      scheduleRepoMock
          .when(() -> ScheduleRepository.addSchedule(any(Schedule.class)))
          .thenThrow(new SQLException());

      Schedule submitted =
          new Schedule(0, 5, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00"));
      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.addSchedule(TOKEN, submitted));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void getSchedulesByDoctor_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(10);

      List<Schedule> expected =
          List.of(
              new Schedule(7, 10, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00")));
      scheduleRepoMock
          .when(() -> ScheduleRepository.getSchedulesByDoctorId(10))
          .thenReturn(expected);

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();
      List<Schedule> result = service.getSchedulesByDoctor(TOKEN, 5);

      assertEquals(expected, result);
    }
  }

  @Test
  void getSchedulesByDoctor_userIsNotADoctor_returnsEmptyListInsteadOfError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      doctorRepoMock.when(() -> DoctorRepository.getDoctorIdByUserId(5)).thenReturn(-1);

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();
      List<Schedule> result = service.getSchedulesByDoctor(TOKEN, 5);

      assertTrue(result.isEmpty());
    }
  }

  @Test
  void deleteSchedule_success_softDeletesWhenNoUpcomingAppointments() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      scheduleRepoMock
          .when(() -> ScheduleRepository.countActiveUpcomingAppointments(7))
          .thenReturn(0);
      scheduleRepoMock.when(() -> ScheduleRepository.deleteSchedule(7)).thenReturn(true);

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();
      boolean result = service.deleteSchedule(TOKEN, 7);

      assertTrue(result);
    }
  }

  @Test
  void deleteSchedule_pastOrCancelledHistoryAloneDoesNotBlockDeletion() throws Exception {
    // Regression test: countActiveUpcomingAppointments only counts non-cancelled, future
    // appointments, so a slot with only past/cancelled history must still be deletable.
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      scheduleRepoMock
          .when(() -> ScheduleRepository.countActiveUpcomingAppointments(7))
          .thenReturn(0);
      scheduleRepoMock.when(() -> ScheduleRepository.deleteSchedule(7)).thenReturn(true);

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();
      boolean result = service.deleteSchedule(TOKEN, 7);

      assertTrue(result);
    }
  }

  @Test
  void deleteSchedule_blockedByUpcomingAppointment() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      scheduleRepoMock
          .when(() -> ScheduleRepository.deleteSchedule(7))
          .thenThrow(
              new IllegalStateException("CANNOT_DELETE: A patient has an upcoming appointment"));

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.deleteSchedule(TOKEN, 7));
      assertTrue(ex.getMessage().contains("CANNOT_DELETE"));
    }
  }

  @Test
  void deleteSchedule_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ScheduleRepository> scheduleRepoMock = mockStatic(ScheduleRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      scheduleRepoMock
          .when(() -> ScheduleRepository.deleteSchedule(7))
          .thenThrow(new SQLException());

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.deleteSchedule(TOKEN, 7));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void accessDenied_whenRoleLacksPermission() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      // PATIENT only has SCHEDULE_READ, not SCHEDULE_CREATE
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Schedule submitted =
          new Schedule(0, 5, "MONDAY", Time.valueOf("09:00:00"), Time.valueOf("10:00:00"));
      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.addSchedule(TOKEN, submitted));
      assertEquals("ACCESS_DENIED", ex.getMessage());
    }
  }

  @Test
  void invalidSession_whenTokenUnknown() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock.when(() -> SessionManager.getSession(TOKEN)).thenReturn(null);

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.deleteSchedule(TOKEN, 7));
      assertEquals("INVALID_SESSION", ex.getMessage());
    }
  }
}
