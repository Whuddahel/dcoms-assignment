package assignment.server.services;

import assignment.server.database.repository.ClinicAdministratorRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ReceptionistRepository;
import assignment.shared.interfaces.RegisterUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

public class RegisterUserServiceImplementation extends UnicastRemoteObject
    implements RegisterUserService {

  public RegisterUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean registerUser(User user) throws RemoteException {
    if (user == null) {
      return false;
    }
    try {
      switch (user.getUserRole().toLowerCase()) {
        case "doctor":
          if (user instanceof Doctor) {
            return DoctorRepository.addDoctor((Doctor) user);
          }
          break;
        case "patient":
          if (user instanceof Patient) {
            return PatientRepository.addPatient((Patient) user);
          }
          break;
        case "admin":
          if (user instanceof ClinicAdministrator) {
            return ClinicAdministratorRepository.addClinicAdministrator((ClinicAdministrator) user);
          }
          break;
        case "receptionist":
          if (user instanceof Receptionist) {
            return ReceptionistRepository.addReceptionist((Receptionist) user);
          }
          break;
        default:
          System.err.println("Unknown user role: " + user.getUserRole());
          return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error occurred while registering user", e);
    }
    return false;
  }
}
