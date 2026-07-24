package assignment.shared.dto;

import assignment.shared.auth.Role;
import java.io.Serializable;

public class LoginResponse implements Serializable {
  private String token;
  private String email;
  private String firstName;
  private String lastName;
  private int userId;
  private Role role;
  private String icPassport;

  // Constructor
  public LoginResponse(
      String token,
      String email,
      String firstName,
      String lastName,
      int userId,
      Role role,
      String icPassport) {
    this.token = token;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userId = userId;
    this.role = role;
    this.icPassport = icPassport;
  }

  public String getToken() {
    return token;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public int getUserId() {
    return userId;
  }

  public Role getRole() {
    return role;
  }

  public String getIcPassport() {
    return icPassport;
  }
}
