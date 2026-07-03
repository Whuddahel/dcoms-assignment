package assignment.server.database;

import assignment.server.database.services.EditUserServiceImplementation;
import assignment.server.database.services.RegisterUserServiceImplementation;
import assignment.shared.config.Config;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DatabaseRegistry {
  public static void startRegistry() throws RemoteException {
    Registry serverReg = LocateRegistry.createRegistry(Config.DB_REGISTRY_PORT);
    serverReg.rebind("RegisterUser", new RegisterUserServiceImplementation());
    serverReg.rebind("EditUser", new EditUserServiceImplementation());
    System.out.println("Registry started on port " + Config.DB_REGISTRY_PORT);
  }
}
