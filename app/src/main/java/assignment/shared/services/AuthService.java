package assignment.shared.services;

import assignment.server.auth.Session;
import assignment.shared.dto.LoginResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthService extends Remote {
  LoginResponse login(String username, String password) throws RemoteException;

  void logout(String token) throws RemoteException;
}
