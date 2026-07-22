package assignment.client.services;

import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.LoginResponse;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.Appointment;
import assignment.shared.model.Consultation;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import assignment.shared.services.AuthService;
import assignment.shared.services.DoctorService;
import assignment.shared.services.ManageConsultationService;
import assignment.shared.services.ManageScheduleService;
import assignment.shared.services.PatientService;
import assignment.shared.services.ReportService;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * ServiceManager acts as the central client coordinator. It connects to the RMI server registry
 * once and pre-loads all shared services, delegating service calls to avoid initializing registries
 * repeatedly.
 */
public class ServiceManager {
  private String activeToken;

  public void setToken(String token) {
    this.activeToken = token;
  }

  public void clearToken() {
    this.activeToken = null;
  }

  private final AuthService authService;
  private final RegisterUserService registerUserService;
  private final EditUserService editUserService;
  private final ManageScheduleService manageScheduleService;
  private final ManageConsultationService manageConsultationService;
  private final ReportService reportService;
  private final PatientService patientService;
  private final DoctorService doctorService;

  public ServiceManager() throws RemoteException {
    String serverHost = System.getenv("SERVER_HOST");
    if (serverHost == null || serverHost.isEmpty()) {
      throw new IllegalStateException("SERVER_HOST environment variable is not set.");
    }
    String portStr = System.getenv("SERVER_REGISTRY_PORT");
    if (portStr == null || portStr.isEmpty()) {
      throw new IllegalStateException("SERVER_REGISTRY_PORT environment variable is not set.");
    }
    int port;
    try {
      port = Integer.parseInt(portStr);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("SERVER_REGISTRY_PORT is not a valid integer: " + portStr, e);
    }
    try {
      Registry registry = LocateRegistry.getRegistry(serverHost, port);
      this.authService = (AuthService) registry.lookup("AuthService");
      this.registerUserService = (RegisterUserService) registry.lookup("RegisterUserService");
      this.editUserService = (EditUserService) registry.lookup("EditUserService");
      this.manageScheduleService = (ManageScheduleService) registry.lookup("ManageScheduleService");
      this.manageConsultationService =
          (ManageConsultationService) registry.lookup("ManageConsultationService");
      this.reportService = (ReportService) registry.lookup("ReportService");
      this.patientService = (PatientService) registry.lookup("PatientService");
      this.doctorService = (DoctorService) registry.lookup("DoctorService");
    } catch (java.rmi.NotBoundException e) {
      throw new RemoteException("A required service registry binding was not found.", e);
    }
  }

  // ==========================================
  // AuthService Delegation
  // ==========================================
  public LoginResponse login(String username, String password) throws RemoteException {
    return authService.login(username, password);
  }

  // ==========================================
  // ManageScheduleService Delegation
  // ==========================================
  public boolean addSchedule(Schedule schedule) throws RemoteException {
    return manageScheduleService.addSchedule(this.activeToken, schedule);
  }

  public List<Schedule> getSchedulesByDoctor(int userId) throws RemoteException {
    return manageScheduleService.getSchedulesByDoctor(this.activeToken, userId);
  }

  public boolean deleteSchedule(int scheduleId) throws RemoteException {
    return manageScheduleService.deleteSchedule(this.activeToken, scheduleId);
  }

  // ==========================================
  // ManageConsultationService Delegation
  // ==========================================
  public boolean addConsultation(Consultation consultation) throws RemoteException {
    return manageConsultationService.addConsultation(this.activeToken, consultation);
  }

  public List<Consultation> getAllConsultations() throws RemoteException {
    return manageConsultationService.getAllConsultations(this.activeToken);
  }

  public boolean updateConsultation(Consultation consultation) throws RemoteException {
    return manageConsultationService.updateConsultation(this.activeToken, consultation);
  }

  // ==========================================
  // RegisterUserService Delegation
  // ==========================================
  public boolean registerUser(User user) throws RemoteException {
    return registerUserService.registerUser(this.activeToken, user);
  }

  // ==========================================
  // EditUserService Delegation
  // ==========================================
  public boolean editUser(User user) throws RemoteException {
    return editUserService.editUser(this.activeToken, user);
  }

  public boolean deleteUser(User user) throws RemoteException {
    return editUserService.deleteUser(this.activeToken, user);
  }

  public List<User> getAllUsers() throws RemoteException {
    return editUserService.getAllUsers(this.activeToken);
  }

  // ==========================================
  // ReportService Delegation
  // ==========================================
  public MonthlyAppointmentReport getMonthlyAppointmentReport(int year, int month)
      throws RemoteException {
    return reportService.getMonthlyAppointmentReport(this.activeToken, year, month);
  }

  public DoctorConsultationReport getDoctorConsultationReport(int year, int month)
      throws RemoteException {
    return reportService.getDoctorConsultationReport(this.activeToken, year, month);
  }

  public PatientVisitSummaryReport getPatientVisitSummaryReport(int year, int month)
      throws RemoteException {
    return reportService.getPatientVisitSummaryReport(this.activeToken, year, month);
  }

  // ==========================================
  // PatientService Delegation
  // ==========================================
  public Patient getPatientByUserId(int userId) throws RemoteException {
    return patientService.getPatientByUserId(this.activeToken, userId);
  }

  public boolean updatePatientProfile(Patient patient) throws RemoteException {
    return patientService.updatePatientProfile(this.activeToken, patient);
  }

  public List<Appointment> getUpcomingAppointments(int userId) throws RemoteException {
    return patientService.getUpcomingAppointments(this.activeToken, userId);
  }

  public List<Appointment> getPastAppointments(int userId) throws RemoteException {
    return patientService.getPastAppointments(this.activeToken, userId);
  }

  public boolean cancelAppointment(int appointmentId, int cancelledByUserId)
      throws RemoteException {
    return patientService.cancelAppointment(this.activeToken, appointmentId, cancelledByUserId);
  }

  public boolean bookAppointment(Appointment appointment) throws RemoteException {
    return patientService.bookAppointment(this.activeToken, appointment);
  }

  public List<Doctor> getAllDoctorsForPatient() throws RemoteException {
    return patientService.getAllDoctors(this.activeToken);
  }

  public List<Schedule> getSchedulesByDoctorIdForPatient(int doctorId) throws RemoteException {
    return patientService.getSchedulesByDoctorId(this.activeToken, doctorId);
  }

  public List<Appointment> getAppointmentsByDoctorAndDate(int doctorId, java.sql.Date date)
      throws RemoteException {
    return patientService.getAppointmentsByDoctorAndDate(this.activeToken, doctorId, date);
  }

  public List<Consultation> getConsultationsByPatient(int userId) throws RemoteException {
    return patientService.getConsultationsByPatient(this.activeToken, userId);
  }

  public Doctor getDoctorByIdForPatient(int doctorId) throws RemoteException {
    return patientService.getDoctorById(this.activeToken, doctorId);
  }

  public Schedule getScheduleByIdForPatient(int scheduleId) throws RemoteException {
    return patientService.getScheduleById(this.activeToken, scheduleId);
  }

  // ==========================================
  // DoctorService Delegation
  // ==========================================
  public List<Appointment> getUpcomingAppointmentsByDoctorUserId(int userId)
      throws RemoteException {
    return doctorService.getUpcomingAppointmentsByDoctorUserId(this.activeToken, userId);
  }

  public List<Patient> getPatientsWithConsultations(int doctorUserId) throws RemoteException {
    return doctorService.getPatientsWithConsultations(this.activeToken, doctorUserId);
  }

  public List<Appointment> getAppointmentsWithConsultationByDoctorAndPatient(
      int doctorUserId, int patientId) throws RemoteException {
    return doctorService.getAppointmentsWithConsultationByDoctorAndPatient(
        this.activeToken, doctorUserId, patientId);
  }

  public Consultation getConsultationByAppointmentId(int appointmentId) throws RemoteException {
    return doctorService.getConsultationByAppointmentId(this.activeToken, appointmentId);
  }

  public Schedule getScheduleByIdForDoctor(int scheduleId) throws RemoteException {
    return doctorService.getScheduleById(this.activeToken, scheduleId);
  }

  public Patient getPatientByIdForDoctor(int patientId) throws RemoteException {
    return doctorService.getPatientById(this.activeToken, patientId);
  }

  public boolean cancelAppointmentForDoctor(int appointmentId, int cancelledByUserId)
      throws RemoteException {
    return doctorService.cancelAppointment(this.activeToken, appointmentId, cancelledByUserId);
  }
}
