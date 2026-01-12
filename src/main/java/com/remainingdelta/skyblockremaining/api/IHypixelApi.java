package com.remainingdelta.skyblockremaining.api;

import com.google.gson.JsonObject;

/**
 * Represents the interface for the hypixel api.
 */
public interface IHypixelApi {

  /**
   * Returns the profileId of the profile that is currently selected based off the UUID of the
   * player.
   *
   * @return profileId of the profile that is currently selected
   */
  String getProfileId();

  /**
   * Returns the garden data json object by calling the garden data api passing in the profile id.
   *
   * @return the garden data json object of the profile that is currently selected
   */
  JsonObject getGardenData();
}
