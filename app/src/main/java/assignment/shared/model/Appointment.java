package assignment.shared.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Appointment implements Serializable {
  private final int appointmentId;
  private final int doctorId;
  private final int patientId;
  private final int scheduleId;
  private final Date appointmentDate;
  private final Timestamp createdAt;
  private final Integer cancelledByUserId;

  public Appointment(
      int appointmentId,
      int doctorId,
      int patientId,
      int scheduleId,
      Date appointmentDate,
      Timestamp createdAt,
      Integer cancelledByUserId) {
    this.appointmentId = appointmentId;
    this.doctorId = doctorId;
    this.patientId = patientId;
    this.scheduleId = scheduleId;
    this.appointmentDate = appointmentDate;
    this.createdAt = createdAt;
    this.cancelledByUserId = cancelledByUserId;
  }

  public Appointment(int doctorId, int patientId, int scheduleId, Date appointmentDate) {
    this(
        0,
        doctorId,
        patientId,
        scheduleId,
        appointmentDate,
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

  public Date getAppointmentDate() {
    return appointmentDate;
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
        "Appointment [appointmentId=%d, doctorId=%d, patientId=%d, scheduleId=%d, appointmentDate=%s, createdAt=%s, cancelledByUserId=%d]",
        appointmentId,
        doctorId,
        patientId,
        scheduleId,
        appointmentDate,
        createdAt,
        cancelledByUserId);
  }
}
