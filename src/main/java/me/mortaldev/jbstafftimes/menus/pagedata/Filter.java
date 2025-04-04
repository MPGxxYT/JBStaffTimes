package me.mortaldev.jbstafftimes.menus.pagedata;

public enum Filter {
  NON_ADMINS,
  ADMINS,
  ALL;

  public static Filter next(Filter filter) {
    return switch (filter) {
      case NON_ADMINS -> ADMINS;
      case ADMINS -> ALL;
      case ALL -> NON_ADMINS;
    };
  }
}
