package assignment.shared.dto;

import java.io.Serializable;

public class AppointmentView implements Serializable {
  private final int appointmentId;
  private final String appointmentDate;
  private final String doctorName;
  private final String patientName;
  private final String timeSlot;
  private final String status;

  public AppointmentView(
      int appointmentId,
      String appointmentDate,
      String doctorName,
      String patientName,
      String timeSlot,
      String status) {
    this.appointmentId = appointmentId;
    this.appointmentDate = appointmentDate;
    this.doctorName = doctorName;
    this.patientName = patientName;
    this.timeSlot = timeSlot;
    this.status = status;
  }

  public int getAppointmentId() {
    return appointmentId;
  }

  public String getAppointmentDate() {
    return appointmentDate;
  }

  public String getDoctorName() {
    return doctorName;
  }

  public String getPatientName() {
    return patientName;
  }

  public String getTimeSlot() {
    return timeSlot;
  }

  public String getStatus() {
    return status;
  }
}
