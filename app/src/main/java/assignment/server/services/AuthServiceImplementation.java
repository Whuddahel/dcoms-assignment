package assignment.server.services;

import assignment.server.auth.Session;
import assignment.server.auth.SessionManager;
import assignment.server.database.UserRepository;
import assignment.shared.dto.LoginResponse;
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
  public LoginResponse login(String email, String password) throws RemoteException {
    try {
      User user = UserRepository.getUserByEmail(email);
      if (user == null) throw new RemoteException(AuthError.INVALID_CREDENTIALS.name());
      System.out.println(user.getPasswordHash());
      System.out.println(user.getUsername());
      System.out.println(user.getRole());
      System.out.println(email);
      System.out.println(password);

      String storedHash = user.getPasswordHash();
      if (!BCrypt.checkpw(password, storedHash)) {
        throw new RuntimeException(AuthError.INVALID_CREDENTIALS.name());
      }

      String token = SessionManager.createSession(user);

      // TODO: Remove debug statement
      SessionManager.printSessions();
      return new LoginResponse(token, user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserId(), user.getRole(), user.getIcPassportNo());
    } catch (SQLException e) {
      throw new RemoteException(AuthError.DB_ERROR.name(), e);
    }
  }

  @Override
  public void logout(String token) throws RemoteException {
    SessionManager.remove(token);
  }
}
