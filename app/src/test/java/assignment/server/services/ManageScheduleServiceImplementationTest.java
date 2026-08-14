package assignment.server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assignment.server.auth.AuthorizationManager;
import assignment.server.auth.Session;
import assignment.server.database.repository.ScheduleRepository;
import java.rmi.RemoteException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ManageScheduleServiceImplementationTest {

  @Test
  void deleteSchedule_success() throws Exception {
    try (MockedStatic<AuthorizationManager> authMock = mockStatic(AuthorizationManager.class);
        MockedStatic<ScheduleRepository> repoMock = mockStatic(ScheduleRepository.class)) {

      authMock
          .when(() -> AuthorizationManager.requirePermissions("token123", "deleteSchedule"))
          .thenReturn(mock(Session.class));
      repoMock.when(() -> ScheduleRepository.deleteSchedule(1)).thenReturn(true);

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();
      assertTrue(service.deleteSchedule("token123", 1));
    }
  }

  @Test
  void deleteSchedule_blockedByActiveUpcomingAppointments() throws Exception {
    try (MockedStatic<AuthorizationManager> authMock = mockStatic(AuthorizationManager.class);
        MockedStatic<ScheduleRepository> repoMock = mockStatic(ScheduleRepository.class)) {

      authMock
          .when(() -> AuthorizationManager.requirePermissions("token123", "deleteSchedule"))
          .thenReturn(mock(Session.class));
      repoMock
          .when(() -> ScheduleRepository.deleteSchedule(1))
          .thenThrow(
              new IllegalStateException(
                  "CANNOT_DELETE: A patient has an upcoming appointment booked in this time slot."));

      ManageScheduleServiceImplementation service = new ManageScheduleServiceImplementation();
      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.deleteSchedule("token123", 1));
      assertTrue(ex.getMessage().contains("CANNOT_DELETE"));
    }
  }
}
