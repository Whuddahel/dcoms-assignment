package assignment.shared.model;

import java.io.Serializable;

public class Users implements Serializable {
  private final int userId;
  private final String firstName;
  private final String lastName;
  private final String userRole;
  private final String icPassportNo;
  private final String email;
  private final String password;

  public Users(
      int userId,
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password) {
    this.userId = userId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userRole = userRole;
    this.icPassportNo = icPassportNo;
    this.email = email;
    this.password = password;
  }

  public Users(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password) {
    this(0, firstName, lastName, userRole, icPassportNo, email, password);
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

  public String getFullName() {
    return firstName + " " + lastName;
  }

  public String getUserRole() {
    return userRole;
  }

  public String getIcPassportNo() {
    return icPassportNo;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return password;
  }

  @Override
  public String toString() {
    return String.format(
        "User [userId=%d, firstName=%s, lastName=%s, userRole=%s, icPassportNo=%s, email=%s, password=%s]",
        userId, firstName, lastName, userRole, icPassportNo, email, password);
  }
}
