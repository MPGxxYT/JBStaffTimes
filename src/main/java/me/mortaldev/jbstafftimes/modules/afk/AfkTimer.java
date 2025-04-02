package me.mortaldev.jbstafftimes.modules.afk;

import me.mortaldev.jbstafftimes.config.MainConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AfkTimer {
  // UUID, timestamp
  private final HashMap<UUID, Long> timers = new HashMap<>();

  public void startTimer(UUID uuid) {
    timers.put(uuid, System.currentTimeMillis());
  }

  public boolean hasStoodTooLong(UUID uuid) {
    int afkThresholdInMilliseconds = MainConfig.getInstance().getAfkThreshold() * 60000;
    if (timers.containsKey(uuid)) {
      return System.currentTimeMillis() - timers.get(uuid) > afkThresholdInMilliseconds;
    }
    return false;
  }

  public Set<UUID> getAfkPlayers() {
    Set<UUID> uuids = new HashSet<>();
    for (UUID uuid : timers.keySet()) {
      if (hasStoodTooLong(uuid)) {
        uuids.add(uuid);
      }
    }
    return uuids;
  }

  public void stopTimer(UUID uuid) {
    timers.remove(uuid);
  }

  public HashMap<UUID, Long> getTimers() {
    return timers;
  }
}
