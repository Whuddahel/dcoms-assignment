package assignment.server.auth;

import assignment.shared.auth.Permission;
import assignment.shared.error.AuthError;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class AuthorizationManager {
  private static final Map<String, Permission> endpointPermissions = new HashMap<>();

  static {
    // USER Domain
    endpointPermissions.put("registerUser", Permission.USER_CREATE);
    endpointPermissions.put("editUser", Permission.USER_UPDATE);
    endpointPermissions.put("deleteUser", Permission.USER_DELETE);
    endpointPermissions.put("getAllUsers", Permission.USER_READ);
    endpointPermissions.put("getPatientByUserId", Permission.USER_READ);
    endpointPermissions.put("updatePatientProfile", Permission.USER_UPDATE);
    endpointPermissions.put("getAllDoctors", Permission.USER_READ);
    endpointPermissions.put("getDoctorById", Permission.USER_READ);
    endpointPermissions.put("getPatientById", Permission.USER_READ);
    endpointPermissions.put("getPatientsWithConsultations", Permission.USER_READ);

    // APPOINTMENT Domain
    endpointPermissions.put("getUpcomingAppointmentsByDoctorUserId", Permission.APPOINTMENT_READ);
    endpointPermissions.put(
        "getAppointmentsWithConsultationByDoctorAndPatient", Permission.APPOINTMENT_READ);
    endpointPermissions.put("cancelAppointment", Permission.APPOINTMENT_UPDATE);
    endpointPermissions.put("getUpcomingAppointments", Permission.APPOINTMENT_READ);
    endpointPermissions.put("getPastAppointments", Permission.APPOINTMENT_READ);
    endpointPermissions.put("bookAppointment", Permission.APPOINTMENT_CREATE);
    endpointPermissions.put("getAppointmentsByDoctorAndDate", Permission.APPOINTMENT_READ);

    // SCHEDULE Domain
    endpointPermissions.put("getScheduleById", Permission.SCHEDULE_READ);
    endpointPermissions.put("getSchedulesByDoctorId", Permission.SCHEDULE_READ);
    endpointPermissions.put("addSchedule", Permission.SCHEDULE_CREATE);
    endpointPermissions.put("getSchedulesByDoctor", Permission.SCHEDULE_READ);
    endpointPermissions.put("deleteSchedule", Permission.SCHEDULE_DELETE);

    // CONSULTATION Domain
    endpointPermissions.put("getConsultationByAppointmentId", Permission.CONSULTATION_READ);
    endpointPermissions.put("getConsultationsByPatient", Permission.CONSULTATION_READ);
    endpointPermissions.put("addConsultation", Permission.CONSULTATION_CREATE);
    endpointPermissions.put("getAllConsultations", Permission.CONSULTATION_READ);
    endpointPermissions.put("updateConsultation", Permission.CONSULTATION_UPDATE);

    // REPORT Domain
    endpointPermissions.put("getMonthlyAppointmentReport", Permission.REPORT_READ);
    endpointPermissions.put("getDoctorConsultationReport", Permission.REPORT_READ);
    endpointPermissions.put("getPatientVisitSummaryReport", Permission.REPORT_READ);
  }

  private AuthorizationManager() {}

  public static Session requirePermissions(String token, String actionName) throws RemoteException {
    Session session = SessionManager.getSession(token);

    if (session == null) throw new RemoteException(AuthError.INVALID_SESSION.name());

    Permission requiredPermission = endpointPermissions.get(actionName);

    if (requiredPermission != null && !session.getRole().hasPermission(requiredPermission)) {
      throw new RemoteException(AuthError.ACCESS_DENIED.name());
    }

    return session;
  }

  public static Session requirePermissions(String token, Permission permission)
      throws RemoteException {
    Session session = SessionManager.getSession(token);

    if (session == null) throw new RemoteException(AuthError.INVALID_SESSION.name());

    if (!session.getRole().hasPermission(permission))
      throw new RemoteException(AuthError.ACCESS_DENIED.name());

    return session;
  }
}
