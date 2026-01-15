package com.remainingdelta.skyblockremaining.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 * Manages the API keys including hypixel api key, and minecraft UUID.
 * Load the env when the class is loaded.
 */
public class ApiKeyManager implements IApiKeyManager {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private final File keyFile;

  private String hypixelApiKey = "";
  private String uuid = "";


  /**
   * Upon creating an ApiKeyManager object, the keys are loaded from JSON.
   *
   * @param configDir The directory where config files are stored.
   */
  public ApiKeyManager(File configDir) {
    this.keyFile = new File(configDir, "keys.json");
    this.loadKeys();
  }

  /**
   * Loads the keys.json file into fields.
   */
  private void loadKeys() {
    if (!this.keyFile.exists()) {
      this.createDefaultKeyFile();
      return;
    }

    try (Reader reader = new FileReader(this.keyFile)) {
      KeyData data = GSON.fromJson(reader, KeyData.class);
      if (data != null) {
        this.hypixelApiKey = data.hypixelApiKey;
        this.uuid = data.uuid;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a default keys.json file if one does not exist.
   */
  private void createDefaultKeyFile() {
    KeyData data = new KeyData();
    data.hypixelApiKey = "PASTE_API_KEY_HERE";
    data.uuid = "PASTE_UUID_HERE";

    try (Writer writer = new FileWriter(this.keyFile)) {
      GSON.toJson(data, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Gets the Hypixel API key.
   *
   * @return the Hypixel API key
   */
  @Override
  public String getHypixelApiKey() {
    return hypixelApiKey;
  }

  /**
   * Gets the uuid.
   *
   * @return the uuid
   */
  @Override
  public String getUuid() {
    return uuid;
  }

  /**
   * Internal class to represent the JSON structure.
   */
  private static class KeyData {
    String hypixelApiKey;
    String uuid;
  }
}
