package assignment.server.services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import assignment.shared.AuthService;

public class AuthServiceImplementation extends UnicastRemoteObject implements AuthService{

    protected AuthServiceImplementation() throws RemoteException {
        super();
    }

}
