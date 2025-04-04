package me.mortaldev.jbstafftimes.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.modules.afk.AfkManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    if (event.getPlayer().hasPermission(MainConfig.getInstance().getStaffPermission())) {
      AfkManager.getInstance().resetTimer(event.getPlayer());
    }
  }
}
