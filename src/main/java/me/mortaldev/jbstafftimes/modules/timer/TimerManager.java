package me.mortaldev.jbstafftimes.modules.timer;

import java.util.*;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.modules.stafftime.StaffTime;
import me.mortaldev.jbstafftimes.modules.stafftime.StaffTimeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TimerManager {
  Map<UUID, UserTimer> timers = new HashMap<>();

  private TimerManager() {}

  public static TimerManager getInstance() {
    return Singleton.INSTANCE;
  }

  private StaffTime getStaffTime(UUID uuid) {
    Optional<StaffTime> staffTimeOptional = StaffTimeManager.getInstance().getByID(uuid.toString());
    if (staffTimeOptional.isPresent()) {
      return staffTimeOptional.get();
    } else {
      StaffTime staffTime = new StaffTime(uuid);
      StaffTimeManager.getInstance().add(staffTime, true);
      return staffTime;
    }
  }

  public void initalizeTimers() {
    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      startTimer(onlinePlayer);
    }
  }

  public void stopAllTimers() {
    for (UUID uuid : timers.keySet()) {
      stopTimer(uuid);
    }
  }

  public void restartAllTimers() {
    Set<UUID> uuids = new HashSet<>(timers.keySet());
    for (UUID uuid : uuids) {
      stopTimer(uuid);
    }
    for (UUID uuid : uuids) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        startTimer(player);
      }
    }
  }

  public void startTimer(Player player) {
    UUID uuid = player.getUniqueId();
    StaffTime staffTime;
    if (player.hasPermission(MainConfig.getInstance().getStaffPermission())) {
      staffTime = getStaffTime(uuid);
      staffTime.checkPermission();
    } else {
      return;
    }
    if (timers.containsKey(uuid)) {
      long duration = timers.get(uuid).start();
      if (duration > 0) {
        staffTime.addTime(StaffTimeManager.getInstance().getToday(), duration);
      }
    } else {
      UserTimer userTimer = new UserTimer(uuid);
      timers.put(uuid, userTimer);
      userTimer.start();
    }
  }

  public void stopTimer(UUID uuid) {
    if (!timers.containsKey(uuid)) {
      return;
    }
    long duration = timers.get(uuid).stop();
    StaffTime staffTime = getStaffTime(uuid);
    if (duration > 0) {
      staffTime.addTime(StaffTimeManager.getInstance().getToday(), duration);
    }
    timers.remove(uuid);
  }

  private static class Singleton {
    private static final TimerManager INSTANCE = new TimerManager();
  }
}
