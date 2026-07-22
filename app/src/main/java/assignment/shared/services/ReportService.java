package assignment.shared.services;

import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReportService extends Remote {
  MonthlyAppointmentReport getMonthlyAppointmentReport(String token, int year, int month)
      throws RemoteException;

  DoctorConsultationReport getDoctorConsultationReport(String token, int year, int month)
      throws RemoteException;

  PatientVisitSummaryReport getPatientVisitSummaryReport(String token, int year, int month)
      throws RemoteException;
}
