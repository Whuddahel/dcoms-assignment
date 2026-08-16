package assignment.client.ui;

import java.util.EnumMap;
import java.util.Map;

public class Helper {

  public enum Theme {
    GREEN,
    BLUE,
    RED,
    YELLOW,
    CYAN,
    PURPLE,
    RESET
  }

  private static final Map<Theme, String> COLOR_MAP = new EnumMap<>(Theme.class);
  private static final String ANSI_RESET = "\u001B[0m";

  static {
    COLOR_MAP.put(Theme.GREEN, "\u001B[32m");
    COLOR_MAP.put(Theme.BLUE, "\u001B[34m");
    COLOR_MAP.put(Theme.RED, "\u001B[31m");
    COLOR_MAP.put(Theme.YELLOW, "\u001B[33m");
    COLOR_MAP.put(Theme.CYAN, "\u001B[36m");
    COLOR_MAP.put(Theme.PURPLE, "\u001B[35m");
    COLOR_MAP.put(Theme.RESET, ANSI_RESET);
  }

  public static String getColorCode(Theme color) {
    return COLOR_MAP.getOrDefault(color, ANSI_RESET);
  }

  public static void printBanner(String content, Theme color) {
    String colorCode = getColorCode(color);
    int requiredLength = content.length() + 8; // At least 4 spaces on each side
    int totalLength = Math.max(37, requiredLength);
    int leftPadding = (totalLength - content.length()) / 2;

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < leftPadding; i++) {
      sb.append(" ");
    }
    sb.append(content);
    while (sb.length() < totalLength) {
      sb.append(" ");
    }

    StringBuilder equalsBuilder = new StringBuilder();
    for (int i = 0; i < totalLength; i++) {
      equalsBuilder.append("=");
    }
    String equalsLine = equalsBuilder.toString();

    System.out.println(colorCode + equalsLine + ANSI_RESET);
    System.out.println(colorCode + sb.toString() + ANSI_RESET);
    System.out.println(colorCode + equalsLine + ANSI_RESET);
  }

  public static void printOption(int number, String content, Theme color) {
    String colorCode = getColorCode(color);
    System.out.println(colorCode + "[" + number + "].\u001B[0m " + content);
  }

  public static void printLine(String content, Theme color) {
    String colorCode = getColorCode(color);
    System.out.println(colorCode + content + ANSI_RESET);
  }

  public static String extractUserMessage(Throwable t) {
    if (t == null) {
      return "An unexpected error occurred.";
    }
    Throwable cause = t;
    while (cause.getCause() != null && cause.getCause() != cause) {
      cause = cause.getCause();
    }
    String msg = cause.getMessage();
    if (msg == null || msg.trim().isEmpty()) {
      msg = t.getMessage();
    }
    if (msg == null || msg.trim().isEmpty()) {
      return "An unexpected error occurred.";
    }

    int nestedIdx = msg.lastIndexOf("nested exception is:");
    if (nestedIdx != -1) {
      msg = msg.substring(nestedIdx + "nested exception is:".length()).trim();
    }
    if (msg.contains("Exception: ")) {
      msg = msg.substring(msg.lastIndexOf("Exception: ") + "Exception: ".length()).trim();
    }
    if (msg.startsWith("DB_ERROR:")) {
      msg = msg.substring("DB_ERROR:".length()).trim();
    } else if (msg.startsWith("SERVER_ERROR:")) {
      msg = msg.substring("SERVER_ERROR:".length()).trim();
    } else if (msg.startsWith("AUTH_ERROR:")) {
      msg = msg.substring("AUTH_ERROR:".length()).trim();
    } else if (msg.startsWith("CANNOT_DELETE:")) {
      msg = msg.substring("CANNOT_DELETE:".length()).trim();
    }

    return msg.trim();
  }

  public static void printError(String prefix, Throwable t) {
    String cleanMsg = extractUserMessage(t);
    String output =
        (prefix != null && !prefix.trim().isEmpty()) ? prefix + ": " + cleanMsg : cleanMsg;
    printLine(output, Theme.RED);
  }
}
