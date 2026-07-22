package assignment.shared.services;

import assignment.shared.model.Consultation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ManageConsultationService extends Remote {
  boolean addConsultation(String token, Consultation consultation) throws RemoteException;

  List<Consultation> getAllConsultations(String token) throws RemoteException;

  boolean updateConsultation(String token, Consultation consultation) throws RemoteException;
}
