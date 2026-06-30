package assignment.server.auth;

import assignment.shared.auth.Permission;
import assignment.shared.error.AuthError;
import java.rmi.RemoteException;

public class AuthorizationManager {
  private AuthorizationManager() {}

  public static Session requirePermissions(String token, Permission permission)
      throws RemoteException {
    Session session = SessionManager.getSession(token);

    if (session == null) throw new RemoteException(AuthError.INVALID_SESSION.name());

    if (!session.getRole().hasPermission(permission))
      throw new RemoteException(AuthError.ACCESS_DENIED.name());

    return session;
  }
}
