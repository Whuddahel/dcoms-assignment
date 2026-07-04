package assignment.shared.auth;

import java.util.EnumSet;
import java.util.Set;

/** Enum of available roles for Users */
public enum Role {
  ADMIN(EnumSet.allOf(Permission.class)),

  DOCTOR(EnumSet.of(Permission.USER_READ)),

  RECEPTIONIST(EnumSet.of(Permission.USER_READ)),

  PATIENT(EnumSet.of(Permission.USER_READ));

  private final Set<Permission> permissions;

  Role(Set<Permission> permissions) {
    this.permissions = permissions;
  }

  public boolean hasPermission(Permission permission) {
    return permissions.contains(permission);
  }

  public static Role databaseToEnum(String roleStr) {
    return Role.valueOf(roleStr.toUpperCase());
  }
}
