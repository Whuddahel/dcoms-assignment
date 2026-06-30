package assignment.shared.auth;

import java.util.EnumSet;
import java.util.Set;

/** Enum of available roles for Users */
public enum Role {
  ADMIN(EnumSet.allOf(Permission.class)),

  DOCTOR(EnumSet.of(Permission.USER_READ)),

  RECEPTIONIST(EnumSet.of(Permission.USER_READ));

  private final Set<Permission> permissions;

  Role(Set<Permission> permissions) {
    this.permissions = permissions;
  }

  public boolean hasPermission(Permission permission) {
    return permissions.contains(permission);
  }
}
