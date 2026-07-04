package assignment.client.services;

import assignment.shared.config.Config;
import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.LoginResponse;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import assignment.shared.services.AuthService;
import assignment.shared.services.ManageScheduleService;
import assignment.shared.services.ReportService;
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
  private final ReportService reportService;

  public ServiceManager() throws Exception {
    Registry registry = LocateRegistry.getRegistry(Config.SERVER_HOST, Config.SERVER_REGISTRY_PORT);
    this.authService = (AuthService) registry.lookup("AuthService");
    this.registerUserService = (RegisterUserService) registry.lookup("RegisterUserService");
    this.editUserService = (EditUserService) registry.lookup("EditUserService");
    this.manageScheduleService = (ManageScheduleService) registry.lookup("ManageScheduleService");
    this.reportService = (ReportService) registry.lookup("ReportService");
  }

  // ==========================================
  // AuthService Delegation
  // ==========================================
  public LoginResponse login(String username, String password) throws Exception {
    return authService.login(username, password);
  }

  // ==========================================
  // ManageScheduleService Delegation
  // ==========================================
  public boolean addSchedule(Schedule schedule) throws Exception {
    return manageScheduleService.addSchedule(schedule);
  }

  public List<Schedule> getSchedulesByDoctor(int userId) throws Exception {
    return manageScheduleService.getSchedulesByDoctor(userId);
  }

  public boolean deleteSchedule(int scheduleId) throws Exception {
    return manageScheduleService.deleteSchedule(scheduleId);
  }

  // ==========================================
  // RegisterUserService Delegation
  // ==========================================
  public boolean registerUser(User user) throws Exception {
    return registerUserService.registerUser(user);
  }

  // ==========================================
  // EditUserService Delegation
  // ==========================================
  public boolean editUser(User user) throws Exception {
    return editUserService.editUser(user);
  }

  public boolean deleteUser(User user) throws Exception {
    return editUserService.deleteUser(user);
  }

  public List<User> getAllUsers() throws Exception {
    return editUserService.getAllUsers();
  }

  // ==========================================
  // ReportService Delegation
  // ==========================================
  public MonthlyAppointmentReport getMonthlyAppointmentReport(int year, int month)
      throws Exception {
    return reportService.getMonthlyAppointmentReport(year, month);
  }

  public DoctorConsultationReport getDoctorConsultationReport(int year, int month)
      throws Exception {
    return reportService.getDoctorConsultationReport(year, month);
  }

  public PatientVisitSummaryReport getPatientVisitSummaryReport(int year, int month)
      throws Exception {
    return reportService.getPatientVisitSummaryReport(year, month);
  }
}
