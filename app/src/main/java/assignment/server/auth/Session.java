package assignment.server.auth;

import assignment.shared.auth.Role;
import assignment.shared.model.User;
import java.time.Instant;

/** Sessions contain information that the server needs to retain. */
public class Session {
  private final int userId;
  private final String username;
  private final Role role;

  // Following fields may or may not be used
  private final Instant loginTime;
  private Instant lastAccessTime;

  public Session(User user) {
    this.userId = user.getUserId();
    this.username = user.getUsername();
    this.role = user.getRole();

    this.loginTime = Instant.now();
    this.lastAccessTime = this.loginTime;
  }

  public int getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public Role getRole() {
    return role;
  }

  public Instant getLoginTime() {
    return loginTime;
  }

  public Instant getLastAccessTime() {
    return lastAccessTime;
  }

  public void refresh() {
    lastAccessTime = Instant.now();
  }
}
