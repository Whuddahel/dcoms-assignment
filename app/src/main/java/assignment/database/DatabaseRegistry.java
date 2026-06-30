package assignment.database;

import assignment.database.services.EditUserServiceImplementation;
import assignment.database.services.RegisterUserServiceImplementation;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DatabaseRegistry {
  public static void startRegistry() throws RemoteException {
    Registry serverReg = LocateRegistry.createRegistry(1040);
    serverReg.rebind("registerUser", new RegisterUserServiceImplementation());
    serverReg.rebind("editUser", new EditUserServiceImplementation());
    System.out.println("Registry started on port 1040");
  }
}
