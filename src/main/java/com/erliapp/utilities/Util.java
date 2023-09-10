package com.erliapp.utilities;

/** General Utility Class. */
public class Util {

  /**
   * Checks to see if a String is Numeric.
   *
   * @param string String to check if it's numeric
   * @return {@code true} if the String is a number. {@code false} if it is not a number.
   */
  public static boolean isNumeric(String string) {
    // System.out.println(String.format("Parsing string: \"%s\"", string));
    if (string == null || string.equals("")) {
      // System.out.println("String cannot be parsed, it is null or empty.");
      return false;
    }
    try {
      Long.parseLong(string);
      return true;
    } catch (NumberFormatException e) {
      // System.out.println("Input String cannot be parsed to Integer.");
      return false;
    }
  }
}
