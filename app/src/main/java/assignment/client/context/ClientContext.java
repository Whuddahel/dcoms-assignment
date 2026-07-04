package assignment.client.context;

import assignment.client.services.ServiceManager;
import assignment.shared.dto.LoginResponse;

public class ClientContext {

  private LoginResponse session;
  private ServiceManager services;

  public ClientContext(ServiceManager services) {
    this.services = services;
  }

  public void setSession(LoginResponse session) {
    this.session = session;
  }

  public LoginResponse getSession() {
    return session;
  }

  public ServiceManager getServices() {
    return services;
  }

  public void clearSession() {
    this.session = null;
  }

  public boolean isLoggedIn() {
    return session != null;
  }
}
