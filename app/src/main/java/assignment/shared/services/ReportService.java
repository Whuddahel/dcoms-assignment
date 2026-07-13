package assignment.shared.services;

import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReportService extends Remote {
  MonthlyAppointmentReport getMonthlyAppointmentReport(int year, int month) throws RemoteException;

  DoctorConsultationReport getDoctorConsultationReport(int year, int month) throws RemoteException;

  PatientVisitSummaryReport getPatientVisitSummaryReport(int year, int month)
      throws RemoteException;
}
