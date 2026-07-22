package assignment.shared.services;

import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * PatientService exposes all server-side operations needed by the Patient role screens. Follows the
 * same RMI interface pattern as {@link ManageScheduleService} and {@link
 * ManageConsultationService}.
 */
public interface PatientService extends Remote {

  // Profile
  Patient getPatientByUserId(String token, int userId) throws RemoteException;

  boolean updatePatientProfile(String token, Patient patient) throws RemoteException;

  // Appointments
  List<Appointment> getUpcomingAppointments(String token, int userId) throws RemoteException;

  List<Appointment> getPastAppointments(String token, int userId) throws RemoteException;

  boolean cancelAppointment(String token, int appointmentId, int cancelledByUserId)
      throws RemoteException;

  boolean bookAppointment(String token, Appointment appointment) throws RemoteException;

  // Doctors & scheduling
  List<Doctor> getAllDoctors(String token) throws RemoteException;

  List<Schedule> getSchedulesByDoctorId(String token, int doctorId) throws RemoteException;

  List<Appointment> getAppointmentsByDoctorAndDate(String token, int doctorId, java.sql.Date date)
      throws RemoteException;

  // Consultation notes (read-only for patient)
  List<Consultation> getConsultationsByPatient(String token, int userId) throws RemoteException;

  // Detail lookups
  Doctor getDoctorById(String token, int doctorId) throws RemoteException;

  Schedule getScheduleById(String token, int scheduleId) throws RemoteException;
}
