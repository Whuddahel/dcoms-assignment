package assignment.shared.interfaces;

import assignment.shared.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EditUserService extends Remote {
  public boolean editUser(String token, User user) throws RemoteException;

  public boolean deleteUser(String token, User user) throws RemoteException;

  public List<User> getAllUsers(String token) throws RemoteException;
}
