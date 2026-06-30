package assignment.server.auth;

import assignment.shared.model.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
  private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

  public SessionManager() {}

  public static String createSession(User user) {
    String token = UUID.randomUUID().toString();
    Session session = new Session(user);
    sessions.put(token, session);
    return token;
  }

  public static Session getUser(String token) {
    Session session = sessions.get(token);
    if (session != null) {
      session.refresh();
    }
    return session;
  }

  public static void remove(String token) {
    sessions.remove(token);
  }

  // ! TODO: Remove test method
  public static void printSessions() {

    System.out.println("=== ACTIVE SESSIONS ===");

    if (sessions.isEmpty()) {
      System.out.println("(none)");
    }

    sessions.forEach(
        (token, session) ->
            System.out.println(
                token + " -> " + session.getUsername() + " (" + session.getRole() + ")"));

    System.out.println("=======================");
  }
}
