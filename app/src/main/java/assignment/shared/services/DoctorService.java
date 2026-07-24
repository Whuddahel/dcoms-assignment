package assignment.shared.services;

import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DoctorService extends Remote {

  List<Appointment> getUpcomingAppointmentsByDoctorUserId(String token, int userId)
      throws RemoteException;

  List<Patient> getPatientsWithConsultations(String token, int doctorUserId) throws RemoteException;

  List<Appointment> getAppointmentsWithConsultationByDoctorAndPatient(
      String token, int doctorUserId, int patientId) throws RemoteException;

  Consultation getConsultationByAppointmentId(String token, int appointmentId)
      throws RemoteException;

  Schedule getScheduleById(String token, int scheduleId) throws RemoteException;

  Patient getPatientById(String token, int patientId) throws RemoteException;

  boolean cancelAppointment(String token, int appointmentId, int cancelledByUserId)
      throws RemoteException;
}
