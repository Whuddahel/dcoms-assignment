package assignment.server.services;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.repository.ReportRepository;
import assignment.shared.auth.Role;
import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import assignment.shared.model.User;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplementationTest {

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
  void getMonthlyAppointmentReport_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReportRepository> reportRepoMock = mockStatic(ReportRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.ADMIN));

      MonthlyAppointmentReport expected =
          new MonthlyAppointmentReport(10, 2, 1, 1, 8, Collections.emptyList());
      reportRepoMock
          .when(() -> ReportRepository.getMonthlyAppointmentReport(2026, 8))
          .thenReturn(expected);

      ReportServiceImplementation service = new ReportServiceImplementation();
      MonthlyAppointmentReport result = service.getMonthlyAppointmentReport(TOKEN, 2026, 8);

      assertEquals(expected, result);
    }
  }

  @Test
  void getMonthlyAppointmentReport_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReportRepository> reportRepoMock = mockStatic(ReportRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.ADMIN));
      reportRepoMock
          .when(() -> ReportRepository.getMonthlyAppointmentReport(2026, 8))
          .thenThrow(new SQLException());

      ReportServiceImplementation service = new ReportServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getMonthlyAppointmentReport(TOKEN, 2026, 8));
      assertTrue(ex.getMessage().contains("Database error in monthly appointment report"));
    }
  }

  @Test
  void getDoctorConsultationReport_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReportRepository> reportRepoMock = mockStatic(ReportRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.ADMIN));

      DoctorConsultationReport expected =
          new DoctorConsultationReport(5, BigDecimal.valueOf(250.0), Collections.emptyList());
      reportRepoMock
          .when(() -> ReportRepository.getDoctorConsultationReport(2026, 8))
          .thenReturn(expected);

      ReportServiceImplementation service = new ReportServiceImplementation();
      DoctorConsultationReport result = service.getDoctorConsultationReport(TOKEN, 2026, 8);

      assertEquals(expected, result);
    }
  }

  @Test
  void getDoctorConsultationReport_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReportRepository> reportRepoMock = mockStatic(ReportRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.ADMIN));
      reportRepoMock
          .when(() -> ReportRepository.getDoctorConsultationReport(2026, 8))
          .thenThrow(new SQLException());

      ReportServiceImplementation service = new ReportServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getDoctorConsultationReport(TOKEN, 2026, 8));
      assertTrue(ex.getMessage().contains("Database error in doctor consultation report"));
    }
  }

  @Test
  void getPatientVisitSummaryReport_success() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReportRepository> reportRepoMock = mockStatic(ReportRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.ADMIN));

      PatientVisitSummaryReport expected =
          new PatientVisitSummaryReport(3, Collections.emptyList());
      reportRepoMock
          .when(() -> ReportRepository.getPatientVisitSummaryReport(2026, 8))
          .thenReturn(expected);

      ReportServiceImplementation service = new ReportServiceImplementation();
      PatientVisitSummaryReport result = service.getPatientVisitSummaryReport(TOKEN, 2026, 8);

      assertEquals(expected, result);
    }
  }

  @Test
  void getPatientVisitSummaryReport_dbError() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class);
        MockedStatic<ReportRepository> reportRepoMock = mockStatic(ReportRepository.class)) {

      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.ADMIN));
      reportRepoMock
          .when(() -> ReportRepository.getPatientVisitSummaryReport(2026, 8))
          .thenThrow(new SQLException());

      ReportServiceImplementation service = new ReportServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getPatientVisitSummaryReport(TOKEN, 2026, 8));
      assertTrue(ex.getMessage().contains("Database error in patient visit summary report"));
    }
  }

  @Test
  void accessDenied_whenRoleLacksPermission() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      // DOCTOR does not have REPORT_READ
      sessionMock
          .when(() -> SessionManager.getSession(TOKEN))
          .thenReturn(sessionWithRole(Role.DOCTOR));

      ReportServiceImplementation service = new ReportServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getMonthlyAppointmentReport(TOKEN, 2026, 8));
      assertEquals("ACCESS_DENIED", ex.getMessage());
    }
  }

  @Test
  void invalidSession_whenTokenUnknown() throws Exception {
    try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
      sessionMock.when(() -> SessionManager.getSession(TOKEN)).thenReturn(null);

      ReportServiceImplementation service = new ReportServiceImplementation();

      RemoteException ex =
          assertThrows(
              RemoteException.class, () -> service.getDoctorConsultationReport(TOKEN, 2026, 8));
      assertEquals("INVALID_SESSION", ex.getMessage());
    }
  }
}
