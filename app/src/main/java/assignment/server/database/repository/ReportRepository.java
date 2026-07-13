package assignment.server.database.repository;

import assignment.server.database.DatabaseManager;
import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportRepository {

  private static Date[] getStartAndEndDates(int year, int month) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    Date start = new Date(cal.getTimeInMillis());

    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    Date end = new Date(cal.getTimeInMillis());

    return new Date[] {start, end};
  }

  private static Timestamp[] getStartAndEndTimestamps(int year, int month) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Timestamp start = new Timestamp(cal.getTimeInMillis());

    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
    Timestamp end = new Timestamp(cal.getTimeInMillis());

    return new Timestamp[] {start, end};
  }

  public static MonthlyAppointmentReport getMonthlyAppointmentReport(int year, int month)
      throws SQLException {
    Date[] range = getStartAndEndDates(year, month);
    Date start = range[0];
    Date end = range[1];

    int totalSuccessful = 0;
    int totalCancelled = 0;
    int cancelledByDoctor = 0;
    int cancelledByPatient = 0;

    String successSql =
        "SELECT COUNT(a.appointmentId) FROM Appointment a "
            + "JOIN Consultation c ON a.appointmentId = c.appointmentId "
            + "WHERE a.appointmentDate >= ? AND a.appointmentDate <= ?";

    String cancelledSql =
        "SELECT COUNT(a.appointmentId) FROM Appointment a "
            + "WHERE a.appointmentDate >= ? AND a.appointmentDate <= ? AND a.cancelledByUserId IS NOT NULL";

    String docCancelSql =
        "SELECT COUNT(a.appointmentId) FROM Appointment a "
            + "JOIN Users u ON a.cancelledByUserId = u.userId "
            + "WHERE a.appointmentDate >= ? AND a.appointmentDate <= ? AND u.userRole = 'doctor'";

    String patCancelSql =
        "SELECT COUNT(a.appointmentId) FROM Appointment a "
            + "JOIN Users u ON a.cancelledByUserId = u.userId "
            + "WHERE a.appointmentDate >= ? AND a.appointmentDate <= ? AND u.userRole = 'patient'";

    try (Connection conn = DatabaseManager.getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(successSql)) {
        ps.setDate(1, start);
        ps.setDate(2, end);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) totalSuccessful = rs.getInt(1);
        }
      }
      try (PreparedStatement ps = conn.prepareStatement(cancelledSql)) {
        ps.setDate(1, start);
        ps.setDate(2, end);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) totalCancelled = rs.getInt(1);
        }
      }
      try (PreparedStatement ps = conn.prepareStatement(docCancelSql)) {
        ps.setDate(1, start);
        ps.setDate(2, end);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) cancelledByDoctor = rs.getInt(1);
        }
      }
      try (PreparedStatement ps = conn.prepareStatement(patCancelSql)) {
        ps.setDate(1, start);
        ps.setDate(2, end);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) cancelledByPatient = rs.getInt(1);
        }
      }
    }

    int totalMade = totalSuccessful + totalCancelled;

    // Top 10 Doctors
    List<MonthlyAppointmentReport.DoctorAppointmentItem> topDoctors = new ArrayList<>();
    String topDocSql =
        "SELECT d.doctorId, u.userId, u.firstName, u.lastName, COUNT(c.consultationId) AS successfulCount "
            + "FROM Doctor d "
            + "JOIN Users u ON d.userId = u.userId "
            + "JOIN Appointment a ON d.doctorId = a.doctorId "
            + "JOIN Consultation c ON a.appointmentId = c.appointmentId "
            + "WHERE a.appointmentDate >= ? AND a.appointmentDate <= ? AND u.deleted = false "
            + "GROUP BY d.doctorId, u.userId, u.firstName, u.lastName "
            + "ORDER BY successfulCount DESC, u.lastName ASC, u.firstName ASC "
            + "FETCH FIRST 10 ROWS ONLY";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(topDocSql)) {
      ps.setDate(1, start);
      ps.setDate(2, end);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int docId = rs.getInt("doctorId");
          int userId = rs.getInt("userId");
          String doctorName = "Dr. " + rs.getString("firstName") + " " + rs.getString("lastName");
          int successfulCount = rs.getInt("successfulCount");

          // Cancelled counts for this doctor in this month
          int cancelledByDoc = 0;
          int cancelledByPat = 0;
          String docCancelledCountsSql =
              "SELECT "
                  + "  SUM(CASE WHEN u.userRole = 'doctor' THEN 1 ELSE 0 END) AS cDoc, "
                  + "  SUM(CASE WHEN u.userRole = 'patient' THEN 1 ELSE 0 END) AS cPat "
                  + "FROM Appointment a "
                  + "JOIN Users u ON a.cancelledByUserId = u.userId "
                  + "WHERE a.doctorId = ? AND a.appointmentDate >= ? AND a.appointmentDate <= ?";

          try (PreparedStatement ps2 = conn.prepareStatement(docCancelledCountsSql)) {
            ps2.setInt(1, docId);
            ps2.setDate(2, start);
            ps2.setDate(3, end);
            try (ResultSet rs2 = ps2.executeQuery()) {
              if (rs2.next()) {
                cancelledByDoc = rs2.getInt("cDoc");
                cancelledByPat = rs2.getInt("cPat");
              }
            }
          }

          topDoctors.add(
              new MonthlyAppointmentReport.DoctorAppointmentItem(
                  userId, doctorName, successfulCount, cancelledByDoc, cancelledByPat));
        }
      }
    }

    return new MonthlyAppointmentReport(
        totalMade,
        totalCancelled,
        cancelledByDoctor,
        cancelledByPatient,
        totalSuccessful,
        topDoctors);
  }

  public static DoctorConsultationReport getDoctorConsultationReport(int year, int month)
      throws SQLException {
    Timestamp[] range = getStartAndEndTimestamps(year, month);
    Timestamp start = range[0];
    Timestamp end = range[1];

    int totalConsultations = 0;
    BigDecimal totalEarning = BigDecimal.ZERO;

    String totalSql =
        "SELECT COUNT(consultationId), SUM(fee) FROM Consultation "
            + "WHERE createdAt >= ? AND createdAt <= ?";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(totalSql)) {
      ps.setTimestamp(1, start);
      ps.setTimestamp(2, end);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          totalConsultations = rs.getInt(1);
          BigDecimal feeSum = rs.getBigDecimal(2);
          if (feeSum != null) {
            totalEarning = feeSum;
          }
        }
      }
    }

    // List of active doctors and their performance
    List<DoctorConsultationReport.DoctorConsultationItem> doctorConsultations = new ArrayList<>();
    String listSql =
        "SELECT d.doctorId, u.userId, u.firstName, u.lastName, "
            + "       COUNT(c.consultationId) AS consultationsCount, "
            + "       SUM(c.fee) AS docEarning "
            + "FROM Doctor d "
            + "JOIN Users u ON d.userId = u.userId "
            + "LEFT JOIN Appointment a ON d.doctorId = a.doctorId "
            + "LEFT JOIN Consultation c ON a.appointmentId = c.appointmentId AND c.createdAt >= ? AND c.createdAt <= ? "
            + "WHERE u.deleted = false "
            + "GROUP BY d.doctorId, u.userId, u.firstName, u.lastName "
            + "ORDER BY consultationsCount DESC, u.lastName ASC, u.firstName ASC";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(listSql)) {
      ps.setTimestamp(1, start);
      ps.setTimestamp(2, end);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int userId = rs.getInt("userId");
          String doctorName = "Dr. " + rs.getString("firstName") + " " + rs.getString("lastName");
          int count = rs.getInt("consultationsCount");
          BigDecimal earning = rs.getBigDecimal("docEarning");
          if (earning == null) {
            earning = BigDecimal.ZERO;
          }

          doctorConsultations.add(
              new DoctorConsultationReport.DoctorConsultationItem(
                  userId, doctorName, count, earning));
        }
      }
    }

    return new DoctorConsultationReport(totalConsultations, totalEarning, doctorConsultations);
  }

  public static PatientVisitSummaryReport getPatientVisitSummaryReport(int year, int month)
      throws SQLException {
    Timestamp[] range = getStartAndEndTimestamps(year, month);
    Timestamp start = range[0];
    Timestamp end = range[1];

    int newPatientsCount = 0;
    String newPatientSql =
        "SELECT COUNT(userId) FROM Users "
            + "WHERE userRole = 'patient' AND createdAt >= ? AND createdAt <= ? AND deleted = false";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(newPatientSql)) {
      ps.setTimestamp(1, start);
      ps.setTimestamp(2, end);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          newPatientsCount = rs.getInt(1);
        }
      }
    }

    // Patients with consultations this month
    List<PatientVisitSummaryReport.PatientVisitItem> patientVisits = new ArrayList<>();
    String patientsSql =
        "SELECT p.patientId, u.userId, u.firstName, u.lastName, u.createdAt AS userCreatedAt, "
            + "       COUNT(c.consultationId) AS consultationsCount "
            + "FROM Patient p "
            + "JOIN Users u ON p.userId = u.userId "
            + "JOIN Appointment a ON p.patientId = a.patientId "
            + "JOIN Consultation c ON a.appointmentId = c.appointmentId "
            + "WHERE c.createdAt >= ? AND c.createdAt <= ? AND u.deleted = false "
            + "GROUP BY p.patientId, u.userId, u.firstName, u.lastName, u.createdAt "
            + "ORDER BY consultationsCount DESC, u.lastName ASC, u.firstName ASC";

    try (Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(patientsSql)) {
      ps.setTimestamp(1, start);
      ps.setTimestamp(2, end);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int userId = rs.getInt("userId");
          String patientName = rs.getString("firstName") + " " + rs.getString("lastName");
          int count = rs.getInt("consultationsCount");
          Timestamp userCreatedAt = rs.getTimestamp("userCreatedAt");

          boolean registeredThisMonth =
              userCreatedAt != null
                  && userCreatedAt.getTime() >= start.getTime()
                  && userCreatedAt.getTime() <= end.getTime();

          patientVisits.add(
              new PatientVisitSummaryReport.PatientVisitItem(
                  userId, patientName, count, registeredThisMonth));
        }
      }
    }

    return new PatientVisitSummaryReport(newPatientsCount, patientVisits);
  }
}
