package assignment.shared.model;

import assignment.shared.auth.Role;

public class Receptionist extends User {
  private final int receptionistId;

  public Receptionist(
      int receptionistId,
      int userId,
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password) {
    super(
        userId, firstName, lastName, Role.databaseToEnum(userRole), icPassportNo, email, password);
    this.receptionistId = receptionistId;
  }

  public Receptionist(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password) {
    super(0, firstName, lastName, Role.databaseToEnum(userRole), icPassportNo, email, password);
    this.receptionistId = 0;
  }

  public int getReceptionistId() {
    return receptionistId;
  }

  @Override
  public String toString() {
    return String.format("Receptionist [receptionistId=%d, %s]", receptionistId, super.toString());
  }
}
