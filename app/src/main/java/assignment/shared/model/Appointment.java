package assignment.shared.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Appointment implements Serializable {
  private final int appointmentId;
  private final int doctorId;
  private final int patientId;
  private final int scheduleId;
  private final Timestamp createdAt;
  private final Integer cancelledByUserId;

  public Appointment(
      int appointmentId,
      int doctorId,
      int patientId,
      int scheduleId,
      Timestamp createdAt,
      Integer cancelledByUserId) {
    this.appointmentId = appointmentId;
    this.doctorId = doctorId;
    this.patientId = patientId;
    this.scheduleId = scheduleId;
    this.createdAt = createdAt;
    this.cancelledByUserId = cancelledByUserId;
  }

  public Appointment(
      int doctorId, int patientId, int scheduleId, Timestamp createdAt, Integer cancelledByUserId) {
    this(0, doctorId, patientId, scheduleId, createdAt, cancelledByUserId);
  }

  public Appointment(int doctorId, int patientId, int scheduleId) {
    this(
        0,
        doctorId,
        patientId,
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

  public int getPatientId() {
    return patientId;
  }

  public int getScheduleId() {
    return scheduleId;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public Integer getcancelledByUserId() {
    return cancelledByUserId;
  }

  @Override
  public String toString() {
    return String.format(
        "Appointment [appointmentId=%d, doctorId=%d, patientId=%d, scheduleId=%d, createdAt=%s, cancelledByUserId=%d]",
        appointmentId, doctorId, patientId, scheduleId, createdAt, cancelledByUserId);
  }
}
