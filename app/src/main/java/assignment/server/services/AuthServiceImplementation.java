package assignment.server.services;

import assignment.shared.AuthService;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AuthServiceImplementation extends UnicastRemoteObject implements AuthService {

  public AuthServiceImplementation() throws RemoteException {
    super();
  }
}
