package assignment.client.ui.screens;

import assignment.client.services.ServiceManager;
import assignment.client.ui.InputHandler;
import assignment.shared.dto.LoginResponse;
import assignment.shared.model.Schedule;
import java.sql.Time;
import java.util.List;

public class ManageScheduleScreen {

    public static void display(ServiceManager client, LoginResponse session) {

        while (true) {
            System.out.println("\n=== Manage Available Time ===");
            System.out.println("[1]. View Current Slots");
            System.out.println("[2]. Add New Availability Slot");
            System.out.println("[3]. Remove Availability Slot");
            System.out.println("[4]. Return to Doctor Menu");

            int choice = InputHandler.readInt("Select an option: ");

            if (choice == 1) {
                showSchedules(client, session.getUserId());
            } else if (choice == 2) {
                addNewSchedule(client, session.getUserId());
            } else if (choice == 3) {
                removeSchedule(client, session.getUserId());
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void showSchedules(
            ServiceManager client, int userIdThatWillBeCrosscheckedWithDoctorId) {
        try {
            List<Schedule> schedules =
                    client.getSchedulesByDoctor(userIdThatWillBeCrosscheckedWithDoctorId);
            System.out.println("\n--- Your Current Availability Slots ---");
            if (schedules == null || schedules.isEmpty()) {
                System.out.println("(No availability slots found)");
                return;
            }
            for (Schedule schedule : schedules) {
                System.out.println(
                        "ID: ["
                                + schedule.getScheduleId()
                                + "] | "
                                + schedule.getDay()
                                + " | "
                                + schedule.getStartTime()
                                + " - "
                                + schedule.getEndTime());
            }
        } catch (Exception e) {
            System.err.println("Error pulling availability schedules: " + e.getMessage());
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

    private static void removeSchedule(
            ServiceManager client, int userIdThatWillBeCrosscheckedWithDoctorId) {
        showSchedules(client, userIdThatWillBeCrosscheckedWithDoctorId);
        System.out.println("\n--- Remove Availability Slot ---");
        int scheduleId = InputHandler.readInt("Enter the Slot ID to delete (-1 To cancel): ");

        if (scheduleId == -1) {
            System.out.println("Operation cancelled.");
            return;
        }

        try {
            boolean success = client.deleteSchedule(scheduleId);
            if (success) {
                System.out.println("Availability slot removed successfully.");
            } else {
                System.out.println("Failed to remove slot");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
