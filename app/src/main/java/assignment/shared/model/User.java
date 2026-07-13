package assignment.shared.model;

import assignment.shared.auth.Role;
import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int userId;
  private final String firstName;
  private final String lastName;
  private final Role role;
  private final String icPassportNo;
  private final String email;
  private final String passwordHash;
  private final Timestamp createdAt;
  private final boolean deleted;

  // Primary Constructor
  public User(
      int userId,
      String firstName,
      String lastName,
      Role role,
      String icPassportNo,
      String email,
      String passwordHash,
      Timestamp createdAt,
      boolean deleted) {
    this.userId = userId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
    this.icPassportNo = icPassportNo;
    this.email = email;
    this.passwordHash = passwordHash;
    this.createdAt = createdAt;
    this.deleted = deleted;
  }

  // Overloaded Constructor for creating users before database ID generation
  public User(
      String firstName,
      String lastName,
      Role role,
      String icPassportNo,
      String email,
      String passwordHash) {
    this(
        0,
        firstName,
        lastName,
        role,
        icPassportNo,
        email,
        passwordHash,
        new Timestamp(System.currentTimeMillis()),
        false);
  }

  public int getUserId() {
    return userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getUsername() {
    return firstName + " " + lastName;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  public Role getRole() {
    return role;
  }

  public String getUserRole() {
    return role != null ? role.name().toLowerCase() : null;
  }

  public String getIcPassportNo() {
    return icPassportNo;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public boolean isDeleted() {
    return deleted;
  }

  @Override
  public String toString() {
    return String.format(
        "User [userId=%d, firstName=%s, lastName=%s, role=%s, icPassportNo=%s, email=%s, passwordHash=%s, createdAt=%s, deleted=%b]",
        userId, firstName, lastName, role, icPassportNo, email, passwordHash, createdAt, deleted);
  }
}
