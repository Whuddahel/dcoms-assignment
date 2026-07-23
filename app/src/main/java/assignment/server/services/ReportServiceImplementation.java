package assignment.server.services;

import assignment.server.database.repository.ReportRepository;
import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import assignment.shared.services.ReportService;
import assignment.shared.ssl.LenientSslRMIClientSocketFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class ReportServiceImplementation extends UnicastRemoteObject implements ReportService {

  public ReportServiceImplementation() throws RemoteException {
    super(
        0,
        new LenientSslRMIClientSocketFactory(),
        new SslRMIServerSocketFactory(null, null, false));
  }

  @Override
  public MonthlyAppointmentReport getMonthlyAppointmentReport(int year, int month)
      throws RemoteException {
    try {
      return ReportRepository.getMonthlyAppointmentReport(year, month);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error in monthly appointment report", e);
    }
  }

  @Override
  public DoctorConsultationReport getDoctorConsultationReport(int year, int month)
      throws RemoteException {
    try {
      return ReportRepository.getDoctorConsultationReport(year, month);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error in doctor consultation report", e);
    }
  }

  @Override
  public PatientVisitSummaryReport getPatientVisitSummaryReport(int year, int month)
      throws RemoteException {
    try {
      return ReportRepository.getPatientVisitSummaryReport(year, month);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error in patient visit summary report", e);
    }
  }
}
