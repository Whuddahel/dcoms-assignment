package assignment.server.services;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.repository.UserRepository;
import assignment.shared.dto.LoginResponse;
import assignment.shared.error.AuthError;
import assignment.shared.model.User;
import assignment.shared.services.AuthService;
import assignment.shared.ssl.LenientSslRMIClientSocketFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImplementation extends UnicastRemoteObject implements AuthService {

  public AuthServiceImplementation() throws RemoteException {
    super(
        0,
        new LenientSslRMIClientSocketFactory(),
        new SslRMIServerSocketFactory(null, null, false));
  }

  @Override
  public LoginResponse login(String email, String password) throws RemoteException {
    try {
      User user = UserRepository.getUserByEmail(email);
      if (user == null) throw new RemoteException(AuthError.INVALID_CREDENTIALS.name());
      String storedHash = user.getPasswordHash();
      if (!BCrypt.checkpw(password, storedHash)) {
        System.out.printf("\n[LOGIN FAILED] Invalid password for email: %s\n", email);
        throw new RemoteException(AuthError.INVALID_CREDENTIALS.name());
      }

      String token = SessionManager.createSession(user);
      System.out.printf(
          "\n[LOGIN SUCCESS] User: %s (%s) | Email: %s\n",
          user.getUsername(), user.getRole(), user.getEmail());

      SessionManager.printSessions();
      return new LoginResponse(
          token,
          user.getEmail(),
          user.getFirstName(),
          user.getLastName(),
          user.getUserId(),
          user.getRole(),
          user.getIcPassportNo());
    } catch (SQLException e) {
      throw new RemoteException(AuthError.DB_ERROR.name(), e);
    }
  }

  @Override
  public void logout(String token) throws RemoteException {
    Session session = SessionManager.getSession(token);
    if (session != null) {
      System.out.printf(
          "\n[LOGOUT] User: %s (%s) logged out.\n", session.getUsername(), session.getRole());
    } else {
      System.out.println("\n[LOGOUT] Session terminated.");
    }
    SessionManager.remove(token);
  }
}
