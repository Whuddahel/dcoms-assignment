package assignment.server.services;

import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegisterUserServiceImplementation extends UnicastRemoteObject
    implements RegisterUserService {

  public RegisterUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean registerUser(Doctor doctor) throws RemoteException {
    try {
      RegisterUserService registerUser =
          (RegisterUserService) Naming.lookup("rmi://localhost:1040/registerUser");
      return registerUser.registerUser(doctor);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean registerUser(Patient patient) throws RemoteException {
    try {
      RegisterUserService registerUser =
          (RegisterUserService) Naming.lookup("rmi://localhost:1040/registerUser");
      return registerUser.registerUser(patient);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean registerUser(ClinicAdministrator admin) throws RemoteException {
    try {
      RegisterUserService registerUser =
          (RegisterUserService) Naming.lookup("rmi://localhost:1040/registerUser");
      return registerUser.registerUser(admin);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean registerUser(Receptionist receptionist) throws RemoteException {
    try {
      RegisterUserService registerUser =
          (RegisterUserService) Naming.lookup("rmi://localhost:1040/registerUser");
      return registerUser.registerUser(receptionist);
    } catch (MalformedURLException | NotBoundException e) {
      e.printStackTrace();
      return false;
    }
  }
}
