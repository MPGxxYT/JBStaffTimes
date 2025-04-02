package me.mortaldev.jbstafftimes.modules.afk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import me.mortaldev.jbstafftimes.Main;
import me.mortaldev.jbstafftimes.modules.timer.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AfkManager {

  private final AfkTimer afkTimer = new AfkTimer();
  // This is a list of all players that have the staff perm
  private final HashSet<UUID> relevantPlayers = new HashSet<>();
  private final HashSet<UUID> afkPlayers = new HashSet<>();
  private final HashMap<String, Integer> scheduledTasks = new HashMap<>();

  private AfkManager() {}

  public static AfkManager getInstance() {
    return Singleton.INSTANCE;
  }


  public boolean containsRelevantPlayer(UUID uuid) {
    return relevantPlayers.contains(uuid);
  }

  public void addRelevantPlayer(UUID uuid) {
    relevantPlayers.add(uuid);
  }

  public void removeRelevantPlayer(UUID uuid) {
    relevantPlayers.remove(uuid);
  }

  public void clearRelevantPlayers() {
    relevantPlayers.clear();
  }

  public void init() {
    if (scheduledTasks.containsKey("afk")) {
      Bukkit.getScheduler().cancelTask(scheduledTasks.remove("afk"));
    }
    scheduledTasks.put(
        "afk",
        Bukkit.getScheduler()
            .scheduleSyncRepeatingTask(
                Main.getInstance(),
                () -> {
                  for (UUID uuid : afkTimer.getAfkPlayers()) {
                    TimerManager.getInstance().stopTimer(uuid);
                    afkTimer.stopTimer(uuid);
                    afkPlayers.add(uuid);
                  }
                },
                20,
                20));
  }

  public void clearAFK() {
    afkPlayers.clear();
  }

  public void moved(Player player) {
    afkTimer.startTimer(player.getUniqueId());
    if (afkPlayers.contains(player.getUniqueId())) {
      afkPlayers.remove(player.getUniqueId());
      TimerManager.getInstance().startTimer(player);
    }
  }

  private static class Singleton {
    private static final AfkManager INSTANCE = new AfkManager();
  }
}
