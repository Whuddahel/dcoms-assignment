package assignment.server;

public class Server {
  public static void main(String[] args) {
    try {
      ServerRegistry.startRegistry();
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Server running...");
  }
}
