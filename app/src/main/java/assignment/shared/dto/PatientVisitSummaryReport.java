package assignment.shared.dto;

import java.io.Serializable;
import java.util.List;

public class PatientVisitSummaryReport implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int newPatientsCount;
  private final List<PatientVisitItem> patientVisits;

  public PatientVisitSummaryReport(int newPatientsCount, List<PatientVisitItem> patientVisits) {
    this.newPatientsCount = newPatientsCount;
    this.patientVisits = patientVisits;
  }

  public int getNewPatientsCount() {
    return newPatientsCount;
  }

  public List<PatientVisitItem> getPatientVisits() {
    return patientVisits;
  }

  public static class PatientVisitItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int userId;
    private final String patientName;
    private final int consultationsCount;
    private final boolean registeredThisMonth;

    public PatientVisitItem(
        int userId, String patientName, int consultationsCount, boolean registeredThisMonth) {
      this.userId = userId;
      this.patientName = patientName;
      this.consultationsCount = consultationsCount;
      this.registeredThisMonth = registeredThisMonth;
    }

    public int getUserId() {
      return userId;
    }

    public String getPatientName() {
      return patientName;
    }

    public int getConsultationsCount() {
      return consultationsCount;
    }

    public boolean isRegisteredThisMonth() {
      return registeredThisMonth;
    }
  }
}
