package assignment.server.services;

import assignment.shared.config.Config;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.User;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RegisterUserServiceImplementation extends UnicastRemoteObject
    implements RegisterUserService {

  public RegisterUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean registerUser(User user) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      RegisterUserService registerUser = (RegisterUserService) registry.lookup("RegisterUser");
      return registerUser.registerUser(user);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }
}
