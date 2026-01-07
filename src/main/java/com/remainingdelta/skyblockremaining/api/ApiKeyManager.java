package com.remainingdelta.skyblockremaining.api;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Manages the API keys including hypixel api key, and minecraft UUID.
 */
public class ApiKeyManager {
  private static String hypixelApiKey;
  private static String uuid;

  /**
   * Load the env when the class is loaded.
   */
  static {
    loadEnv();
  }

  /**
   * Loads the .env file to the fields.
   */
  private static void loadEnv() {
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
  public static String getHypixelApiKey() {
    return hypixelApiKey;
  }

  /**
   * Gets the uuid.
   *
   * @return the uuid
   */
  public static String getUuid() {
    return uuid;
  }
}
