package assignment.shared.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Consultation implements Serializable {
  private final int consultationId;
  private final int appointmentId;
  private final String content;
  private final double fee;
  private final Timestamp createdAt;

  public Consultation(
      int consultationId, int appointmentId, String content, double fee, Timestamp createdAt) {
    this.consultationId = consultationId;
    this.appointmentId = appointmentId;
    this.content = content;
    this.fee = fee;
    this.createdAt = createdAt;
  }

  public Consultation(int appointmentId, String content, double fee, Timestamp createdAt) {
    this(0, appointmentId, content, fee, createdAt);
  }

  public Consultation(int appointmentId, String content, double fee) {
    this(0, appointmentId, content, fee, new java.sql.Timestamp(System.currentTimeMillis()));
  }

  public int getConsultationId() {
    return consultationId;
  }

  public int getAppointmentId() {
    return appointmentId;
  }

  public String getContent() {
    return content;
  }

  public double getFee() {
    return fee;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    return String.format(
        "Consultation [consultationId=%d, appointmentId=%d, content=%s, fee=%.2f, createdAt=%s]",
        consultationId, appointmentId, content, fee, createdAt);
  }
}
