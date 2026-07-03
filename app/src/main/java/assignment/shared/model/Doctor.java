package assignment.shared.model;

public class Doctor extends Users {
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
      String specialization) {
    super(userId, firstName, lastName, userRole, icPassportNo, email, password);
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
    super(0, firstName, lastName, userRole, icPassportNo, email, password);
    this.doctorId = 0;
    this.specialization = specialization;
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
