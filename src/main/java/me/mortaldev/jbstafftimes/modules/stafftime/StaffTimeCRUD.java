package me.mortaldev.jbstafftimes.modules.stafftime;

import me.mortaldev.crudapi.CRUD;
import me.mortaldev.crudapi.CRUDAdapters;
import me.mortaldev.crudapi.handlers.GSON;
import me.mortaldev.jbstafftimes.Main;

public class StaffTimeCRUD extends CRUD<StaffTime> {
  private static final String PATH = Main.getInstance().getDataFolder() + "/stafftime/";

  private StaffTimeCRUD() {
    super(GSON.getInstance());
  }

  public static StaffTimeCRUD getInstance() {
    return Singleton.INSTANCE;
  }

  @Override
  public Class<StaffTime> getClazz() {
    return StaffTime.class;
  }

  @Override
  public CRUDAdapters getCRUDAdapters() {
    return new CRUDAdapters();
  }

  @Override
  public String getPath() {
    return PATH;
  }

  private static class Singleton {
    private static final StaffTimeCRUD INSTANCE = new StaffTimeCRUD();
  }
}
