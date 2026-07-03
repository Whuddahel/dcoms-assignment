package assignment.shared.interfaces;

import assignment.shared.model.Users;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface EditUserService extends Remote {
  public boolean editUser(Users user) throws RemoteException;

  public List<Users> getAllUsers() throws RemoteException;
}
