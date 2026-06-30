package assignment.shared.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;

public interface EditUserService extends Remote {
    public boolean editUser(Doctor doctor) throws RemoteException;

    public boolean editUser(Patient patient) throws RemoteException;

    public boolean editUser(ClinicAdministrator clinicAdministrator) throws RemoteException;

    public boolean editUser(Receptionist receptionist) throws RemoteException;
}
