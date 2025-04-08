package me.mortaldev.jbstafftimes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.menus.StaffTimesMenu;
import me.mortaldev.jbstafftimes.modules.stafftime.StaffTime;
import me.mortaldev.jbstafftimes.modules.stafftime.StaffTimeManager;
import me.mortaldev.jbstafftimes.utils.TextUtil;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.Optional;

@CommandAlias("utime|ut")
@CommandPermission("jbstafftimes.utime")
public class UTimeCommand extends BaseCommand {

  @Default
  public void displayTime(Player player) {
    Optional<StaffTime> staffTimeOptional =
        StaffTimeManager.getInstance().getByID(player.getUniqueId().toString());
    if (staffTimeOptional.isEmpty()) {
      player.sendMessage(TextUtil.format("&cYou have no hours to display. [Likely a bug]"));
      return;
    }
    StaffTime staffTime = staffTimeOptional.get();
    LocalDate today = StaffTimeManager.getInstance().getToday();
    player.sendMessage("");
    player.sendMessage(TextUtil.format("&6Your Staff Hours: &7[" +MainConfig.getInstance().getMinimumHours()+ "h min]"));
    Long timeOfWholeWeek = staffTime.getTimeOfWholeWeek(today);
    String formattedTime = StaffTimesMenu.formattedDuration(timeOfWholeWeek);
    player.sendMessage(TextUtil.format("&f > &7" + formattedTime + "h"));
    player.sendMessage("");
  }
}
