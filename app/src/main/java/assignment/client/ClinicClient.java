package assignment.client;

import assignment.shared.config.Config;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.interfaces.RegisterUserService;
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
  public boolean registerUser(Users user) throws Exception {
    return registerUserService.registerUser(user);
  }

  // ==========================================
  // EditUserService Delegation
  // ==========================================
  public boolean editUser(Users user) throws Exception {
    return editUserService.editUser(user);
  }

  public List<Users> getAllUsers() throws Exception {
    return editUserService.getAllUsers();
  }
}
