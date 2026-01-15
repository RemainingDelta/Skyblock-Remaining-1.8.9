package com.remainingdelta.skyblockremaining.config;

import java.io.File;

/**
 * Defines the contract for loading, saving, and locating the mod's configuration settings.
 */
public interface IConfigManager {

  /**
   * Loads the saved configuration (GUI positions, enabled features) from disk.
   */
  void loadConfig();

  /**
   * Saves the current configuration (GUI positions, enabled features) to disk.
   */
  void saveConfig();

  /**
   * Gets the base directory where all mod config files are stored.
   *
   * @return The config/skyblock-remaining folder.
   */
  File getConfigDirectory();
}
