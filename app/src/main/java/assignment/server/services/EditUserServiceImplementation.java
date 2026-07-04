package assignment.server.services;

import assignment.server.database.repository.ClinicAdministratorRepository;
import assignment.server.database.repository.DoctorRepository;
import assignment.server.database.repository.PatientRepository;
import assignment.server.database.repository.ReceptionistRepository;
import assignment.server.database.repository.UserRepository;
import assignment.shared.interfaces.EditUserService;
import assignment.shared.model.ClinicAdministrator;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Receptionist;
import assignment.shared.model.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class EditUserServiceImplementation extends UnicastRemoteObject implements EditUserService {
  public EditUserServiceImplementation() throws RemoteException {
    super();
  }

  @Override
  public boolean editUser(User user) throws RemoteException {
    if (user == null) {
      return false;
    }
    try {
      switch (user.getUserRole().toLowerCase()) {
        case "doctor":
          if (user instanceof Doctor) {
            return DoctorRepository.updateDoctor((Doctor) user);
          }
          break;
        case "patient":
          if (user instanceof Patient) {
            return PatientRepository.updatePatient((Patient) user);
          }
          break;
        case "admin":
          if (user instanceof ClinicAdministrator) {
            return ClinicAdministratorRepository.updateClinicAdministrator(
                (ClinicAdministrator) user);
          }
          break;
        case "receptionist":
          if (user instanceof Receptionist) {
            return ReceptionistRepository.updateReceptionist((Receptionist) user);
          }
          break;
        default:
          System.err.println("Unknown user role for edit: " + user.getUserRole());
          return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error occurred while editing user", e);
    }
    return false;
  }

  @Override
  public boolean deleteUser(User user) throws RemoteException {
    if (user == null) {
      return false;
    }
    try {
      switch (user.getUserRole().toLowerCase()) {
        case "doctor":
          if (user instanceof Doctor) {
            return DoctorRepository.deleteDoctor(((Doctor) user).getDoctorId());
          }
          break;
        case "patient":
          if (user instanceof Patient) {
            return PatientRepository.deletePatient(((Patient) user).getPatientId());
          }
          break;
        case "admin":
          if (user instanceof ClinicAdministrator) {
            return ClinicAdministratorRepository.deleteClinicAdministrator(
                ((ClinicAdministrator) user).getAdminId());
          }
          break;
        case "receptionist":
          if (user instanceof Receptionist) {
            return ReceptionistRepository.deleteReceptionist(
                ((Receptionist) user).getReceptionistId());
          }
          break;
        default:
          System.err.println("Unknown user role for delete: " + user.getUserRole());
          return false;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error occurred while deleting user", e);
    }
    return false;
  }

  @Override
  public List<User> getAllUsers() throws RemoteException {
    try {
      return UserRepository.getAllUsersWithRoles();
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RemoteException("Database error in getAllUsers", e);
    }
  }
}
