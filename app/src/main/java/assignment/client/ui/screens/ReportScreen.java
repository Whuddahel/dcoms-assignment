package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.shared.dto.DoctorConsultationReport;
import assignment.shared.dto.MonthlyAppointmentReport;
import assignment.shared.dto.PatientVisitSummaryReport;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportScreen {

  // ANSI Escape Codes for text styling
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
  private static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_BOLD = "\u001B[1m";

  public static void displayMonthlyAppointmentReport(ServiceManager client) {
    System.out.println("\n=== Generate Monthly Appointment Report ===");
    int[] date = promptForYearAndMonth();
    if (date == null) return;
    int year = date[0];
    int month = date[1];

    try {
      MonthlyAppointmentReport report = client.getMonthlyAppointmentReport(year, month);
      String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

      System.out.println(
          "\n" + ANSI_CYAN + "=============================================" + ANSI_RESET);
      System.out.printf(
          ANSI_BOLD + "APPOINTMENT REPORT FOR %s %d\n" + ANSI_RESET, monthName.toUpperCase(), year);
      System.out.println(ANSI_CYAN + "=============================================" + ANSI_RESET);
      System.out.printf("Total Appointments Made:     %d\n", report.getTotalMade());
      System.out.printf("Total Successful:            %d\n", report.getTotalSuccessful());
      System.out.printf("Total Cancelled:             %d\n", report.getTotalCancelled());
      System.out.printf("  - Cancelled by Doctor:     %d\n", report.getCancelledByDoctor());
      System.out.printf("  - Cancelled by Patient:    %d\n", report.getCancelledByPatient());
      System.out.println(ANSI_CYAN + "---------------------------------------------" + ANSI_RESET);

      System.out.println(
          "\n" + ANSI_YELLOW + "Top 10 Doctors by Successful Appointments:" + ANSI_RESET);
      if (report.getTopDoctors().isEmpty()) {
        System.out.println("(No successful appointments found for this month)");
      } else {
        String[] headers = {
          "No.",
          "Doctor Name (ID: {user_id})",
          "Successful Appointment",
          "Appointment Cancelled (by Doctor/Client)"
        };

        List<String[]> rows = new ArrayList<>();
        int index = 1;
        for (MonthlyAppointmentReport.DoctorAppointmentItem item : report.getTopDoctors()) {
          rows.add(
              new String[] {
                String.valueOf(index++),
                String.format("%s (ID: %d)", item.getDoctorName(), item.getUserId()),
                String.valueOf(item.getSuccessfulCount()),
                String.format(
                    "Doc: %d, Pat: %d", item.getCancelledByDoc(), item.getCancelledByPat())
              });
        }
        printTable(headers, rows);
      }

      InputHandler.readLine("\nPress Enter to return to menu...", true);
    } catch (Exception e) {
      System.err.println("Error generating report: " + e.getMessage());
    }
  }

  public static void displayDoctorConsultationReport(ServiceManager client) {
    System.out.println("\n=== Generate Doctor Consultation Report ===");
    int[] date = promptForYearAndMonth();
    if (date == null) return;
    int year = date[0];
    int month = date[1];

    try {
      DoctorConsultationReport report = client.getDoctorConsultationReport(year, month);
      String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

      System.out.println(
          "\n" + ANSI_CYAN + "=============================================" + ANSI_RESET);
      System.out.printf(
          ANSI_BOLD + "DOCTOR CONSULTATION REPORT FOR %s %d\n" + ANSI_RESET,
          monthName.toUpperCase(),
          year);
      System.out.println(ANSI_CYAN + "=============================================" + ANSI_RESET);
      System.out.printf("Total Consultations Made:    %d\n", report.getTotalConsultations());
      System.out.printf("Total Earnings:              RM %.2f\n", report.getTotalEarning());
      System.out.println(ANSI_CYAN + "---------------------------------------------" + ANSI_RESET);

      List<DoctorConsultationReport.DoctorConsultationItem> items = report.getDoctorConsultations();
      if (items.isEmpty()) {
        System.out.println("(No consultations found for this month)");
        InputHandler.readLine("\nPress Enter to return to menu...", true);
        return;
      }

      int currentPage = 0;
      int pageSize = 20;
      int totalItems = items.size();
      int totalPages = (int) Math.ceil((double) totalItems / pageSize);

      while (true) {
        System.out.println("\n" + ANSI_YELLOW + "Doctor Consultation List:" + ANSI_RESET);
        if (totalPages > 1) {
          System.out.printf("Page %d of %d\n", currentPage + 1, totalPages);
        }

        String[] headers = {
          "No.", "Doctor Name (ID: {user_id})", "Total Consultation Made", "Total Earning"
        };

        List<String[]> rows = new ArrayList<>();
        int startIdx = currentPage * pageSize;
        int endIdx = Math.min(startIdx + pageSize, totalItems);

        for (int i = startIdx; i < endIdx; i++) {
          DoctorConsultationReport.DoctorConsultationItem item = items.get(i);
          rows.add(
              new String[] {
                String.valueOf(i + 1),
                String.format("%s (ID: %d)", item.getDoctorName(), item.getUserId()),
                String.valueOf(item.getConsultationsCount()),
                String.format("RM %.2f", item.getTotalEarning())
              });
        }
        printTable(headers, rows);

        if (totalPages <= 1) {
          break;
        }

        System.out.println("\nNavigation settings:");
        System.out.println("  \"<\" or \">\" to change page");
        System.out.println("  \"back\" or press Enter to return to menu");
        String input = InputHandler.readLine("Input: ", true);
        if (input.equalsIgnoreCase("back") || input.isEmpty()) {
          break;
        } else if (input.equals("<")) {
          if (currentPage > 0) currentPage--;
          else System.out.println("Already on the first page.");
        } else if (input.equals(">")) {
          if (currentPage < totalPages - 1) currentPage++;
          else System.out.println("Already on the last page.");
        } else {
          System.out.println("Invalid input.");
        }
      }

      InputHandler.readLine("\nPress Enter to return to menu...", true);
    } catch (Exception e) {
      System.err.println("Error generating report: " + e.getMessage());
    }
  }

  public static void displayPatientVisitSummary(ServiceManager client) {
    System.out.println("\n=== Generate Patient Visit Summary ===");
    int[] date = promptForYearAndMonth();
    if (date == null) return;
    int year = date[0];
    int month = date[1];

    try {
      PatientVisitSummaryReport report = client.getPatientVisitSummaryReport(year, month);
      String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

      System.out.println(
          "\n" + ANSI_CYAN + "=============================================" + ANSI_RESET);
      System.out.printf(
          ANSI_BOLD + "PATIENT VISIT SUMMARY FOR %s %d\n" + ANSI_RESET,
          monthName.toUpperCase(),
          year);
      System.out.println(ANSI_CYAN + "=============================================" + ANSI_RESET);
      System.out.printf("New Patients Registered:     %d\n", report.getNewPatientsCount());
      System.out.println(ANSI_CYAN + "---------------------------------------------" + ANSI_RESET);

      System.out.println(
          "\n" + ANSI_YELLOW + "Patient Visit List (Consultations Made):" + ANSI_RESET);
      if (report.getPatientVisits().isEmpty()) {
        System.out.println("(No patients had consultations this month)");
      } else {
        String[] headers = {"No.", "Patient Name - (ID: {user_id})", "Consultations Made"};
        List<String[]> rows = new ArrayList<>();
        int index = 1;
        for (PatientVisitSummaryReport.PatientVisitItem item : report.getPatientVisits()) {
          String nameDisplay =
              String.format("%s - (ID: %d)", item.getPatientName(), item.getUserId());
          if (item.isRegisteredThisMonth()) {
            nameDisplay = ANSI_GREEN + nameDisplay + " (New)" + ANSI_RESET;
          }
          rows.add(
              new String[] {
                String.valueOf(index++), nameDisplay, String.valueOf(item.getConsultationsCount())
              });
        }
        printTable(headers, rows);
      }

      InputHandler.readLine("\nPress Enter to return to menu...", true);
    } catch (Exception e) {
      System.err.println("Error generating report: " + e.getMessage());
    }
  }

  private static int[] promptForYearAndMonth() {
    LocalDate now = LocalDate.now();
    int year = now.getYear();

    while (true) {
      String input =
          InputHandler.readLine(
              "Enter month (1-12, name e.g. 'jan' / 'january', 'current', or 'back' to return): ");
      if (input.equalsIgnoreCase("back")) {
        return null;
      }
      try {
        int month = parseMonth(input);
        return new int[] {year, month};
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid input: " + e.getMessage());
      }
    }
  }

  public static int parseMonth(String input) throws IllegalArgumentException {
    input = input.trim().toLowerCase();
    if (input.equals("current")) {
      return LocalDate.now().getMonthValue();
    }
    try {
      int month = Integer.parseInt(input);
      if (month >= 1 && month <= 12) {
        return month;
      }
    } catch (NumberFormatException e) {
      // Ignore and parse as string name
    }
    switch (input) {
      case "jan":
      case "january":
        return 1;
      case "feb":
      case "february":
        return 2;
      case "mar":
      case "march":
        return 3;
      case "apr":
      case "april":
        return 4;
      case "may":
        return 5;
      case "jun":
      case "june":
        return 6;
      case "jul":
      case "july":
        return 7;
      case "aug":
      case "august":
        return 8;
      case "sep":
      case "september":
        return 9;
      case "oct":
      case "october":
        return 10;
      case "nov":
      case "november":
        return 11;
      case "dec":
      case "december":
        return 12;
      default:
        throw new IllegalArgumentException("Unsupported month: " + input);
    }
  }

  private static void printTable(String[] headers, List<String[]> rows) {
    int cols = headers.length;
    int[] colWidths = new int[cols];
    for (int i = 0; i < cols; i++) {
      colWidths[i] = headers[i].length();
    }
    for (String[] row : rows) {
      for (int i = 0; i < cols; i++) {
        if (row[i] != null) {
          String cleanStr = stripAnsi(row[i]);
          colWidths[i] = Math.max(colWidths[i], cleanStr.length());
        }
      }
    }

    // Print headers
    StringBuilder headerLine = new StringBuilder();
    for (int i = 0; i < cols; i++) {
      headerLine.append(padRight(headers[i], colWidths[i]));
      if (i < cols - 1) headerLine.append(" | ");
    }
    System.out.println(ANSI_BOLD + headerLine.toString() + ANSI_RESET);

    // Print separator
    StringBuilder separator = new StringBuilder();
    for (int i = 0; i < cols; i++) {
      separator.append(repeat("-", colWidths[i]));
      if (i < cols - 1) separator.append("-+-");
    }
    System.out.println(separator.toString());

    // Print rows
    for (String[] row : rows) {
      StringBuilder rowLine = new StringBuilder();
      for (int i = 0; i < cols; i++) {
        String val = row[i] != null ? row[i] : "";
        rowLine.append(padRightWithAnsi(val, colWidths[i]));
        if (i < cols - 1) rowLine.append(" | ");
      }
      System.out.println(rowLine.toString());
    }
  }

  private static String stripAnsi(String str) {
    return str.replaceAll("\\u001B\\[[;\\d]*m", "");
  }

  private static String padRight(String s, int n) {
    return String.format("%-" + n + "s", s);
  }

  private static String padRightWithAnsi(String s, int n) {
    int rawLength = stripAnsi(s).length();
    int ansiLength = s.length() - rawLength;
    return String.format("%-" + (n + ansiLength) + "s", s);
  }

  private static String repeat(String s, int count) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append(s);
    }
    return sb.toString();
  }
}
