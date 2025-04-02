package me.mortaldev.jbstafftimes.modules.stafftime;

import java.util.HashMap;
import me.mortaldev.crudapi.CRUD;
import me.mortaldev.jbstafftimes.Main;

public class StaffTimeCRUD extends CRUD<StaffTime> {
  private static final String PATH = Main.getInstance().getDataFolder() + "/stafftime/";

  private StaffTimeCRUD() {}

  public static StaffTimeCRUD getInstance() {
    return Singleton.INSTANCE;
  }

  @Override
  public Class<StaffTime> getClazz() {
    return StaffTime.class;
  }

  @Override
  public HashMap<Class<?>, Object> getTypeAdapterHashMap() {
    return new HashMap<>();
  }

  @Override
  public String getPath() {
    return PATH;
  }

  private static class Singleton {
    private static final StaffTimeCRUD INSTANCE = new StaffTimeCRUD();
  }
}
