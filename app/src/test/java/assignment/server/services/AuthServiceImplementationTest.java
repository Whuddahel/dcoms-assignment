package assignment.server.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import assignment.server.auth.SessionManager;
import assignment.server.database.repository.UserRepository;
import assignment.shared.auth.Role;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.MockedStatic;

class AuthServiceImplementationTest {

  @Test
  void login_success() throws Exception {

    User user =
        new User(
            1,
            "John",
            "Doe",
            Role.ADMIN,
            "S1234567A",
            "john@test.com",
            BCrypt.hashpw("password123", BCrypt.gensalt()),
            new Timestamp(System.currentTimeMillis()),
            false);

    try (MockedStatic<UserRepository> repoMock = mockStatic(UserRepository.class);
        MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {

      repoMock.when(() -> UserRepository.getUserByEmail("john@test.com")).thenReturn(user);

      sessionMock.when(() -> SessionManager.createSession(user)).thenReturn("token123");

      AuthServiceImplementation service = new AuthServiceImplementation();

      LoginResponse response = service.login("john@test.com", "password123");

      assertNotNull(response);
      assertEquals("token123", response.getToken());
      assertEquals("john@test.com", response.getEmail());
      assertEquals(1, response.getUserId());

      sessionMock.verify(() -> SessionManager.createSession(user));
    }
  }

  @Test
  void login_userNotFound() throws Exception {

    try (MockedStatic<UserRepository> repoMock = mockStatic(UserRepository.class)) {

      repoMock.when(() -> UserRepository.getUserByEmail("john@test.com")).thenReturn(null);

      AuthServiceImplementation service = new AuthServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.login("john@test.com", "password123"));

      assertEquals("INVALID_CREDENTIALS", ex.getMessage());
    }
  }

  @Test
  void login_wrongPassword() throws Exception {

    User user =
        new User(
            1,
            "John",
            "Doe",
            Role.ADMIN,
            "S1234567A",
            "john@test.com",
            BCrypt.hashpw("correctPassword", BCrypt.gensalt()),
            new Timestamp(System.currentTimeMillis()),
            false);

    try (MockedStatic<UserRepository> repoMock = mockStatic(UserRepository.class)) {

      repoMock.when(() -> UserRepository.getUserByEmail("john@test.com")).thenReturn(user);

      AuthServiceImplementation service = new AuthServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.login("john@test.com", "wrongPassword"));

      assertEquals("INVALID_CREDENTIALS", ex.getMessage());
    }
  }

  @Test
  void login_databaseError() throws Exception {

    try (MockedStatic<UserRepository> repoMock = mockStatic(UserRepository.class)) {

      repoMock.when(() -> UserRepository.getUserByEmail(anyString())).thenThrow(new SQLException());

      AuthServiceImplementation service = new AuthServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.login("john@test.com", "password"));

      assertTrue(ex.getMessage().contains("DB_ERROR"));
    }
  }

  @Test
  void logout() throws Exception {

    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {

      AuthServiceImplementation service = new AuthServiceImplementation();

      service.logout("token123");

      sessionMock.verify(() -> SessionManager.remove("token123"));
    }
  }
}
