package assignment.database.services;

import assignment.database.repository.ClinicAdministratorRepository;
import assignment.database.repository.DoctorRepository;
import assignment.database.repository.PatientRepository;
import assignment.database.repository.ReceptionistRepository;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class EditUserServiceImplementation extends UnicastRemoteObject implements EditUserService {
  public EditUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean editUser(Doctor doctor) throws RemoteException {
    return DoctorRepository.updateDoctor(doctor);
  }

  @Override
  public boolean editUser(Patient patient) throws RemoteException {
    return PatientRepository.updatePatient(patient);
  }

  @Override
  public boolean editUser(ClinicAdministrator admin) throws RemoteException {
    return ClinicAdministratorRepository.updateClinicAdministrator(admin);
  }

  @Override
  public boolean editUser(Receptionist receptionist) throws RemoteException {
    return ReceptionistRepository.updateReceptionist(receptionist);
  }
}
