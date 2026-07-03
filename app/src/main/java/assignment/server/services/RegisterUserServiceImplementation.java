package assignment.server.services;

import assignment.shared.config.Config;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
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
  public boolean registerUser(Doctor doctor) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      RegisterUserService registerUser = (RegisterUserService) registry.lookup("RegisterUser");
      return registerUser.registerUser(doctor);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean registerUser(Patient patient) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      RegisterUserService registerUser = (RegisterUserService) registry.lookup("RegisterUser");
      return registerUser.registerUser(patient);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean registerUser(ClinicAdministrator admin) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      RegisterUserService registerUser = (RegisterUserService) registry.lookup("RegisterUser");
      return registerUser.registerUser(admin);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean registerUser(Receptionist receptionist) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      RegisterUserService registerUser = (RegisterUserService) registry.lookup("RegisterUser");
      return registerUser.registerUser(receptionist);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }
}
