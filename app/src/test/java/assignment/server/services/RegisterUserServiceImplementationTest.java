package assignment.server.services;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.repository.ClinicAdministratorRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ReceptionistRepository;
import assignment.shared.auth.Role;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterUserServiceImplementationTest {

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
  void registerUser_doctor_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Doctor doctor =
          new Doctor("John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP");
      doctorRepoMock.when(() -> DoctorRepository.addDoctor(doctor)).thenReturn(true);

      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();
      boolean result = service.registerUser(TOKEN, doctor);

      assertTrue(result);
    }
  }

  @Test
  void registerUser_patient_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Patient patient =
          new Patient(
              "John", "Doe", "patient", "IC2", "john@test.com", "hash", 100, "0123456789");
      patientRepoMock.when(() -> PatientRepository.addPatient(patient)).thenReturn(true);

      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();
      boolean result = service.registerUser(TOKEN, patient);

      assertTrue(result);
    }
  }

  @Test
  void registerUser_admin_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ClinicAdministratorRepository> adminRepoMock =
            mockStatic(ClinicAdministratorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      ClinicAdministrator admin =
          new ClinicAdministrator("John", "Doe", "admin", "IC2", "john@test.com", "hash");
      adminRepoMock
          .when(() -> ClinicAdministratorRepository.addClinicAdministrator(admin))
          .thenReturn(true);

      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();
      boolean result = service.registerUser(TOKEN, admin);

      assertTrue(result);
    }
  }

  @Test
  void registerUser_receptionist_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReceptionistRepository> receptionistRepoMock =
            mockStatic(ReceptionistRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Receptionist receptionist =
          new Receptionist("John", "Doe", "receptionist", "IC2", "john@test.com", "hash");
      receptionistRepoMock
          .when(() -> ReceptionistRepository.addReceptionist(receptionist))
          .thenReturn(true);

      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();
      boolean result = service.registerUser(TOKEN, receptionist);

      assertTrue(result);
    }
  }

  @Test
  void registerUser_nullUser_returnsFalse() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();
      boolean result = service.registerUser(TOKEN, null);

      assertFalse(result);
    }
  }

  @Test
  void registerUser_unknownRole_returnsFalse() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      User user =
          new User(
              1,
              "John",
              "Doe",
              null,
              "IC2",
              "john@test.com",
              "hash",
              new Timestamp(System.currentTimeMillis()),
              false) {
            @Override
            public String getUserRole() {
              return "unknown";
            }
          };

      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();
      boolean result = service.registerUser(TOKEN, user);

      assertFalse(result);
    }
  }

  @Test
  void registerUser_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Doctor doctor =
          new Doctor("John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP");
      doctorRepoMock
          .when(() -> DoctorRepository.addDoctor(doctor))
          .thenThrow(new SQLException());

      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.registerUser(TOKEN, doctor));
      assertTrue(ex.getMessage().contains("Database error occurred while registering user"));
    }
  }

  @Test
  void accessDenied_whenRoleLacksPermission() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      // PATIENT does not have USER_CREATE
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.PATIENT));

      Doctor doctor =
          new Doctor("John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP");
      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.registerUser(TOKEN, doctor));
      assertEquals("ACCESS_DENIED", ex.getMessage());
    }
  }

  @Test
  void invalidSession_whenTokenUnknown() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock.when(() -> SessionManager.getSession(TOKEN)).thenReturn(null);

      Doctor doctor =
          new Doctor("John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP");
      RegisterUserServiceImplementation service = new RegisterUserServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.registerUser(TOKEN, doctor));
      assertEquals("INVALID_SESSION", ex.getMessage());
    }
  }
}
