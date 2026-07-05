package assignment.shared.services;

import assignment.shared.model.Consultation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ManageConsultationService extends Remote
{
    boolean addConsultation(Consultation consultation) throws RemoteException;

    List<Consultation> getAllConsultations() throws RemoteException;

    boolean updateConsultation(Consultation consultation) throws RemoteException;
}