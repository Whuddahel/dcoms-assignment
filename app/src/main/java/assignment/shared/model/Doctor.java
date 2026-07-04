package assignment.shared.model;

import assignment.shared.auth.Role;
import java.sql.Timestamp;

public class Doctor extends User {
  private final int doctorId;
  private final String specialization;

  public Doctor(
      int doctorId,
      int userId,
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password,
      String specialization,
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
    this.doctorId = doctorId;
    this.specialization = specialization;
  }

  public Doctor(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password,
      String specialization) {
    this(
        0,
        0,
        firstName,
        lastName,
        userRole,
        icPassportNo,
        email,
        password,
        specialization,
        new Timestamp(System.currentTimeMillis()),
        false);
  }

  public int getDoctorId() {
    return doctorId;
  }

  public String getSpecialization() {
    return specialization;
  }

  @Override
  public String toString() {
    return String.format(
        "Doctor [doctorId=%d, specialization=%s, %s]", doctorId, specialization, super.toString());
  }
}
