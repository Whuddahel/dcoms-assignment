package assignment.shared.model;

import assignment.shared.auth.Role;
import java.sql.Timestamp;

public class ClinicAdministrator extends User {
  private final int adminId;

  public ClinicAdministrator(
      int adminId,
      int userId,
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password,
      Timestamp createdAt,
      boolean deleted) {
    super(
        userId,
        firstName,
        lastName,
        Role.databaseToEnum(userRole),
        icPassportNo,
        email,
        password,
        createdAt,
        deleted);
    this.adminId = adminId;
  }

  public ClinicAdministrator(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password) {
    this(
        0,
        0,
        firstName,
        lastName,
        userRole,
        icPassportNo,
        email,
        password,
        new Timestamp(System.currentTimeMillis()),
        false);
  }

  public int getAdminId() {
    return adminId;
  }

  @Override
  public String toString() {
    return String.format("ClinicAdministrator [adminId=%d, %s]", adminId, super.toString());
  }
}
