package assignment.shared.model;

import assignment.shared.auth.Role;

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
      String password) {
    super(
        userId, firstName, lastName, Role.databaseToEnum(userRole), icPassportNo, email, password);
    this.adminId = adminId;
  }

  public ClinicAdministrator(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password) {
    super(0, firstName, lastName, Role.databaseToEnum(userRole), icPassportNo, email, password);
    this.adminId = 0;
  }

  public int getAdminId() {
    return adminId;
  }

  @Override
  public String toString() {
    return String.format("ClinicAdministrator [adminId=%d, %s]", adminId, super.toString());
  }
}
