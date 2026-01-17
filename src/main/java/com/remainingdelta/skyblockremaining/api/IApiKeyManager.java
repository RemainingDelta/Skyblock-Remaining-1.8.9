package com.remainingdelta.skyblockremaining.api;

/**
 * Interface for ApiKeyManager
 */
public interface IApiKeyManager {

  /**
   * Gets the Hypixel API key.
   *
   * @return the Hypixel API key
   */
  String getHypixelApiKey();

  /**
   * Gets the Hypixel API key.
   *
   * @return the Hypixel API key
   */
  String getUuid();

  /**
   * Sets the Hypixel API key.
   *
   * @param key the Hypixel API key
   */
  void setApiKey(String key);
}
