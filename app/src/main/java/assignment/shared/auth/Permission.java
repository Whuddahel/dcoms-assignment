package assignment.shared.auth;

/** Permissions for (typically) mutative functions */
public enum Permission {
  // * DEVELOPER NOTE: Please store new permissions in CRUD order if applicable
  // USER Domain
  USER_CREATE,
  USER_READ,
  USER_UPDATE,
  USER_DELETE,

  // APPOINTMENT Domain
  APPOINTMENT_CREATE,
  APPOINTMENT_READ,
  APPOINTMENT_UPDATE,
  APPOINTMENT_DELETE,

  // SCHEDULE Domain
  SCHEDULE_CREATE,
  SCHEDULE_READ,
  SCHEDULE_UPDATE,
  SCHEDULE_DELETE,

  // CONSULTATION Domain
  CONSULTATION_CREATE,
  CONSULTATION_READ,
  CONSULTATION_UPDATE,
  CONSULTATION_DELETE,

  // REPORT Domain
  REPORT_READ
}
