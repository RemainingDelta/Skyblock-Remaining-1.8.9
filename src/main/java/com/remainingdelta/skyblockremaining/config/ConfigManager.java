package com.remainingdelta.skyblockremaining.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.remainingdelta.skyblockremaining.SkyblockRemaining;
import com.remainingdelta.skyblockremaining.TodoItem;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of IConfigManager that manages the mod's configuration files using GSON.
 */
public class ConfigManager implements IConfigManager {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final File configDir;
  private final File configFile;

  public ConfigManager(File minecraftDir) {
    this.configDir = new File(minecraftDir, "config/skyblock-remaining");
    if (!this.configDir.exists()) {
      this.configDir.mkdir();
    }
    this.configFile = new File(this.configDir, "general.json");
  }


  /**
   * Loads the saved configuration (GUI positions, enabled features) from disk.
   */
  @Override
  public void loadConfig() {
    if (!this.configFile.exists()) {
      this.saveConfig();
      return;
    }
    try (Reader reader = new FileReader(configFile)) {
      ConfigData data = GSON.fromJson(reader, ConfigData.class);

      if (data != null) {
        // Apply GUI positions
        SkyblockRemaining.guiX = data.guiX;
        SkyblockRemaining.guiY = data.guiY;

        // Apply Enabled States to TodoItems
        if (data.enabledFeatures != null) {
          for (TodoItem item : SkyblockRemaining.todoList) {
            if (data.enabledFeatures.containsKey(item.getName())) {
              item.setEnabled(data.enabledFeatures.get(item.getName()));
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Saves the current configuration (GUI positions, enabled features) to disk.
   */
  @Override
  public void saveConfig() {
    ConfigData data = new ConfigData();
    data.guiX = SkyblockRemaining.guiX;
    data.guiY = SkyblockRemaining.guiY;

    data.enabledFeatures = new HashMap<>();
    for (TodoItem item : SkyblockRemaining.todoList) {
      data.enabledFeatures.put(item.getName(), item.isEnabled());
    }

    try (Writer writer = new FileWriter(configFile)) {
      GSON.toJson(data, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the base directory where all mod config files are stored.
   *
   * @return The config/skyblock-remaining folder.
   */
  @Override
  public File getConfigDirectory() {
    return this.configDir;
  }

  /**
   * Internal class to represent the JSON structure.
   */
  private static class ConfigData {
    int guiX = 10;
    int guiY = 10;
    Map<String, Boolean> enabledFeatures = new HashMap<>();
  }
}
