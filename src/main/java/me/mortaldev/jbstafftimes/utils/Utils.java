package me.mortaldev.jbstafftimes.utils;

public class Utils {
  public static int clamp(int value, int min, int max) {
    return Math.max(min, Math.min(max, value));
  }

  public static String formatOrdinal(int number) {
    if (number % 100 >= 11 && number % 100 <= 13) {
      return number + "th";
    }
    return switch (number % 10) {
      case 1 -> number + "st";
      case 2 -> number + "nd";
      case 3 -> number + "rd";
      default -> number + "th";
    };
  }
}
