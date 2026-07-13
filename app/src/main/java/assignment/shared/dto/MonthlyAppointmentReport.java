package assignment.shared.dto;

import java.io.Serializable;
import java.util.List;

public class MonthlyAppointmentReport implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int totalMade;
  private final int totalCancelled;
  private final int cancelledByDoctor;
  private final int cancelledByPatient;
  private final int totalSuccessful;
  private final List<DoctorAppointmentItem> topDoctors;

  public MonthlyAppointmentReport(
      int totalMade,
      int totalCancelled,
      int cancelledByDoctor,
      int cancelledByPatient,
      int totalSuccessful,
      List<DoctorAppointmentItem> topDoctors) {
    this.totalMade = totalMade;
    this.totalCancelled = totalCancelled;
    this.cancelledByDoctor = cancelledByDoctor;
    this.cancelledByPatient = cancelledByPatient;
    this.totalSuccessful = totalSuccessful;
    this.topDoctors = topDoctors;
  }

  public int getTotalMade() {
    return totalMade;
  }

  public int getTotalCancelled() {
    return totalCancelled;
  }

  public int getCancelledByDoctor() {
    return cancelledByDoctor;
  }

  public int getCancelledByPatient() {
    return cancelledByPatient;
  }

  public int getTotalSuccessful() {
    return totalSuccessful;
  }

  public List<DoctorAppointmentItem> getTopDoctors() {
    return topDoctors;
  }

  public static class DoctorAppointmentItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int userId;
    private final String doctorName;
    private final int successfulCount;
    private final int cancelledByDoc;
    private final int cancelledByPat;

    public DoctorAppointmentItem(
        int userId,
        String doctorName,
        int successfulCount,
        int cancelledByDoc,
        int cancelledByPat) {
      this.userId = userId;
      this.doctorName = doctorName;
      this.successfulCount = successfulCount;
      this.cancelledByDoc = cancelledByDoc;
      this.cancelledByPat = cancelledByPat;
    }

    public int getUserId() {
      return userId;
    }

    public String getDoctorName() {
      return doctorName;
    }

    public int getSuccessfulCount() {
      return successfulCount;
    }

    public int getCancelledByDoc() {
      return cancelledByDoc;
    }

    public int getCancelledByPat() {
      return cancelledByPat;
    }
  }
}
