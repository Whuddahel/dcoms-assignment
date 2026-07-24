package assignment.shared.model;

import java.io.Serializable;
import java.sql.Time;

public class Schedule implements Serializable, Comparable<Schedule> {
  private final int scheduleId;
  private final int doctorId;
  private final String day;
  private final Time startTime;
  private final Time endTime;

  public Schedule(int scheduleId, int doctorId, String day, Time startTime, Time endTime) {
    this.scheduleId = scheduleId;
    this.doctorId = doctorId;
    this.day = day;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Schedule(int doctorId, String day, Time startTime, Time endTime) {
    this(0, doctorId, day, startTime, endTime);
  }

  public int getScheduleId() {
    return scheduleId;
  }

  public int getDoctorId() {
    return doctorId;
  }

  public String getDay() {
    return day;
  }

  public Time getStartTime() {
    return startTime;
  }

  public Time getEndTime() {
    return endTime;
  }

  @Override
  public int compareTo(Schedule other) {
    int day1 = getDayValue(this.day);
    int day2 = getDayValue(other.day);
    if (day1 != day2) {
      return Integer.compare(day1, day2);
    }
    if (this.startTime != null && other.startTime != null) {
      return this.startTime.compareTo(other.startTime);
    }
    return 0;
  }

  private int getDayValue(String d) {
    if (d == null) return 8;
    return switch (d.trim().toUpperCase()) {
      case "MONDAY" -> 1;
      case "TUESDAY" -> 2;
      case "WEDNESDAY" -> 3;
      case "THURSDAY" -> 4;
      case "FRIDAY" -> 5;
      case "SATURDAY" -> 6;
      case "SUNDAY" -> 7;
      default -> 8;
    };
  }

  @Override
  public String toString() {
    return String.format(
        "Schedule [scheduleId=%d, doctorId=%d, day=%s, startTime=%s, endTime=%s]",
        scheduleId, doctorId, day, startTime, endTime);
  }
}
