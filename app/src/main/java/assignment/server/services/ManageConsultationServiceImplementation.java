package assignment.server.services;

import assignment.server.database.repository.ConsultationRepository;
import assignment.shared.model.Consultation;
import assignment.shared.services.ManageConsultationService;
import assignment.shared.ssl.LenientSslRMIClientSocketFactory;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class ManageConsultationServiceImplementation extends UnicastRemoteObject
    implements ManageConsultationService {

  public ManageConsultationServiceImplementation() throws RemoteException {
    super(
        0,
        new LenientSslRMIClientSocketFactory(),
        new SslRMIServerSocketFactory(null, null, false));
  }

  @Override
  public boolean addConsultation(Consultation consultation) throws RemoteException {
    try {
      return ConsultationRepository.addConsultation(consultation);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to add consultation entry", e);
    }
  }

  @Override
  public List<Consultation> getAllConsultations() throws RemoteException {
    try {
      return ConsultationRepository.getAllConsultations();
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to retrieve consultations", e);
    }
  }

  @Override
  public boolean updateConsultation(Consultation consultation) throws RemoteException {
    try {
      return ConsultationRepository.updateConsultation(consultation);
    } catch (SQLException e) {
      throw new RemoteException("DB_ERROR: Failed to modify existing consultation notes", e);
    }
  }
}
