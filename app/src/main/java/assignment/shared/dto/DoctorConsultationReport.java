package assignment.shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DoctorConsultationReport implements Serializable {
  private static final long serialVersionUID = 1L;

  private final int totalConsultations;
  private final BigDecimal totalEarning;
  private final List<DoctorConsultationItem> doctorConsultations;

  public DoctorConsultationReport(
      int totalConsultations,
      BigDecimal totalEarning,
      List<DoctorConsultationItem> doctorConsultations) {
    this.totalConsultations = totalConsultations;
    this.totalEarning = totalEarning;
    this.doctorConsultations = doctorConsultations;
  }

  public int getTotalConsultations() {
    return totalConsultations;
  }

  public BigDecimal getTotalEarning() {
    return totalEarning;
  }

  public List<DoctorConsultationItem> getDoctorConsultations() {
    return doctorConsultations;
  }

  public static class DoctorConsultationItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int userId;
    private final String doctorName;
    private final int consultationsCount;
    private final BigDecimal totalEarning;

    public DoctorConsultationItem(
        int userId, String doctorName, int consultationsCount, BigDecimal totalEarning) {
      this.userId = userId;
      this.doctorName = doctorName;
      this.consultationsCount = consultationsCount;
      this.totalEarning = totalEarning;
    }

    public int getUserId() {
      return userId;
    }

    public String getDoctorName() {
      return doctorName;
    }

    public int getConsultationsCount() {
      return consultationsCount;
    }

    public BigDecimal getTotalEarning() {
      return totalEarning;
    }
  }
}
