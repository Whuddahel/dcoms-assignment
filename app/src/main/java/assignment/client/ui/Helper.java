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
}
