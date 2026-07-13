package assignment.shared.model;

import assignment.shared.auth.Role;
import java.sql.Timestamp;

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
    this.receptionistId = receptionistId;
  }

  public Receptionist(
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

  public int getReceptionistId() {
    return receptionistId;
  }

  @Override
  public String toString() {
    return String.format("Receptionist [receptionistId=%d, %s]", receptionistId, super.toString());
  }
}
