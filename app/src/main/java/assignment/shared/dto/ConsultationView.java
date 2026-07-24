package assignment.shared.dto;

import java.io.Serializable;

public class ConsultationView implements Serializable {
  private final int consultationId;
  private final int appointmentId;
  private final String date;
  private final String doctorName;
  private final String patientName;
  private final double fee;
  private final String notes;

  public ConsultationView(
      int consultationId,
      int appointmentId,
      String date,
      String doctorName,
      String patientName,
      double fee,
      String notes) {
    this.consultationId = consultationId;
    this.appointmentId = appointmentId;
    this.date = date;
    this.doctorName = doctorName;
    this.patientName = patientName;
    this.fee = fee;
    this.notes = notes;
  }

  public int getConsultationId() {
    return consultationId;
  }

  public int getAppointmentId() {
    return appointmentId;
  }

  public String getDate() {
    return date;
  }

  public String getDoctorName() {
    return doctorName;
  }

  public String getPatientName() {
    return patientName;
  }

  public double getFee() {
    return fee;
  }

  public String getNotes() {
    return notes;
  }
}
