package assignment.client.services;

import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.LoginResponse;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.Consultation;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import assignment.shared.services.AuthService;
import assignment.shared.services.ManageConsultationService;
import assignment.shared.services.ManageScheduleService;
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
  private final AuthService authService;
  private final RegisterUserService registerUserService;
  private final EditUserService editUserService;
  private final ManageScheduleService manageScheduleService;
  private final ManageConsultationService manageConsultationService;
  private final ReportService reportService;

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
    return manageScheduleService.addSchedule(schedule);
  }

  public List<Schedule> getSchedulesByDoctor(int userId) throws RemoteException {
    return manageScheduleService.getSchedulesByDoctor(userId);
  }

  public boolean deleteSchedule(int scheduleId) throws RemoteException {
    return manageScheduleService.deleteSchedule(scheduleId);
  }

  // ==========================================
  // ManageConsultationService Delegation
  // ==========================================
  public boolean addConsultation(Consultation consultation) throws RemoteException {
    return manageConsultationService.addConsultation(consultation);
  }

  public List<Consultation> getAllConsultations() throws RemoteException {
    return manageConsultationService.getAllConsultations();
  }

  public boolean updateConsultation(Consultation consultation) throws RemoteException {
    return manageConsultationService.updateConsultation(consultation);
  }

  // ==========================================
  // RegisterUserService Delegation
  // ==========================================
  public boolean registerUser(User user) throws RemoteException {
    return registerUserService.registerUser(user);
  }

  // ==========================================
  // EditUserService Delegation
  // ==========================================
  public boolean editUser(User user) throws RemoteException {
    return editUserService.editUser(user);
  }

  public boolean deleteUser(User user) throws RemoteException {
    return editUserService.deleteUser(user);
  }

  public List<User> getAllUsers() throws RemoteException {
    return editUserService.getAllUsers();
  }

  // ==========================================
  // ReportService Delegation
  // ==========================================
  public MonthlyAppointmentReport getMonthlyAppointmentReport(int year, int month)
      throws RemoteException {
    return reportService.getMonthlyAppointmentReport(year, month);
  }

  public DoctorConsultationReport getDoctorConsultationReport(int year, int month)
      throws RemoteException {
    return reportService.getDoctorConsultationReport(year, month);
  }

  public PatientVisitSummaryReport getPatientVisitSummaryReport(int year, int month)
      throws RemoteException {
    return reportService.getPatientVisitSummaryReport(year, month);
  }
}
