package com.remainingdelta.skyblockremaining.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Saves and loads Composter Data.
 */
public class ComposterDataManager implements IDataManager<ComposterState> {

  private final File composterFile;
  private final Gson gson;

  /**
   * Composter Data manager constructor which finds or creates a file for saving/loading composter
   * data.
   */
  public ComposterDataManager() {
    File configDir = new File(Minecraft.getMinecraft().mcDataDir, "config");
    if (!configDir.exists()) {
      configDir.mkdirs();
    }
    this.composterFile = new File(configDir, "skyblock_composter.json");
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  /**
   * Saves the composter state to storage.
   *
   * @param state The composter state to save.
   */
  @Override
  public void save(ComposterState state) {
    try (FileWriter writer = new FileWriter(this.composterFile)) {
      gson.toJson(state, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads the composter state from storage.
   *
   * @return The loaded composter state
   */
  @Override
  public ComposterState load() {
    if (!this.composterFile.exists()) {
      return new ComposterState();
    }
    try (FileReader reader = new FileReader(this.composterFile)) {
      ComposterState state = gson.fromJson(reader, ComposterState.class);
      if (state == null) {
        return new ComposterState();
      }
      return state;

    } catch (IOException e) {
      e.printStackTrace();
      return new ComposterState();
    }
  }
}
