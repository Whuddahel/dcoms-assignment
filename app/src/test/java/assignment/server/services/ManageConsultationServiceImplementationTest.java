package assignment.server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.repository.ConsultationRepository;
import assignment.shared.auth.Role;
import assignment.shared.model.Consultation;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ManageConsultationServiceImplementationTest {

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
  void addConsultation_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Consultation consultation = new Consultation(30, "Patient presented with mild fever.", 50.0);
      consultRepoMock
          .when(() -> ConsultationRepository.addConsultation(consultation))
          .thenReturn(true);

      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();
      boolean result = service.addConsultation(TOKEN, consultation);

      assertTrue(result);
    }
  }

  @Test
  void addConsultation_duplicateForSameAppointment_surfacesAsGenericDbError() throws Exception {
    // Consultation.appointmentId is UNIQUE in the schema, so a second consultation for the
    // same appointment fails at the DB layer with a SQLException.
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Consultation consultation = new Consultation(30, "Follow-up notes.", 50.0);
      consultRepoMock
          .when(() -> ConsultationRepository.addConsultation(consultation))
          .thenThrow(new SQLException("duplicate key value violates unique constraint"));

      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.addConsultation(TOKEN, consultation));
      // NOTE: ConsultationScreen (client) checks the message for "DUPLICATE_ERROR" to show a
      // friendly "already recorded" hint, but the server only ever sends this generic DB_ERROR
      // text regardless of cause -- that client-side check can never actually match today.
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void getAllConsultations_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      List<Consultation> expected =
          List.of(
              new Consultation(1, 30, "Notes", 50.0, new Timestamp(System.currentTimeMillis())));
      consultRepoMock.when(ConsultationRepository::getAllConsultations).thenReturn(expected);

      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();
      List<Consultation> result = service.getAllConsultations(TOKEN);

      assertEquals(expected, result);
    }
  }

  @Test
  void getAllConsultations_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));
      consultRepoMock
          .when(ConsultationRepository::getAllConsultations)
          .thenThrow(new SQLException());

      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.getAllConsultations(TOKEN));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void updateConsultation_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Consultation updated =
          new Consultation(
              1, 30, "Revised notes.", 50.0, new Timestamp(System.currentTimeMillis()));
      consultRepoMock
          .when(() -> ConsultationRepository.updateConsultation(updated))
          .thenReturn(true);

      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();
      boolean result = service.updateConsultation(TOKEN, updated);

      assertTrue(result);
    }
  }

  @Test
  void updateConsultation_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ConsultationRepository> consultRepoMock =
            mockStatic(ConsultationRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Consultation updated =
          new Consultation(
              1, 30, "Revised notes.", 50.0, new Timestamp(System.currentTimeMillis()));
      consultRepoMock
          .when(() -> ConsultationRepository.updateConsultation(updated))
          .thenThrow(new SQLException());

      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.updateConsultation(TOKEN, updated));
      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void accessDenied_whenRoleLacksPermission() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      // PATIENT has CONSULTATION_READ but not CONSULTATION_CREATE
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Consultation consultation = new Consultation(30, "Notes", 50.0);
      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.addConsultation(TOKEN, consultation));
      assertEquals("ACCESS_DENIED", ex.getMessage());
    }
  }

  @Test
  void invalidSession_whenTokenUnknown() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock.when(() -> SessionManager.getSession(TOKEN)).thenReturn(null);

      ManageConsultationServiceImplementation service =
          new ManageConsultationServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.getAllConsultations(TOKEN));
      assertEquals("INVALID_SESSION", ex.getMessage());
    }
  }
}
