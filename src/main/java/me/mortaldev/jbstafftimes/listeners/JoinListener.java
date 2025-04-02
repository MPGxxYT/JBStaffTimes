package me.mortaldev.jbstafftimes.listeners;

import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.modules.afk.AfkManager;
import me.mortaldev.jbstafftimes.modules.timer.TimerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    TimerManager.getInstance().startTimer(event.getPlayer());
    if (event.getPlayer().hasPermission(MainConfig.getInstance().getStaffPermission())) {
      AfkManager.getInstance().addRelevantPlayer(event.getPlayer().getUniqueId());
    }
  }
}
