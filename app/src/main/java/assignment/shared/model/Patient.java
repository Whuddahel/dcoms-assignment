package assignment.shared.model;

public class Patient extends Users {
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
      String contactNumber) {
    super(userId, firstName, lastName, userRole, icPassportNo, email, password);
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
    super(0, firstName, lastName, userRole, icPassportNo, email, password);
    this.patientId = 0;
    this.medicalRecordId = medicalRecordId;
    this.contactNumber = contactNumber;
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
