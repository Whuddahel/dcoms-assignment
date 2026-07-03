package assignment.shared.config;

/**
 * Global configuration constants for the application. This class holds RMI settings and Database
 * configuration details shared or used by the client and server components.
 */
public final class Config {

  // Prevent instantiation
  private Config() {}

  // ==========================================
  // RMI Registry Constants
  // ==========================================
  public static final String SERVER_HOST = "localhost";
  public static final String DB_HOST = "localhost";
  public static final int SERVER_REGISTRY_PORT = 1099;
  public static final int DB_REGISTRY_PORT = 1040;

  // ==========================================
  // Database Connection Constants
  // ==========================================
  public static final String DB_URL = "jdbc:derby://" + DB_HOST + ":1527/appdb;create=true";
  public static final String DB_USER = "User";
  public static final String DB_PASSWORD = "Password";
}
