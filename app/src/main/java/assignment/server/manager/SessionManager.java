package assignment.server.manager;

import assignment.shared.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
  private static final Map<String, User> sessions = new HashMap<>();

  public static String createSession(User user) {
    String token = UUID.randomUUID().toString();
    sessions.put(token, user);
    return token;
  }

  public static User getUser(String token) {
    return sessions.get(token);
  }

  public static void remove(String token) {
    sessions.remove(token);
  }
}
