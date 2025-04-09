package me.mortaldev.jbstafftimes.listeners;

import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.modules.afk.AfkManager;
import me.mortaldev.jbstafftimes.modules.timer.TimerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    TimerManager.getInstance().stopTimer(event.getPlayer().getUniqueId());
    if (event.getPlayer().hasPermission(MainConfig.getInstance().getStaffPermission())) {
      AfkManager.getInstance().removeRelevantPlayer(event.getPlayer().getUniqueId());
      AfkManager.getInstance().removeAfkPlayer(event.getPlayer().getUniqueId());
    }
  }
}
