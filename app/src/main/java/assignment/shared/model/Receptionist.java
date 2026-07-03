package assignment.shared.model;

public class Receptionist extends Users {
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
    super(userId, firstName, lastName, userRole, icPassportNo, email, password);
    this.receptionistId = receptionistId;
  }

  public Receptionist(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password) {
    super(0, firstName, lastName, userRole, icPassportNo, email, password);
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
