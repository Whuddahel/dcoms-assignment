package assignment.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthService extends Remote {
  String login(String username, String password) throws RemoteException;

  void logout(String token) throws RemoteException;
}
