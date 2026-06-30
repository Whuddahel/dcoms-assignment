package assignment.server;

import assignment.server.services.EditUserServiceImplementation;
import assignment.server.services.RegisterUserServiceImplementation;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerRegistry {
  public static void startRegistry() throws RemoteException {
    Registry serverReg = LocateRegistry.createRegistry(1099);
    serverReg.rebind("registerUser", new RegisterUserServiceImplementation());
    serverReg.rebind("editUser", new EditUserServiceImplementation());
    System.out.println("Server Registry started on port 1099");
  }
}
