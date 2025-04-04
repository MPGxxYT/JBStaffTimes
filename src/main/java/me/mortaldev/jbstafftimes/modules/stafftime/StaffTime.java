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
  private final HashMap<String, Long> times;
  // date, time in minutes
  private Filter filter;

  public StaffTime(UUID uuid) {
    this.uuid = uuid;
    this.filter = Filter.NON_ADMINS;
    this.times = new HashMap<>();
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

  public HashMap<String, Long> getTimes() {
    return times;
  }

  public Long getTotalTime() {
    Long totalTime = 0L;
    for (Long time : times.values()) {
      totalTime += time;
    }
    return totalTime;
  }

  public Long getTime(LocalDate date) {
    return times.get(localDateToString(date));
  }

  public Long getTimeOfWholeWeek(LocalDate date) {
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

  private String localDateToString(LocalDate date) {
    return date.format(DateTimeFormatter.ISO_DATE);
  }

  private LocalDate localDateFromString(String dateString) {
    return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
  }

  @Override
  public String getID() {
    return uuid.toString();
  }
}
