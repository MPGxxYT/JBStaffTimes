package me.mortaldev.jbstafftimes.modules.stafftime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.UUID;
import me.mortaldev.crudapi.CRUD;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.menus.pagedata.Filter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StaffTime implements CRUD.Identifiable {
  private final UUID uuid;
  private HashMap<String, Long> times;
  // date, time in minutes
  private HashMap<String, Long> afkTime = new HashMap<>();
  private HashMap<String, Long> afkFlags = new HashMap<>();
  private Filter filter;

  public StaffTime(UUID uuid) {
    this.uuid = uuid;
    this.filter = Filter.NON_ADMINS;
    this.times = new HashMap<>();
  }

  public HashMap<String, Long> getTimes() {
    return times;
  }

  public void setTimes(HashMap<String, Long> times) {
    this.times = times;
  }

  public void removeTimesDate(LocalDate date) {
    times.remove(localDateToString(date));
  }

  public HashMap<String, Long> getAfkTime() {
    return afkTime;
  }

  public void setAfkTime(HashMap<String, Long> afkTime) {
    this.afkTime = afkTime;
  }

  public void removeAfkTimeDate(LocalDate date) {
    afkTime.remove(localDateToString(date));
  }

  public HashMap<String, Long> getAfkFlags() {
    return afkFlags;
  }

  public void setAfkFlags(HashMap<String, Long> afkFlags) {
    this.afkFlags = afkFlags;
  }

  public void removeAfkFlagDate(LocalDate date) {
    afkFlags.remove(localDateToString(date));
  }

  public void addAFKFlag(LocalDate date) {
    if (afkFlags.containsKey(localDateToString(date))) {
      long summedFlags = afkFlags.get(localDateToString(date)) + 1;
      afkFlags.put(localDateToString(date), summedFlags);
    } else {
      afkFlags.put(localDateToString(date), 1L);
    }
  }

  public Long getAfkFlagsOfWeek(LocalDate date) {
    Long flagsTotal = wholeWeekOf(date, afkFlags);
    if (flagsTotal == null) {
      return 0L;
    }
    return flagsTotal;
  }

  public void addTime(LocalDate date, long time) {
    if (times.containsKey(localDateToString(date))) {
      long summedTime = times.get(localDateToString(date)) + time;
      times.put(localDateToString(date), summedTime);
    } else {
      times.put(localDateToString(date), time);
    }
    StaffTimeManager.getInstance().update(this);
  }

  public void addAFKTime(LocalDate date, long time) {
    if (afkTime.containsKey(localDateToString(date))) {
      long summedTime = afkTime.get(localDateToString(date)) + time;
      afkTime.put(localDateToString(date), summedTime);
    } else {
      afkTime.put(localDateToString(date), time);
    }
    StaffTimeManager.getInstance().update(this);
  }

  public void checkPermission() {
    Player player = Bukkit.getPlayer(uuid);
    Filter lastFilter = filter;
    if (player != null) {
      if (player.hasPermission(MainConfig.getInstance().getAdminPermission())) {
        filter = Filter.ADMINS;
      } else if (player.hasPermission(MainConfig.getInstance().getStaffPermission())) {
        filter = Filter.NON_ADMINS;
      }
    }
    if (filter != lastFilter) {
      StaffTimeManager.getInstance().update(this);
    }
  }

  public Filter getFilter() {
    return filter;
  }

  public Long getTotalTime() {
    Long totalTime = 0L;
    for (Long time : times.values()) {
      totalTime += time;
    }
    return totalTime;
  }

  /**
   * Returns the total time of the week containing the given date.
   *
   * @param date the date
   * @return the total time of the week in miliseconds
   */
  public Long getTimeOfWholeWeek(LocalDate date) {
    return wholeWeekOf(date, times);
  }

  /**
   * Returns the total AFK time of the week containing the given date.
   *
   * @param date the date
   * @return the total AFK time of the week in miliseconds
   */
  public Long getAfkOfWholeWeek(LocalDate date) {
    return wholeWeekOf(date, afkTime);
  }

  private Long wholeWeekOf(LocalDate date, HashMap<String, Long> times) {
    LocalDate sunday = StaffTimeManager.getInstance().getSundayOf(date);
    Long totalTime = 0L;
    for (int i = 0; i < 7; i++) {
      if (times.containsKey(localDateToString(sunday))) {
        totalTime += times.get(localDateToString(sunday));
      }
      sunday = sunday.plusDays(1);
    }
    return totalTime;
  }

  public String localDateToString(LocalDate date) {
    return date.format(DateTimeFormatter.ISO_DATE);
  }

  public LocalDate localDateFromString(String dateString) {
    return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
  }

  @Override
  public String getID() {
    return uuid.toString();
  }
}
