package me.mortaldev.jbstafftimes.modules.stafftime;

import me.mortaldev.crudapi.CRUD;
import me.mortaldev.crudapi.CRUDManager;
import me.mortaldev.jbstafftimes.Main;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

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

  private static class Singleton {
    private static final StaffTimeManager INSTANCE = new StaffTimeManager();
  }
}
