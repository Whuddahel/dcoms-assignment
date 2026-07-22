package assignment.server.services;

import assignment.server.auth.AuthorizationManager;
import assignment.server.database.repository.AppointmentRepository;
import assignment.server.database.repository.ConsultationRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ScheduleRepository;
import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import assignment.shared.services.DoctorService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class DoctorServiceImplementation extends UnicastRemoteObject implements DoctorService {

  public DoctorServiceImplementation() throws RemoteException {
    super();
  }

  private int getDoctorId(int userId) throws RemoteException {
    try {
      int docId = DoctorRepository.getDoctorIdByUserId(userId);
      if (docId == -1) {
        throw new RemoteException("No doctor found for user ID " + userId);
      }
      return docId;
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve doctor ID", e);
    }
  }

  @Override
  public List<Appointment> getUpcomingAppointmentsByDoctorUserId(String token, int userId)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getUpcomingAppointmentsByDoctorUserId");
    try {
      int doctorId = getDoctorId(userId);
      return AppointmentRepository.getUpcomingAppointmentsByDoctorId(doctorId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve upcoming appointments", e);
    }
  }

  @Override
  public List<Patient> getPatientsWithConsultations(String token, int doctorUserId)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getPatientsWithConsultations");
    try {
      int doctorId = getDoctorId(doctorUserId);
      return ConsultationRepository.getPatientsWithConsultationsByDoctorId(doctorId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve patients with consultations", e);
    }
  }

  @Override
  public List<Appointment> getAppointmentsWithConsultationByDoctorAndPatient(
      String token, int doctorUserId, int patientId) throws RemoteException {
    AuthorizationManager.requirePermissions(
        token, "getAppointmentsWithConsultationByDoctorAndPatient");
    try {
      int doctorId = getDoctorId(doctorUserId);
      return AppointmentRepository.getAppointmentsByDoctorAndPatient(doctorId, patientId);
    } catch (SQLException e) {
      throw new RemoteException(
          "DB_ERROR: Failed to retrieve appointments for doctor and patient", e);
    }
  }

  @Override
  public Consultation getConsultationByAppointmentId(String token, int appointmentId)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getConsultationByAppointmentId");
    try {
      return ConsultationRepository.getConsultationByAppointmentId(appointmentId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve consultation", e);
    }
  }

  @Override
  public Schedule getScheduleById(String token, int scheduleId) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getScheduleById");
    try {
      return ScheduleRepository.getScheduleById(scheduleId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve schedule", e);
    }
  }

  @Override
  public Patient getPatientById(String token, int patientId) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getPatientById");
    try {
      return PatientRepository.getPatientById(patientId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve patient", e);
    }
  }

  @Override
  public boolean cancelAppointment(String token, int appointmentId, int cancelledByUserId)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "cancelAppointment");
    try {
      return AppointmentRepository.cancelAppointment(appointmentId, cancelledByUserId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to cancel appointment", e);
    }
  }
}
