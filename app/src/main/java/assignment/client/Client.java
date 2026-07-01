package assignment.client;

import assignment.shared.services.AuthService;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.mindrot.jbcrypt.BCrypt;

public class Client {
  public static void main(String[] args) {
    try {
      Registry registry = LocateRegistry.getRegistry("localhost", 1099);
      AuthService authService = (AuthService) registry.lookup("AuthService");
      System.out.println(BCrypt.hashpw("Choong The Wanking Shawn", BCrypt.gensalt()));
      String token = authService.login("michael", "Choong The Wanking Shawn");

      System.out.println(token);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
