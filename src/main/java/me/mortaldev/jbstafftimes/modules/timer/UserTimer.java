package me.mortaldev.jbstafftimes.modules.timer;

import java.util.UUID;

public class UserTimer {
  private final UUID uuid;
  private long timestamp;

  public UserTimer(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  /**
   * Starts the timer if it is not already running, or stops it and returns the duration if it is
   * already running.
   *
   * @return the duration if the timer was already running, or 0 if it was not already running
   */
  public long start() {
    if (timestamp >= 0L) {
      long duration = stop();
      timestamp = System.currentTimeMillis();
      return duration;
    }
    timestamp = System.currentTimeMillis();
    return 0L;
  }

  /**
   * Stops the timer and returns the duration that the timer has been running.
   *
   * @return the duration that the timer has been running
   */
  public long stop() {
    long duration = System.currentTimeMillis() - timestamp;
    timestamp = 0L;
    return duration;
  }
}
