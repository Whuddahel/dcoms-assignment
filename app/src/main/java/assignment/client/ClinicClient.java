package assignment.client;

import assignment.shared.config.Config;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.Users;
import assignment.shared.services.AuthService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * ClinicClient acts as the central client coordinator. It connects to the RMI server registry once
 * and pre-loads all shared services, delegating service calls to avoid initializing registries
 * repeatedly.
 */
public class ClinicClient {
  private final AuthService authService;
  private final RegisterUserService registerUserService;
  private final EditUserService editUserService;

  public ClinicClient() throws Exception {
    Registry registry = LocateRegistry.getRegistry(Config.SERVER_HOST, Config.SERVER_REGISTRY_PORT);
    this.authService = (AuthService) registry.lookup("AuthService");
    this.registerUserService = (RegisterUserService) registry.lookup("RegisterUser");
    this.editUserService = (EditUserService) registry.lookup("EditUser");
  }

  // ==========================================
  // AuthService Delegation
  // ==========================================
  public String login(String username, String password) throws Exception {
    return authService.login(username, password);
  }

  // ==========================================
  // RegisterUserService Delegation
  // ==========================================
  public boolean registerUser(Doctor doctor) throws Exception {
    return registerUserService.registerUser(doctor);
  }

  public boolean registerUser(Patient patient) throws Exception {
    return registerUserService.registerUser(patient);
  }

  public boolean registerUser(ClinicAdministrator admin) throws Exception {
    return registerUserService.registerUser(admin);
  }

  public boolean registerUser(Receptionist receptionist) throws Exception {
    return registerUserService.registerUser(receptionist);
  }

  // ==========================================
  // EditUserService Delegation
  // ==========================================
  public boolean editUser(Doctor doctor) throws Exception {
    return editUserService.editUser(doctor);
  }

  public boolean editUser(Patient patient) throws Exception {
    return editUserService.editUser(patient);
  }

  public boolean editUser(ClinicAdministrator admin) throws Exception {
    return editUserService.editUser(admin);
  }

  public boolean editUser(Receptionist receptionist) throws Exception {
    return editUserService.editUser(receptionist);
  }

  public List<Users> getAllUsers() throws Exception {
    return editUserService.getAllUsers();
  }
}
