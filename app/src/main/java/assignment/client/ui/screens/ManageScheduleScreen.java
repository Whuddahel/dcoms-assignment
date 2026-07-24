package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.Helper;
import assignment.client.ui.InputHandler;
import assignment.client.ui.PrintHelper;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Schedule;
import java.sql.Time;
import java.util.List;

public class ManageScheduleScreen {

  public static void display(ServiceManager client, LoginResponse session) {
    while (true) {
      System.out.println("\n=== Manage Available Time ===");
      List<Schedule> schedules = null;
      try {
        schedules = client.getSchedulesByDoctor(session.getUserId());
        if (schedules != null) {
          java.util.Collections.sort(schedules);
        }
      } catch (Exception e) {
        System.err.println("Error pulling schedules: " + e.getMessage());
      }

      if (schedules == null || schedules.isEmpty()) {
        System.out.println("(No availability slots found)");
        System.out.println("\n[1]. Add New Availability Slot");
        System.out.println("[2]. Return to Doctor Menu");
        String input = InputHandler.readLine("Select an option: ", true);
        if (input.equals("1")) {
          addNewSchedule(client, session.getUserId());
        } else if (input.equals("2")) {
          break;
        } else {
          System.out.println("Invalid choice. Please try again.");
        }
      } else {
        String input =
            PrintHelper.printScheduleList(
                schedules,
                0,
                "Enter slot index to manage, 'add' to add new, or 'back' to return: ");
        if (input.equalsIgnoreCase("back")) {
          break;
        } else if (input.equalsIgnoreCase("add")) {
          addNewSchedule(client, session.getUserId());
        } else {
          try {
            int scheduleId = Integer.parseInt(input);
            manageSingleSchedule(client, schedules, scheduleId);
          } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please try again.");
          }
        }
      }
    }
  }

  private static void manageSingleSchedule(
      ServiceManager client, List<Schedule> schedules, int scheduleId) {
    Schedule selected = null;
    for (Schedule s : schedules) {
      if (s.getScheduleId() == scheduleId) {
        selected = s;
        break;
      }
    }
    if (selected == null) {
      System.out.println("Invalid slot selected.");
      return;
    }

    System.out.println("\n--- Selected Slot ---");
    System.out.printf("Day: %s\n", selected.getDay());
    System.out.printf("Time: %s - %s\n", selected.getStartTime(), selected.getEndTime());

    System.out.println("\nDo you want to delete this availability slot?");
    System.out.println("[1]. Back");
    Helper.printLine("[2]. Delete Slot", Helper.Theme.RED);

    int choice = InputHandler.readInt("Select an option: ");
    if (choice == 2) {
      try {
        boolean success = client.deleteSchedule(scheduleId);
        if (success) {
          System.out.println("Availability slot deleted successfully.");
        } else {
          System.out.println("Failed to delete slot.");
        }
      } catch (Exception e) {
        System.err.println("Error removing slot: " + e.getMessage());
      }
    } else if (choice != 1) {
      System.out.println("Invalid choice.");
    }
  }

  private static void addNewSchedule(
      ServiceManager client, int userIdToBeLaterConvertedToDoctorID) {
    System.out.println("\n--- Add New Availability ---");

    String day = InputHandler.readLine("Enter Day (e.g., Monday): ");
    String standardizedDay = day.trim().toUpperCase();
    List<String> validDays =
        List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
    if (!validDays.contains(standardizedDay)) {
      System.out.println(
          "Invalid day! Please enter a valid day of the week (e.g., Monday, Tuesday).");
      return;
    }

    String startStr = InputHandler.readLine("Enter Start Time (HH:MM:SS): ");
    String endStr = InputHandler.readLine("Enter End Time (HH:MM:SS): ");

    try {
      Time startTime = Time.valueOf(startStr);
      Time endTime = Time.valueOf(endStr);

      if (!startTime.before(endTime)) {
        System.out.println("Error: Start time must be before end time.");
        return;
      }

      List<Schedule> existingSchedules =
          client.getSchedulesByDoctor(userIdToBeLaterConvertedToDoctorID);
      if (existingSchedules != null) {
        for (Schedule existing : existingSchedules) {
          if (existing.getDay().equalsIgnoreCase(standardizedDay)) {
            if (startTime.before(existing.getEndTime())
                && existing.getStartTime().before(endTime)) {
              System.out.println(
                  "Error: The new schedule clashes with an existing slot ("
                      + existing.getStartTime()
                      + " - "
                      + existing.getEndTime()
                      + ").");
              return;
            }
          }
        }
      }

      Schedule schedule =
          new Schedule(0, userIdToBeLaterConvertedToDoctorID, standardizedDay, startTime, endTime);
      boolean success = client.addSchedule(schedule);

      if (success) {
        System.out.println("Availability slot added successfully!");
      } else {
        System.out.println("Failed to add availability slot.");
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid time format! Please use HH:MM:SS format.");
    } catch (Exception e) {
      System.err.println("Error adding schedule entry: " + e.getMessage());
    }
  }
}
