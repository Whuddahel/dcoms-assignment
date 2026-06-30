package assignment.shared.model;

import java.io.Serializable;

// User objects may or may not be transferred over RMI. Implementing Serializable for now
public class User implements Serializable {
  private final int userId;
  private final String username;
  private final String passwordHash;
  private final String role;

  public User(int userId, String username, String passwordHash, String role) {
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

  public String getRole() {
    return role;
  }
}
