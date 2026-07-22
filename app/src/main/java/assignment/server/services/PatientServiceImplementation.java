package assignment.server.services;

import assignment.server.auth.AuthorizationManager;
import assignment.server.database.repository.AppointmentRepository;
import assignment.server.database.repository.ConsultationRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ScheduleRepository;
import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import assignment.shared.services.PatientService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Server-side implementation of {@link PatientService}. Delegates to existing repository classes.
 */
public class PatientServiceImplementation extends UnicastRemoteObject implements PatientService {

  public PatientServiceImplementation() throws RemoteException {
    super();
  }

  // ==========================================
  // Profile
  // ==========================================

  @Override
  public Patient getPatientByUserId(String token, int userId) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getPatientByUserId");
    try {
      return PatientRepository.getPatientByUserId(userId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve patient profile", e);
    }
  }

  @Override
  public boolean updatePatientProfile(String token, Patient patient) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "updatePatientProfile");
    try {
      return PatientRepository.updatePatient(patient);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to update patient profile", e);
    }
  }

  // ==========================================
  // Appointments
  // ==========================================

  @Override
  public List<Appointment> getUpcomingAppointments(String token, int userId)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getUpcomingAppointments");
    try {
      Patient patient = PatientRepository.getPatientByUserId(userId);
      if (patient == null) {
        return new ArrayList<>();
      }
      List<Appointment> all =
          AppointmentRepository.getAppointmentsByPatientId(patient.getPatientId());
      List<Appointment> upcoming = new ArrayList<>();
      Date today = new Date(System.currentTimeMillis());
      for (Appointment a : all) {
        // Upcoming = future date AND not cancelled
        if (!a.getAppointmentDate().before(today) && a.getcancelledByUserId() == null) {
          upcoming.add(a);
        }
      }
      return upcoming;
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve upcoming appointments", e);
    }
  }

  @Override
  public List<Appointment> getPastAppointments(String token, int userId) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getPastAppointments");
    try {
      Patient patient = PatientRepository.getPatientByUserId(userId);
      if (patient == null) {
        return new ArrayList<>();
      }
      List<Appointment> all =
          AppointmentRepository.getAppointmentsByPatientId(patient.getPatientId());
      List<Appointment> past = new ArrayList<>();
      Date today = new Date(System.currentTimeMillis());
      for (Appointment a : all) {
        // Past = before today, OR cancelled
        if (a.getAppointmentDate().before(today) || a.getcancelledByUserId() != null) {
          past.add(a);
        }
      }
      return past;
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve past appointments", e);
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

  @Override
  public boolean bookAppointment(String token, Appointment appointment) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "bookAppointment");
    try {
      return AppointmentRepository.addAppointment(appointment);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to book appointment", e);
    }
  }

  // ==========================================
  // Doctors & Scheduling
  // ==========================================

  @Override
  public List<Doctor> getAllDoctors(String token) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getAllDoctors");
    try {
      return DoctorRepository.getAllDoctors();
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve doctors", e);
    }
  }

  @Override
  public List<Schedule> getSchedulesByDoctorId(String token, int doctorId) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getSchedulesByDoctorId");
    try {
      return ScheduleRepository.getSchedulesByDoctorId(doctorId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve doctor schedules", e);
    }
  }

  @Override
  public List<Appointment> getAppointmentsByDoctorAndDate(String token, int doctorId, Date date)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getAppointmentsByDoctorAndDate");
    try {
      return AppointmentRepository.getAppointmentsByDoctorAndDate(doctorId, date);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve doctor appointments for date", e);
    }
  }

  // ==========================================
  // Consultations (read-only for patient)
  // ==========================================

  @Override
  public List<Consultation> getConsultationsByPatient(String token, int userId)
      throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getConsultationsByPatient");
    try {
      Patient patient = PatientRepository.getPatientByUserId(userId);
      if (patient == null) {
        return new ArrayList<>();
      }
      return ConsultationRepository.getConsultationsByPatientId(patient.getPatientId());
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve consultations", e);
    }
  }

  // ==========================================
  // Detail Lookups
  // ==========================================

  @Override
  public Doctor getDoctorById(String token, int doctorId) throws RemoteException {
    AuthorizationManager.requirePermissions(token, "getDoctorById");
    try {
      return DoctorRepository.getDoctorById(doctorId);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve doctor", e);
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
}
