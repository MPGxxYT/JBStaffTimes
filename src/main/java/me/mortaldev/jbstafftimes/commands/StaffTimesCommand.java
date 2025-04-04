package me.mortaldev.jbstafftimes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.mortaldev.jbstafftimes.Main;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.menus.StaffTimesMenu;
import me.mortaldev.jbstafftimes.menus.pagedata.PageData;
import me.mortaldev.jbstafftimes.modules.afk.AfkManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("stafftimes")
@CommandPermission("jbstafftimes.admin")
public class StaffTimesCommand extends BaseCommand {

  @Default
  public void openMenu(Player player) {
    Main.getGuiManager().openGUI(new StaffTimesMenu(new PageData(1)), player);
  }

  @Subcommand("reload")
  public void reloadConfig(CommandSender sender) {
    String response = MainConfig.getInstance().reload();
    sender.sendMessage(response);
    AfkManager.getInstance().init();
  }

  @Subcommand("debug")
  public void debug(CommandSender sender) {
    Main.toggleDebug();
    sender.sendMessage("Debug mode " + Main.getDebugToggle().toString());
  }
}
