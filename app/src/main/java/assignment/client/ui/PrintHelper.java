package assignment.client.ui;

import assignment.shared.dto.AppointmentView;
import assignment.shared.dto.ConsultationView;
import assignment.shared.model.Doctor;
import assignment.shared.model.Patient;
import assignment.shared.model.Schedule;
import assignment.shared.model.User;
import java.util.List;

public class PrintHelper {

  public static class Paginator<T> {
    private final List<T> items;
    private final int pageSize;
    private int currentPage;

    public Paginator(List<T> items, int pageSize) {
      this.items = items;
      this.pageSize = pageSize;
      this.currentPage = 0;
    }

    public List<T> getCurrentPageItems() {
      int startIdx = currentPage * pageSize;
      int endIdx = Math.min(startIdx + pageSize, items.size());
      if (startIdx >= items.size()) {
        return List.of();
      }
      return items.subList(startIdx, endIdx);
    }

    public int getCurrentPage() {
      return currentPage;
    }

    public int getTotalPages() {
      if (items.isEmpty()) return 1;
      return (int) Math.ceil((double) items.size() / pageSize);
    }

    public boolean hasNext() {
      return currentPage < getTotalPages() - 1;
    }

    public boolean hasPrev() {
      return currentPage > 0;
    }

    public void nextPage() {
      if (hasNext()) {
        currentPage++;
      }
    }

    public void prevPage() {
      if (hasPrev()) {
        currentPage--;
      }
    }

    public void printPageInfo() {
      System.out.printf("Page %d of %d\n", currentPage + 1, getTotalPages());
    }

    public int getStartIndex() {
      return currentPage * pageSize;
    }

    public int getTotalItems() {
      return items.size();
    }
  }

  public static String printAppointmentList(
      List<AppointmentView> appointments, int startIdx, String prompt) {
    for (int i = 0; i < appointments.size(); i++) {
      AppointmentView a = appointments.get(i);
      String statusColor =
          a.getStatus().equalsIgnoreCase("Cancelled")
              ? Helper.getColorCode(Helper.Theme.RED)
              : Helper.getColorCode(Helper.Theme.GREEN);

      StringBuilder sb = new StringBuilder();
      sb.append(String.format("[%d]. %s", startIdx + i + 1, a.getAppointmentDate()));

      if (a.getDoctorName() != null && !a.getDoctorName().isEmpty()) {
        sb.append(String.format(" | Dr. %s", a.getDoctorName()));
      }
      if (a.getPatientName() != null && !a.getPatientName().isEmpty()) {
        sb.append(String.format(" | %s", a.getPatientName()));
      }

      sb.append(
          String.format(
              " | %s | %s%s%s",
              a.getTimeSlot(),
              statusColor,
              a.getStatus(),
              Helper.getColorCode(Helper.Theme.RESET)));

      System.out.println(sb.toString());
    }

    String input = InputHandler.readLine(prompt, true);
    try {
      int idx = Integer.parseInt(input);
      int listIdx = idx - startIdx - 1;
      if (listIdx >= 0 && listIdx < appointments.size()) {
        return String.valueOf(appointments.get(listIdx).getAppointmentId());
      }
    } catch (NumberFormatException e) {
      // fall through and return raw input
    }
    return input;
  }

  public static String printScheduleList(List<Schedule> schedules, int startIdx, String prompt) {
    for (int i = 0; i < schedules.size(); i++) {
      Schedule s = schedules.get(i);
      System.out.printf(
          "[%d]. %s | %s - %s\n", startIdx + i + 1, s.getDay(), s.getStartTime(), s.getEndTime());
    }

    String input = InputHandler.readLine(prompt, true);
    try {
      int idx = Integer.parseInt(input);
      int listIdx = idx - startIdx - 1;
      if (listIdx >= 0 && listIdx < schedules.size()) {
        return String.valueOf(schedules.get(listIdx).getScheduleId());
      }
    } catch (NumberFormatException e) {
      // fall through and return raw input
    }
    return input;
  }

  public static String printUserList(List<User> users, int startIdx, String prompt) {
    for (int i = 0; i < users.size(); i++) {
      User u = users.get(i);
      System.out.printf(
          "[%d]. %s - %s (%s)\n",
          startIdx + i + 1, u.getUserRole().toUpperCase(), u.getFullName(), u.getEmail());
    }

    String input = InputHandler.readLine(prompt, true);
    try {
      int idx = Integer.parseInt(input);
      int listIdx = idx - startIdx - 1;
      if (listIdx >= 0 && listIdx < users.size()) {
        return String.valueOf(users.get(listIdx).getUserId());
      }
    } catch (NumberFormatException e) {
      // fall through and return raw input
    }
    return input;
  }

  public static String printDoctorList(List<Doctor> doctors, int startIdx, String prompt) {
    for (int i = 0; i < doctors.size(); i++) {
      Doctor d = doctors.get(i);
      System.out.printf(
          "[%d]. Dr. %s %s - %s\n",
          startIdx + i + 1, d.getFirstName(), d.getLastName(), d.getSpecialization());
    }

    String input = InputHandler.readLine(prompt, true);
    try {
      int idx = Integer.parseInt(input);
      int listIdx = idx - startIdx - 1;
      if (listIdx >= 0 && listIdx < doctors.size()) {
        return String.valueOf(doctors.get(listIdx).getDoctorId());
      }
    } catch (NumberFormatException e) {
      // fall through and return raw input
    }
    return input;
  }

  public static String printPatientList(List<Patient> patients, int startIdx, String prompt) {
    for (int i = 0; i < patients.size(); i++) {
      Patient p = patients.get(i);
      System.out.printf(
          "[%d]. %s %s - %s\n",
          startIdx + i + 1, p.getFirstName(), p.getLastName(), p.getContactNumber());
    }

    String input = InputHandler.readLine(prompt, true);
    try {
      int idx = Integer.parseInt(input);
      int listIdx = idx - startIdx - 1;
      if (listIdx >= 0 && listIdx < patients.size()) {
        return String.valueOf(patients.get(listIdx).getPatientId());
      }
    } catch (NumberFormatException e) {
      // fall through and return raw input
    }
    return input;
  }

  public static String printConsultationList(
      List<ConsultationView> consultations, int startIdx, String prompt) {
    for (int i = 0; i < consultations.size(); i++) {
      ConsultationView c = consultations.get(i);

      StringBuilder sb = new StringBuilder();
      sb.append(String.format("[%d]. ", startIdx + i + 1));

      if (c.getDate() != null && !c.getDate().isEmpty()) {
        sb.append(String.format("%s | ", c.getDate()));
      }

      sb.append(String.format("Appt ID: %d | Fee: $%.2f", c.getAppointmentId(), c.getFee()));

      if (c.getDoctorName() != null && !c.getDoctorName().isEmpty()) {
        sb.append(String.format(" | Dr. %-20s", c.getDoctorName()));
      }
      if (c.getPatientName() != null && !c.getPatientName().isEmpty()) {
        sb.append(String.format(" | Patient: %-20s", c.getPatientName()));
      }
      if (c.getNotes() != null && !c.getNotes().isEmpty()) {
        sb.append(String.format(" | Notes: %s", c.getNotes()));
      }

      System.out.println(sb.toString());
    }

    String input = InputHandler.readLine(prompt, true);
    try {
      int idx = Integer.parseInt(input);
      int listIdx = idx - startIdx - 1;
      if (listIdx >= 0 && listIdx < consultations.size()) {
        return String.valueOf(consultations.get(listIdx).getConsultationId());
      }
    } catch (NumberFormatException e) {
      // fall through and return raw input
    }
    return input;
  }
}
