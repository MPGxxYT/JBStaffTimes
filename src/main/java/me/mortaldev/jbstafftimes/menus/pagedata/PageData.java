package me.mortaldev.jbstafftimes.menus.pagedata;

import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.utils.Utils;

public class PageData {
  private String searchQuery;
  private Filter filter;
  private int weekNumber;

  public PageData(int weekNumber) {
    this.weekNumber = weekNumber;
    this.searchQuery = null;
    this.filter = Filter.NON_ADMINS;
  }

  public String getSearchQuery() {
    return searchQuery;
  }

  public void setSearchQuery(String searchQuery) {
    this.searchQuery = searchQuery;
  }

  public Filter getFilter() {
    return filter;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public int getWeekNumber() {
    return weekNumber;
  }

  public void setWeekNumber(int weekNumber) {
    this.weekNumber = Utils.clamp(weekNumber, 1, MainConfig.getInstance().getSavingLength());
  }
}
