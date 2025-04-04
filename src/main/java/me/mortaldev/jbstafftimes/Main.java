package me.mortaldev.jbstafftimes;

import co.aikar.commands.PaperCommandManager;
import me.mortaldev.jbstafftimes.commands.StaffTimesCommand;
import me.mortaldev.jbstafftimes.config.MainConfig;
import me.mortaldev.jbstafftimes.listeners.ChatListener;
import me.mortaldev.jbstafftimes.listeners.JoinListener;
import me.mortaldev.jbstafftimes.listeners.MoveListener;
import me.mortaldev.jbstafftimes.listeners.QuitListener;
import me.mortaldev.jbstafftimes.modules.afk.AfkManager;
import me.mortaldev.jbstafftimes.modules.stafftime.StaffTimeManager;
import me.mortaldev.jbstafftimes.modules.timer.TimerManager;
import me.mortaldev.menuapi.GUIListener;
import me.mortaldev.menuapi.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public final class Main extends JavaPlugin {

  private static final String LABEL = "JBStaffTimes";
  static Main instance;
  static HashSet<String> dependencies = new HashSet<>();
  static PaperCommandManager commandManager;
  static GUIManager guiManager;
  static Boolean debugToggle;

  public static Main getInstance() {
    return instance;
  }

  public static String getLabel() {
    return LABEL;
  }

  public static GUIManager getGuiManager() {
    return guiManager;
  }

  public static void log(String message) {
    Bukkit.getLogger().info("[" + Main.getLabel() + "] " + message);
  }

  public static void toggleDebug() {
    debugToggle = !debugToggle;
  }

  public static Boolean getDebugToggle() {
    return debugToggle;
  }

  @Override
  public void onEnable() {
    instance = this;
    debugToggle = false;
    commandManager = new PaperCommandManager(this);

    // DATA FOLDER

    if (!getDataFolder().exists()) {
      getDataFolder().mkdir();
    }

    // DEPENDENCIES

    for (String plugin : dependencies) {
      if (Bukkit.getPluginManager().getPlugin(plugin) == null) {
        getLogger().warning("Could not find " + plugin + "! This plugin is required.");
        Bukkit.getPluginManager().disablePlugin(this);
        return;
      }
    }

    // CONFIGS
    MainConfig.getInstance().load();

    // Managers (Loading data)
    StaffTimeManager.getInstance().load();

    // GUI Manager
    guiManager = new GUIManager();
    GUIListener guiListener = new GUIListener(guiManager);
    Bukkit.getPluginManager().registerEvents(guiListener, this);

    // Events

    getServer().getPluginManager().registerEvents(new JoinListener(), this);
    getServer().getPluginManager().registerEvents(new QuitListener(), this);
    getServer().getPluginManager().registerEvents(new MoveListener(), this);
    getServer().getPluginManager().registerEvents(new ChatListener(), this);

    // COMMANDS

    commandManager.registerCommand(new StaffTimesCommand());

    TimerManager.getInstance().initalizeTimers();
    AfkManager.getInstance().init();
    getLogger().info(LABEL + " Enabled");
  }

  @Override
  public void onDisable() {
    TimerManager.getInstance().stopAllTimers();
    getLogger().info(LABEL + " Disabled");
  }
}
