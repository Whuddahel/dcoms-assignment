package assignment.shared.model;

import java.io.Serializable;
import java.sql.Time;

public class Schedule implements Serializable {
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
  public String toString() {
    return String.format(
        "Schedule [scheduleId=%d, doctorId=%d, day=%s, startTime=%s, endTime=%s]",
        scheduleId, doctorId, day, startTime, endTime);
  }
}
