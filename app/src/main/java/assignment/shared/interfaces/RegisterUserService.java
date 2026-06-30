package assignment.shared.interfaces;

import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegisterUserService extends Remote {
  // Function declarations go here
  public boolean registerUser(Doctor doctor) throws RemoteException;

  public boolean registerUser(Patient patient) throws RemoteException;

  public boolean registerUser(ClinicAdministrator admin) throws RemoteException;

  public boolean registerUser(Receptionist receptionist) throws RemoteException;
}
