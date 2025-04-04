package me.mortaldev.jbstafftimes.config;

import me.mortaldev.AbstractConfig;
import me.mortaldev.ConfigValue;
import me.mortaldev.jbstafftimes.Main;
import org.bukkit.plugin.java.JavaPlugin;

public class MainConfig extends AbstractConfig {

  private ConfigValue<Integer> savingLength = new ConfigValue<>("savingLength", 3);
  private ConfigValue<Integer> afkThreshold = new ConfigValue<>("afkThreshold", 2);
  private ConfigValue<Integer> minimumHours = new ConfigValue<>("minimumHours", 8);
  private ConfigValue<String> staffPermission =
      new ConfigValue<>("staffPermission", "group.helper");
  private ConfigValue<String> adminPermission = new ConfigValue<>("adminPermission", "group.admin");

  private MainConfig() {}

  public static synchronized MainConfig getInstance() {
    return Singleton.INSTANCE;
  }

  @Override
  public void log(String message) {
    Main.log(message);
  }

  @Override
  public String getName() {
    return "config";
  }

  @Override
  public JavaPlugin getMain() {
    return Main.getInstance();
  }

  @Override
  public void loadData() {
    savingLength = getConfigValue(savingLength);
    afkThreshold = getConfigValue(afkThreshold);
    minimumHours = getConfigValue(minimumHours);
    staffPermission = getConfigValue(staffPermission);
    adminPermission = getConfigValue(adminPermission);
  }


  public Integer getSavingLength() {
    return savingLength.getValue();
  }

  public void setSavingLength(Integer savingLength) {
    this.savingLength.setValue(savingLength);
    saveValue(this.savingLength.getId(), savingLength);
  }

  /**
   * Gets the number of seconds before a player is considered AFK. This value is
   * configured in the config.yml file under the key "afkThreshold".
   * <p>
   * The default value is 2, which means that a player is considered AFK after 2
   * seconds of inactivity.
   *
   * @return The number of seconds before a player is considered AFK.
   */
  public Integer getAfkThreshold() {
    return afkThreshold.getValue();
  }

  /**
   * Sets the number of seconds before a player is considered AFK. This value is saved
   * to the config.yml file under the key "afkThreshold".
   *
   * @param afkThreshold The number of seconds before a player is considered AFK.
   */
  public void setAfkThreshold(Integer afkThreshold) {
    this.afkThreshold.setValue(afkThreshold);
    saveValue(this.afkThreshold.getId(), afkThreshold);
  }


  public Integer getMinimumHours() {
    return minimumHours.getValue();
  }

  public void setMinimumHours(Integer minimumHours) {
    this.minimumHours.setValue(minimumHours);
    saveValue(this.minimumHours.getId(), minimumHours);
  }


  public String getStaffPermission() {
    return staffPermission.getValue();
  }

  public void setStaffPermission(String staffPermission) {
    this.staffPermission.setValue(staffPermission);
    saveValue(this.staffPermission.getId(), staffPermission);
  }

  public String getAdminPermission() {
    return adminPermission.getValue();
  }

  public void setAdminPermission(String adminPermission) {
    this.adminPermission.setValue(adminPermission);
    saveValue(this.adminPermission.getId(), adminPermission);
  }

  private static class Singleton {
    private static final MainConfig INSTANCE = new MainConfig();
  }
}
