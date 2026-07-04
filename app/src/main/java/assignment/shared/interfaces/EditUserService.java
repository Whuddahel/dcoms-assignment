package assignment.shared.interfaces;

import assignment.shared.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EditUserService extends Remote {
  public boolean editUser(User user) throws RemoteException;

  public boolean deleteUser(User user) throws RemoteException;

  public List<User> getAllUsers() throws RemoteException;
}
