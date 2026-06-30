package assignment.shared.model;

import assignment.shared.auth.Role;
import java.io.Serializable;

// User objects may or may not be transferred over RMI. Implementing Serializable for now
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int userId;
  private final String username;
  private final String passwordHash;
  private final Role role;

  public User(int userId, String username, String passwordHash, Role role) {
    this.userId = userId;
    this.username = username;
    this.passwordHash = passwordHash;
    this.role = role;
  }

  public int getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Role getRole() {
    return role;
  }
}
