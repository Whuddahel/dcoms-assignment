package assignment.server.services;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.repository.ClinicAdministratorRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ReceptionistRepository;
import assignment.server.database.repository.UserRepository;
import assignment.shared.auth.Role;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EditUserServiceImplementationTest {

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

  // ==========================================
  // editUser
  // ==========================================

  @Test
  void editUser_doctor_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Doctor doctor =
          new Doctor(
              1, 2, "John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP",
              new Timestamp(System.currentTimeMillis()), false);
      doctorRepoMock.when(() -> DoctorRepository.updateDoctor(doctor)).thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.editUser(TOKEN, doctor);

      assertTrue(result);
    }
  }

  @Test
  void editUser_patient_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Patient patient =
          new Patient(
              1, 2, "John", "Doe", "patient", "IC2", "john@test.com", "hash", 100, "0123456789",
              new Timestamp(System.currentTimeMillis()), false);
      patientRepoMock.when(() -> PatientRepository.updatePatient(patient)).thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.editUser(TOKEN, patient);

      assertTrue(result);
    }
  }

  @Test
  void editUser_admin_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ClinicAdministratorRepository> adminRepoMock =
            mockStatic(ClinicAdministratorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      ClinicAdministrator admin =
          new ClinicAdministrator(
              1, 2, "John", "Doe", "admin", "IC2", "john@test.com", "hash",
              new Timestamp(System.currentTimeMillis()), false);
      adminRepoMock
          .when(() -> ClinicAdministratorRepository.updateClinicAdministrator(admin))
          .thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.editUser(TOKEN, admin);

      assertTrue(result);
    }
  }

  @Test
  void editUser_receptionist_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReceptionistRepository> receptionistRepoMock =
            mockStatic(ReceptionistRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Receptionist receptionist =
          new Receptionist(
              1, 2, "John", "Doe", "receptionist", "IC2", "john@test.com", "hash",
              new Timestamp(System.currentTimeMillis()), false);
      receptionistRepoMock
          .when(() -> ReceptionistRepository.updateReceptionist(receptionist))
          .thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.editUser(TOKEN, receptionist);

      assertTrue(result);
    }
  }

  @Test
  void editUser_nullUser_returnsFalse() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.editUser(TOKEN, null);

      assertFalse(result);
    }
  }

  @Test
  void editUser_unknownRole_returnsFalse() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      // A plain User whose role string matches none of the switch cases
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

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.editUser(TOKEN, user);

      assertFalse(result);
    }
  }

  @Test
  void editUser_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Doctor doctor =
          new Doctor(
              1, 2, "John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP",
              new Timestamp(System.currentTimeMillis()), false);
      doctorRepoMock
          .when(() -> DoctorRepository.updateDoctor(doctor))
          .thenThrow(new SQLException());

      EditUserServiceImplementation service = new EditUserServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.editUser(TOKEN, doctor));
      assertTrue(ex.getMessage().contains("Database error occurred while editing user"));
    }
  }

  // ==========================================
  // deleteUser
  // ==========================================

  @Test
  void deleteUser_doctor_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Doctor doctor =
          new Doctor(
              5, 2, "John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP",
              new Timestamp(System.currentTimeMillis()), false);
      doctorRepoMock.when(() -> DoctorRepository.deleteDoctor(5)).thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.deleteUser(TOKEN, doctor);

      assertTrue(result);
    }
  }

  @Test
  void deleteUser_patient_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<PatientRepository> patientRepoMock = mockStatic(PatientRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Patient patient =
          new Patient(
              5, 2, "John", "Doe", "patient", "IC2", "john@test.com", "hash", 100, "0123456789",
              new Timestamp(System.currentTimeMillis()), false);
      patientRepoMock.when(() -> PatientRepository.deletePatient(5)).thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.deleteUser(TOKEN, patient);

      assertTrue(result);
    }
  }

  @Test
  void deleteUser_admin_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ClinicAdministratorRepository> adminRepoMock =
            mockStatic(ClinicAdministratorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      ClinicAdministrator admin =
          new ClinicAdministrator(
              5, 2, "John", "Doe", "admin", "IC2", "john@test.com", "hash",
              new Timestamp(System.currentTimeMillis()), false);
      adminRepoMock
          .when(() -> ClinicAdministratorRepository.deleteClinicAdministrator(5))
          .thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.deleteUser(TOKEN, admin);

      assertTrue(result);
    }
  }

  @Test
  void deleteUser_receptionist_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReceptionistRepository> receptionistRepoMock =
            mockStatic(ReceptionistRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Receptionist receptionist =
          new Receptionist(
              5, 2, "John", "Doe", "receptionist", "IC2", "john@test.com", "hash",
              new Timestamp(System.currentTimeMillis()), false);
      receptionistRepoMock
          .when(() -> ReceptionistRepository.deleteReceptionist(5))
          .thenReturn(true);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.deleteUser(TOKEN, receptionist);

      assertTrue(result);
    }
  }

  @Test
  void deleteUser_nullUser_returnsFalse() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      boolean result = service.deleteUser(TOKEN, null);

      assertFalse(result);
    }
  }

  @Test
  void deleteUser_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<DoctorRepository> doctorRepoMock = mockStatic(DoctorRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      Doctor doctor =
          new Doctor(
              5, 2, "John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP",
              new Timestamp(System.currentTimeMillis()), false);
      doctorRepoMock.when(() -> DoctorRepository.deleteDoctor(5)).thenThrow(new SQLException());

      EditUserServiceImplementation service = new EditUserServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.deleteUser(TOKEN, doctor));
      assertTrue(ex.getMessage().contains("Database error occurred while deleting user"));
    }
  }

  // ==========================================
  // getAllUsers
  // ==========================================

  @Test
  void getAllUsers_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<UserRepository> userRepoMock = mockStatic(UserRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));

      List<User> expected =
          List.of(
              new User(
                  1,
                  "John",
                  "Doe",
                  Role.PATIENT,
                  "IC2",
                  "john@test.com",
                  "hash",
                  new Timestamp(System.currentTimeMillis()),
                  false));
      userRepoMock.when(UserRepository::getAllUsersWithRoles).thenReturn(expected);

      EditUserServiceImplementation service = new EditUserServiceImplementation();
      List<User> result = service.getAllUsers(TOKEN);

      assertEquals(expected, result);
    }
  }

  @Test
  void getAllUsers_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<UserRepository> userRepoMock = mockStatic(UserRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.RECEPTIONIST));
      userRepoMock.when(UserRepository::getAllUsersWithRoles).thenThrow(new SQLException());

      EditUserServiceImplementation service = new EditUserServiceImplementation();

      RemoteException ex = assertThrows(RemoteException.class, () -> service.getAllUsers(TOKEN));
      assertTrue(ex.getMessage().contains("Database error in getAllUsers"));
    }
  }

  // ==========================================
  // Shared auth checks
  // ==========================================

  @Test
  void accessDenied_whenRoleLacksPermission() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      // DOCTOR only has USER_READ, not USER_UPDATE
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      Doctor doctor =
          new Doctor(
              1, 2, "John", "Doe", "doctor", "IC2", "john@test.com", "hash", "GP",
              new Timestamp(System.currentTimeMillis()), false);
      EditUserServiceImplementation service = new EditUserServiceImplementation();

      RemoteException ex =
          assertThrows(RemoteException.class, () -> service.editUser(TOKEN, doctor));
      assertEquals("ACCESS_DENIED", ex.getMessage());
    }
  }

  @Test
  void invalidSession_whenTokenUnknown() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock.when(() -> SessionManager.getSession(TOKEN)).thenReturn(null);

      EditUserServiceImplementation service = new EditUserServiceImplementation();

      RemoteException ex = assertThrows(RemoteException.class, () -> service.getAllUsers(TOKEN));
      assertEquals("INVALID_SESSION", ex.getMessage());
    }
  }
}
