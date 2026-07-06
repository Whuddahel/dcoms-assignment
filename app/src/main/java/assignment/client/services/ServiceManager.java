package assignment.client.services;

import assignment.shared.config.Config;
import assignment.shared.dto.LoginResponse;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.Consultation;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import assignment.shared.services.AuthService;
import assignment.shared.services.ManageScheduleService;
import assignment.shared.services.ManageConsultationService;
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

  public ServiceManager() throws Exception {
    Registry registry = LocateRegistry.getRegistry(Config.SERVER_HOST, Config.SERVER_REGISTRY_PORT);
    this.authService = (AuthService) registry.lookup("AuthService");
    this.registerUserService = (RegisterUserService) registry.lookup("RegisterUserService");
    this.editUserService = (EditUserService) registry.lookup("EditUserService");
    this.manageScheduleService = (ManageScheduleService) registry.lookup("ManageScheduleService");
    this.manageConsultationService = (ManageConsultationService) registry.lookup("ManageConsultationService");
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
  // ManageConsultationService Delegation
  // ==========================================
  public boolean addConsultation(Consultation consultation) throws Exception
  {
    return manageConsultationService.addConsultation(consultation);
  }

  public List<Consultation> getAllConsultations() throws Exception
  {
    return manageConsultationService.getAllConsultations();
  }

  public boolean updateConsultation(Consultation consultation) throws Exception
  {
    return manageConsultationService.updateConsultation(consultation);
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

  public List<User> getAllUsers() throws Exception {
    return editUserService.getAllUsers();
  }
}
