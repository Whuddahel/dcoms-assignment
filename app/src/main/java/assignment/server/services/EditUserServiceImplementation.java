package assignment.server.services;

import assignment.shared.config.Config;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.Users;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class EditUserServiceImplementation extends UnicastRemoteObject implements EditUserService {
  public EditUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean editUser(Doctor doctor) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      EditUserService editUser = (EditUserService) registry.lookup("EditUser");
      return editUser.editUser(doctor);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean editUser(Patient patient) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      EditUserService editUser = (EditUserService) registry.lookup("EditUser");
      return editUser.editUser(patient);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean editUser(ClinicAdministrator clinicAdministrator) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      EditUserService editUser = (EditUserService) registry.lookup("EditUser");
      return editUser.editUser(clinicAdministrator);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean editUser(Receptionist receptionist) throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      EditUserService editUser = (EditUserService) registry.lookup("EditUser");
      return editUser.editUser(receptionist);
    } catch (NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public List<Users> getAllUsers() throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(Config.DB_HOST, Config.DB_REGISTRY_PORT);
      EditUserService editUser = (EditUserService) registry.lookup("EditUser");
      return editUser.getAllUsers();
    } catch (NotBoundException e) {
      e.printStackTrace();
      throw new RemoteException("Database server connection failed", e);
    }
  }
}
