package me.mortaldev.jbstafftimes.menus;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.mortaldev.jbstafftimes.Main;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.menus.pagedata.Filter;
import me.mortaldev.jbstafftimes.menus.pagedata.PageData;
import me.mortaldev.jbstafftimes.modules.stafftime.StaffTime;
import me.mortaldev.jbstafftimes.modules.stafftime.StaffTimeManager;
import me.mortaldev.jbstafftimes.modules.timer.TimerManager;
import me.mortaldev.jbstafftimes.utils.ItemStackHelper;
import me.mortaldev.jbstafftimes.utils.TextUtil;
import me.mortaldev.jbstafftimes.utils.Utils;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class StaffTimesMenu extends InventoryGUI {

  private final PageData pageData;
  private HashSet<StaffTime> staffTimes;

  public StaffTimesMenu(PageData pageData) {
    this.pageData = pageData;
    this.staffTimes = StaffTimeManager.getInstance().getSet();
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 9 * getInventorySize(), TextUtil.format("Staff Times"));
  }

  private int getInventorySize() {
    staffTimes = filterStaffTimes(staffTimes, pageData.getFilter());
    if (pageData.getSearchQuery() != null) {
      String searchQuery = pageData.getSearchQuery();
      staffTimes =
          staffTimes.stream()
              .filter(
                  staffTime -> {
                    String name =
                        Bukkit.getOfflinePlayer(UUID.fromString(staffTime.getID())).getName();
                    return name != null && name.contains(searchQuery);
                  })
              .collect(Collectors.toCollection(HashSet::new));
    }
    double ceil = Math.ceil((double) staffTimes.size() / 9);
    return Utils.clamp((int) ceil + 1, 3, 6);
  }

  private HashSet<StaffTime> filterStaffTimes(HashSet<StaffTime> staffTimes, Filter filter) {
    HashSet<StaffTime> newStaffTimes = new HashSet<>();
    return switch (filter) {
      case NON_ADMINS, ADMINS -> {
        for (StaffTime staffTime : staffTimes) {
          if (staffTime.getFilter() == filter) {
            newStaffTimes.add(staffTime);
          }
        }
        yield newStaffTimes;
      }
      default -> staffTimes;
    };
  }

  @Override
  public void decorate(Player player) {
    ItemStack whiteGlass =
        ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name(" ").build();
    for (int i = 0; i < 9; i++) {
      getInventory().setItem(i, whiteGlass);
    }
    for (int i = 0, slot = 3; i < MainConfig.getInstance().getSavingLength(); i++, slot++) {
      addButton(slot, WeekOfButton(i + 1));
    }
    addUsers();
    addButton(0, SearchButton());
    addButton(8, FilterButton());
    addButton(1, UpdateButton());
    super.decorate(player);
  }

  private void addUsers() {
    long adjustment = (pageData.getWeekNumber() - 1) * 7L;
    LocalDate adjustedDate = getAdjustedDate(adjustment);
    int slot = 9;
    LinkedHashSet<StaffTime> sortedStaffTimes = sortStaffTimes(staffTimes);
    for (StaffTime staffTime : sortedStaffTimes) {
      ItemStack skull = getHead(staffTime, adjustedDate);
      getInventory().setItem(slot, skull);
      slot++;
    }
  }

  public static LocalDate getAdjustedDate(long adjustment) {
    LocalDate date = StaffTimeManager.getInstance().getToday().minusDays(adjustment);
    return StaffTimeManager.getInstance().getSundayOf(date);
  }

  private ItemStack getHead(StaffTime staffTime, LocalDate adjustedDate) {
    Long activeTimeOfWeek = staffTime.getTimeOfWholeWeek(adjustedDate);
    Long afkTimeOfWeek = staffTime.getAfkOfWholeWeek(adjustedDate);
    double activeHours = (double) (activeTimeOfWeek / 60000) / 60;
    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(staffTime.getID()));
    skull.editMeta(SkullMeta.class, meta -> meta.setOwningPlayer(offlinePlayer));
    return ItemStackHelper.builder(skull)
        .name("&e&l" + getTimeColoredName(offlinePlayer.getName(), activeHours))
        .addLore("")
        .addLore("&3&lActive:&7 " + formattedDuration(activeTimeOfWeek) + " Hours")
        .addLore("")
        .addLore("&3&lAFK: ")
        .addLore("&7 ~ " + formattedDuration(afkTimeOfWeek) + " Hours")
        .addLore("&7 ~ " + staffTime.getAfkFlagsOfWeek(adjustedDate) + " Times Flagged")
        .build();
  }

  public static String formattedDuration(Long time) {
    double hours = (double) (time / 60000) / 60;
    return String.format("%.2f", hours);
  }

  private String getTimeColoredName(String name, double hours) {
    return hours >= MainConfig.getInstance().getMinimumHours() ? "&2&l" + name : "&c&l" + name;
  }

  private LinkedHashSet<StaffTime> sortStaffTimes(Set<StaffTime> staffTimes) {
    LinkedHashSet<StaffTime> sortedStaffTimes = new LinkedHashSet<>(staffTimes);
    Stream<StaffTime> sorted =
        sortedStaffTimes.stream()
            .sorted(Comparator.comparingLong(StaffTime::getTotalTime).reversed());
    return sorted.collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private InventoryButton UpdateButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.BONE_MEAL)
                    .name("&e&lUpdate Data")
                    .addLore("&7Will update the data to the current second.")
                    .addLore()
                    .addLore("&7[Click to update]")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              TimerManager.getInstance().restartAllTimers();
              Main.getGuiManager().openGUI(new StaffTimesMenu(pageData), player);
            });
  }

  private InventoryButton WeekOfButton(int weekNumber) {
    long adjustment = (weekNumber - 1) * 7L;
    LocalDate sunday = getAdjustedDate(adjustment);
    Material material = pageData.getWeekNumber() == weekNumber ? Material.PAPER : Material.BOOK;
    String lore = pageData.getWeekNumber() == weekNumber ? "&7&l[SELECTED]" : "&7[Click to view]";
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(material)
                    .name("&e&lWeek of " + Utils.formatOrdinal(sunday.getDayOfMonth()))
                    .addLore(lore)
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              pageData.setWeekNumber(weekNumber);
              Main.getGuiManager().openGUI(new StaffTimesMenu(pageData), player);
            });
  }

  private InventoryButton FilterButton() {
    String nonAdminsLore =
        pageData.getFilter() == Filter.NON_ADMINS ? "&f&l Non Admins" : "&7Non Admins";
    String adminsLore = pageData.getFilter() == Filter.ADMINS ? "&f&l Admins" : "&7Admins";
    String allLore = pageData.getFilter() == Filter.ALL ? "&f&l All Staff" : "&7All Staff";
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.MAP)
                    .name("&e&lFilter")
                    .addLore("")
                    .addLore(nonAdminsLore)
                    .addLore(adminsLore)
                    .addLore(allLore)
                    .addLore("")
                    .addLore("&7[Click to switch filter]")
                    .build())
        .consumer(
            event -> {
              Filter next = Filter.next(pageData.getFilter());
              pageData.setFilter(next);
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager().openGUI(new StaffTimesMenu(pageData), player);
            });
  }

  private InventoryButton SearchButton() {
    String queryLore = pageData.getSearchQuery() == null ? "[Empty]" : pageData.getSearchQuery();
    String clickLore =
        pageData.getSearchQuery() == null ? "[Click to search]" : "[Click to clear search]";
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.ANVIL)
                    .name("&e&lSearch")
                    .addLore("&7Use this to lookup a user.")
                    .addLore()
                    .addLore("&fQuery:&7 " + queryLore)
                    .addLore()
                    .addLore("&7" + clickLore)
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (pageData.getSearchQuery() == null) {
                new AnvilGUI.Builder()
                    .plugin(Main.getInstance())
                    .title("Search")
                    .itemLeft(
                        ItemStackHelper.builder(Material.PAPER)
                            .name("")
                            .build()) // the name might be why theres a bug
                    .onClick(
                        (slot, stateSnapshot) -> {
                          if (slot == 2) {
                            String textEntry = stateSnapshot.getText();
                            pageData.setSearchQuery(textEntry);
                            Main.getGuiManager().openGUI(new StaffTimesMenu(pageData), player);
                          }
                          return Collections.emptyList();
                        })
                    .open(player);
              } else {
                pageData.setSearchQuery(null);
                Main.getGuiManager().openGUI(new StaffTimesMenu(pageData), player);
              }
            });
  }
}
