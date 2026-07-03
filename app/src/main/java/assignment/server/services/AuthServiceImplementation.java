package assignment.server.services;

import assignment.server.auth.SessionManager;
import assignment.server.database.repository.UserRepository;
import assignment.shared.error.AuthError;
import assignment.shared.model.User;
import assignment.shared.services.AuthService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImplementation extends UnicastRemoteObject implements AuthService {

  public AuthServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public String login(String email, String password) throws RemoteException {
    try {
      User user = UserRepository.getUserByEmail(email);
      if (user == null) throw new RemoteException(AuthError.INVALID_CREDENTIALS.name());

      String storedHash = user.getPasswordHash();
      if (!BCrypt.checkpw(password, storedHash)) {
        throw new RuntimeException(AuthError.INVALID_CREDENTIALS.name());
      }

      String token = SessionManager.createSession(user);

      // TODO: Remove debug statement
      SessionManager.printSessions();
      return token;
    } catch (SQLException e) {
      throw new RemoteException(AuthError.DB_ERROR.name(), e);
    }
  }

  @Override
  public void logout(String token) throws RemoteException {
    SessionManager.remove(token);
  }
}
