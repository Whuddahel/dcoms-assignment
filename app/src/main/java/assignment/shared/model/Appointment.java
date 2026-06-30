package assignment.shared.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Appointment implements Serializable {
  private final int appointmentId;
  private final int doctorId;
  private final int medicalRecordId;
  private final int scheduleId;
  private final Timestamp createdAt;
  private final Integer cancelledBy;

  public Appointment(
      int appointmentId,
      int doctorId,
      int medicalRecordId,
      int scheduleId,
      Timestamp createdAt,
      Integer cancelledBy) {
    this.appointmentId = appointmentId;
    this.doctorId = doctorId;
    this.medicalRecordId = medicalRecordId;
    this.scheduleId = scheduleId;
    this.createdAt = createdAt;
    this.cancelledBy = cancelledBy;
  }

  public Appointment(
      int doctorId, int medicalRecordId, int scheduleId, Timestamp createdAt, Integer cancelledBy) {
    this(0, doctorId, medicalRecordId, scheduleId, createdAt, cancelledBy);
  }

  public Appointment(int doctorId, int medicalRecordId, int scheduleId) {
    this(
        0,
        doctorId,
        medicalRecordId,
        scheduleId,
        new java.sql.Timestamp(System.currentTimeMillis()),
        null);
  }

  public int getAppointmentId() {
    return appointmentId;
  }

  public int getDoctorId() {
    return doctorId;
  }

  public int getMedicalRecordId() {
    return medicalRecordId;
  }

  public int getScheduleId() {
    return scheduleId;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Integer getCancelledBy() {
    return cancelledBy;
  }

  @Override
  public String toString() {
    return String.format(
        "Appointment [appointmentId=%d, doctorId=%d, medicalRecordId=%d, scheduleId=%d, createdAt=%s, cancelledBy=%d]",
        appointmentId, doctorId, medicalRecordId, scheduleId, createdAt, cancelledBy);
  }
}
