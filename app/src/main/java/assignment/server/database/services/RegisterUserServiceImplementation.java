package assignment.server.database.services;

import assignment.server.database.repository.ClinicAdministratorRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ReceptionistRepository;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegisterUserServiceImplementation extends UnicastRemoteObject
    implements RegisterUserService {

  public RegisterUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean registerUser(Doctor doctor) throws RemoteException {
    return DoctorRepository.addDoctor(doctor);
  }

  @Override
  public boolean registerUser(Patient patient) throws RemoteException {
    return PatientRepository.addPatient(patient);
  }

  @Override
  public boolean registerUser(ClinicAdministrator admin) throws RemoteException {
    return ClinicAdministratorRepository.addClinicAdministrator(admin);
  }

  @Override
  public boolean registerUser(Receptionist receptionist) throws RemoteException {
    return ReceptionistRepository.addReceptionist(receptionist);
  }
}
