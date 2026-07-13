package assignment.shared.model;

import assignment.shared.auth.Role;
import java.sql.Timestamp;

public class Patient extends User {
  private final int patientId;
  private final int medicalRecordId;
  private final String contactNumber;

  public Patient(
      int patientId,
      int userId,
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password,
      int medicalRecordId,
      String contactNumber,
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
    this.patientId = patientId;
    this.medicalRecordId = medicalRecordId;
    this.contactNumber = contactNumber;
  }

  public Patient(
      String firstName,
      String lastName,
      String userRole,
      String icPassportNo,
      String email,
      String password,
      int medicalRecordId,
      String contactNumber) {
    this(
        0,
        0,
        firstName,
        lastName,
        userRole,
        icPassportNo,
        email,
        password,
        medicalRecordId,
        contactNumber,
        new Timestamp(System.currentTimeMillis()),
        false);
  }

  public int getPatientId() {
    return patientId;
  }

  public int getMedicalRecordId() {
    return medicalRecordId;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  @Override
  public String toString() {
    return String.format(
        "Patient [patientId=%d, medicalRecordId=%d, contactNumber=%s, %s]",
        patientId, medicalRecordId, contactNumber, super.toString());
  }
}
