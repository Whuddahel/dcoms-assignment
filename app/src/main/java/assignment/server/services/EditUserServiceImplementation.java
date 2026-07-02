package assignment.server.services;

import assignment.shared.interfaces.EditUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.Users;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class EditUserServiceImplementation extends UnicastRemoteObject implements EditUserService {
  public EditUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean editUser(Doctor doctor) throws RemoteException {
    try {
      EditUserService editUser = (EditUserService) Naming.lookup("rmi://localhost:1040/editUser");
      return editUser.editUser(doctor);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean editUser(Patient patient) throws RemoteException {
    try {
      EditUserService editUser = (EditUserService) Naming.lookup("rmi://localhost:1040/editUser");
      return editUser.editUser(patient);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean editUser(ClinicAdministrator clinicAdministrator) throws RemoteException {
    try {
      EditUserService editUser = (EditUserService) Naming.lookup("rmi://localhost:1040/editUser");
      return editUser.editUser(clinicAdministrator);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean editUser(Receptionist receptionist) throws RemoteException {
    try {
      EditUserService editUser = (EditUserService) Naming.lookup("rmi://localhost:1040/editUser");
      return editUser.editUser(receptionist);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public List<Users> getAllUsers() throws RemoteException {
    try {
      EditUserService editUser = (EditUserService) Naming.lookup("rmi://localhost:1040/editUser");
      return editUser.getAllUsers();
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      throw new RemoteException("Database server connection failed", e);
    }
  }
}
