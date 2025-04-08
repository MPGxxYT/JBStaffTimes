package me.mortaldev.jbstafftimes.modules.stafftime;

import me.mortaldev.crudapi.CRUD;
import me.mortaldev.crudapi.CRUDManager;
import me.mortaldev.jbstafftimes.Main;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.menus.StaffTimesMenu;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffTimeManager extends CRUDManager<StaffTime> {

  private StaffTimeManager() {}

  public static StaffTimeManager getInstance() {
    return Singleton.INSTANCE;
  }

  @Override
  public CRUD<StaffTime> getCRUD() {
    return StaffTimeCRUD.getInstance();
  }

  @Override
  public void log(String string) {
    Main.log(string);
  }

  /**
   * Gets the current date in America/New_York [EST].
   *
   * @return the current date
   */
  public LocalDate getToday() {
    return ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDate();
  }

  /**
   * Returns the Sunday of the week containing the given date.
   *
   * @param date the date
   * @return the Sunday of the week
   */
  public LocalDate getSundayOf(LocalDate date) {
    return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
  }

  @Override
  public void load() {
    super.load();
    filterStaffTimes();
  }

  private void filterStaffTimes() {
    Integer savingLength = MainConfig.getInstance().getSavingLength();
    LocalDate adjustedDate = StaffTimesMenu.getAdjustedDate(savingLength * 7L);
    StaffTimeManager staffTimeManager = StaffTimeManager.getInstance();
    List<StaffTime> staffTimesToUpdate = new ArrayList<>();
    for (StaffTime staffTime : getSet()) {
      filterAfkTime(staffTime, adjustedDate);
      filterTimes(staffTime, adjustedDate);
      filterFlags(staffTime, adjustedDate);
      staffTimesToUpdate.add(staffTime);
    }
    staffTimesToUpdate.forEach(staffTimeManager::update);
  }

  private void filterFlags(StaffTime staffTime, LocalDate adjustedDate) {
    HashMap<String, Long> map = staffTime.getAfkFlags();
    if (map == null) {
      map = new HashMap<>();
      staffTime.setAfkFlags(map);
      return;
    }
    List<LocalDate> keysToRemove = new ArrayList<>();
    map.forEach(
        (date, time) -> {
          LocalDate localDate = staffTime.localDateFromString(date);
          if (localDate.isBefore(adjustedDate)) {
            keysToRemove.add(localDate);
          }
        });
    keysToRemove.forEach(staffTime::removeAfkFlagDate);
  }

  private void filterTimes(StaffTime staffTime, LocalDate adjustedDate) {
    HashMap<String, Long> map = staffTime.getTimes();
    if (map == null) {
      map = new HashMap<>();
      staffTime.setTimes(map);
      return;
    }
    List<LocalDate> keysToRemove = new ArrayList<>();
    map.forEach(
        (date, time) -> {
          LocalDate localDate = staffTime.localDateFromString(date);
          if (localDate.isBefore(adjustedDate)) {
            keysToRemove.add(localDate);
          }
        });
    keysToRemove.forEach(staffTime::removeTimesDate);
  }

  private void filterAfkTime(StaffTime staffTime, LocalDate adjustedDate) {
    HashMap<String, Long> map = staffTime.getAfkTime();
    if (map == null) {
      map = new HashMap<>();
      staffTime.setAfkTime(map);
      return;
    }
    List<LocalDate> keysToRemove = new ArrayList<>();
    map.forEach(
        (date, time) -> {
          LocalDate localDate = staffTime.localDateFromString(date);
          if (localDate.isBefore(adjustedDate)) {
            keysToRemove.add(localDate);
          }
        });
    keysToRemove.forEach(staffTime::removeAfkTimeDate);
  }

  private static class Singleton {
    private static final StaffTimeManager INSTANCE = new StaffTimeManager();
  }
}
