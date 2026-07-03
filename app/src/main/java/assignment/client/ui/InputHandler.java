package assignment.client.ui;

import java.util.Scanner;

/**
 * InputHandler owns the single shared Scanner for all CLI input.
 *
 * <p>All console-reading logic is centralised here so that:
 *
 * <ul>
 *   <li>Only one Scanner wraps System.in (avoids buffer conflicts).
 *   <li>Input validation and retry loops are not duplicated across menus.
 *   <li>Menu and screen classes are responsible only for display and routing.
 * </ul>
 *
 * <p>Do not create {@code new Scanner(System.in)} anywhere else in the project. Do not close this
 * Scanner; it wraps System.in which must remain open.
 */
public final class InputHandler {

  private static final Scanner scanner = new Scanner(System.in);

  // Prevent instantiation — all methods are static.
  private InputHandler() {}

  // =========================================================================
  // Core read methods
  // =========================================================================

  /**
   * Prompts the user until a non-empty input line is entered.
   *
   * @param prompt text printed before reading input
   * @return the trimmed, non-empty string the user entered
   */
  public static String readLine(String prompt) {
    return readLine(prompt, false);
  }

  /**
   * Prompts the user for input, optionally allowing empty input.
   *
   * @param prompt text printed before reading input
   * @param allowEmpty if true, empty inputs are allowed; if false, loops until a non-empty input is
   *     provided
   * @return the trimmed input string
   */
  public static String readLine(String prompt, boolean allowEmpty) {
    if (allowEmpty) {
      System.out.print(prompt);
      return scanner.nextLine().trim();
    }
    while (true) {
      System.out.print(prompt);
      String value = scanner.nextLine().trim();
      if (!value.isEmpty()) {
        return value;
      }
      System.out.println("Input cannot be empty. Please try again.");
    }
  }

  /**
   * Prompts the user until a valid integer is entered.
   *
   * <p>Prints an error message and re-prompts on non-numeric input.
   *
   * @param prompt text printed before each read attempt
   * @return the parsed integer value
   */
  public static int readInt(String prompt) {
    while (true) {
      System.out.print(prompt);
      try {
        return Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a whole number.");
      }
    }
  }

  /**
   * Prompts the user until a valid double is entered.
   *
   * @param prompt text printed before each read attempt
   * @return the parsed double value
   */
  public static double readDouble(String prompt) {
    while (true) {
      System.out.print(prompt);
      try {
        return Double.parseDouble(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a number with decimals (eg: 10.0).");
      }
    }
  }

  /**
   * Prompts the user for a yes/no answer.
   *
   * <p>Accepts {@code y}, {@code yes}, {@code n}, {@code no} (case-insensitive). Re-prompts on any
   * other input.
   *
   * @param prompt text printed before each read attempt
   * @return {@code true} if the user answered yes, {@code false} for no
   */
  public static boolean readYesNo(String prompt) {
    while (true) {
      System.out.print(prompt + " (y/n): ");
      String input = scanner.nextLine().trim().toLowerCase();
      if (input.equals("y") || input.equals("yes")) {
        return true;
      } else if (input.equals("n") || input.equals("no")) {
        return false;
      }
      System.out.println("Please enter 'y' or 'n'.");
    }
  }
}
