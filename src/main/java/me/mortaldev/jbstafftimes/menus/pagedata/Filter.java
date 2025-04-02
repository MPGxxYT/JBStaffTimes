package me.mortaldev.jbstafftimes.menus.pagedata;

public enum Filter {
  NON_ADMINS,
  ALL;

  public static Filter next(Filter filter) {
    return switch (filter) {
      case NON_ADMINS -> ALL;
      case ALL -> NON_ADMINS;
    };
  }
}
