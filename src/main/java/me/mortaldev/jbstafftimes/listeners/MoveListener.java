package me.mortaldev.jbstafftimes.listeners;

import java.util.HashMap;
import me.mortaldev.jbstafftimes.Main;
import me.mortaldev.jbstafftimes.modules.afk.AfkManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
  private final HashMap<String, Integer> scheduledTasks = new HashMap<>();

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (!event.hasChangedPosition()) {
      return;
    }
    if (!AfkManager.getInstance().containsRelevantPlayer(event.getPlayer().getUniqueId())) {
      return;
    }
    if (scheduledTasks.containsKey("delay." + event.getPlayer().getUniqueId())) {
      return;
    }
    scheduledTasks.put(
        "delay." + event.getPlayer().getUniqueId(),
        Bukkit.getScheduler()
            .scheduleSyncDelayedTask(
                Main.getInstance(),
                () ->
                    Bukkit.getScheduler()
                        .cancelTask(
                            scheduledTasks.remove("delay." + event.getPlayer().getUniqueId())),
                20L));
    AfkManager.getInstance().resetTimer(event.getPlayer());
  }
}
