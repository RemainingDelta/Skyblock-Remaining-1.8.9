package com.remainingdelta.skyblockremaining.api;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Manages the API keys including hypixel api key, and minecraft UUID.
 * Load the env when the class is loaded.
 */
public class ApiKeyManager implements IApiKeyManager {
  private String hypixelApiKey;
  private String uuid;


  /**
   * Upon creating a ApiKeyManager object, the env is loaded.
   */
  public ApiKeyManager() {
    this.loadEnv();
  }

  /**
   * Loads the .env file to the fields.
   */
  private void loadEnv() {
    File envFile = new File(".env");
    if (!envFile.exists()) {
      envFile = new File("../.env");
    }
    if (!envFile.exists()) {
      System.out.println("Env file not found!");
    }
    if (envFile.exists()) {
      Properties prop = new Properties();
      try (FileInputStream fis = new FileInputStream(envFile)) {
        prop.load(fis);
        hypixelApiKey = prop.getProperty("HYPIXEL_API_KEY");
        uuid = prop.getProperty("UUID");
      } catch (Exception e) {
        e.printStackTrace();
      }
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
}
