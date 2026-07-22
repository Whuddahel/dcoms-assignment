package assignment.server.services;

import assignment.server.auth.AuthorizationManager;
import assignment.server.database.repository.ReportRepository;
import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import assignment.shared.services.ReportService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class ReportServiceImplementation extends UnicastRemoteObject implements ReportService {

  public ReportServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public MonthlyAppointmentReport getMonthlyAppointmentReport(String token, int year, int month)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getMonthlyAppointmentReport");
    try {
      return ReportRepository.getMonthlyAppointmentReport(year, month);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error in monthly appointment report", e);
    }
  }

  @Override
  public DoctorConsultationReport getDoctorConsultationReport(String token, int year, int month)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getDoctorConsultationReport");
    try {
      return ReportRepository.getDoctorConsultationReport(year, month);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error in doctor consultation report", e);
    }
  }

  @Override
  public PatientVisitSummaryReport getPatientVisitSummaryReport(String token, int year, int month)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getPatientVisitSummaryReport");
    try {
      return ReportRepository.getPatientVisitSummaryReport(year, month);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error in patient visit summary report", e);
    }
  }
}
