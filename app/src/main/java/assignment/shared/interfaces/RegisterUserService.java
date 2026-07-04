package assignment.shared.interfaces;

import assignment.shared.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegisterUserService extends Remote {
  // Function declarations go here
  public boolean registerUser(User user) throws RemoteException;
}
