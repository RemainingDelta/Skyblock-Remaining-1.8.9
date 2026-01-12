package com.remainingdelta.skyblockremaining.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Calls the Hypixel API for queries that we needed.
 */
public class HypixelApi implements IHypixelApi {

  private final IApiKeyManager apiKeyManager;

  /**
   * Creates a HypixelApi with ApiKeyManager injection.
   *
   * @param apiKeyManager used to access their api
   */
  public HypixelApi(IApiKeyManager apiKeyManager) {
    this.apiKeyManager = apiKeyManager;
  }

  /**
   * Returns the profileId of the profile that is currently selected based off the UUID of the
   * player.
   *
   * @return profileId of the profile that is currently selected
   */
  @Override
  public String getProfileId() {
    String hypixelApiKey = this.apiKeyManager.getHypixelApiKey();
    String uuid = this.apiKeyManager.getUuid();
    if (hypixelApiKey == null || uuid == null) {
      return null;
    }
    String URL = "https://api.hypixel.net/v2/skyblock/profiles?key=" + hypixelApiKey
        + "&uuid=" + uuid;
    JsonObject response = getJsonResponse(URL);

    if (response != null && response.has("profiles")) {
      for (JsonElement profileElement : response.getAsJsonArray("profiles")) {
        JsonObject profile = profileElement.getAsJsonObject();
        if (profile.has("selected") && profile.get("selected").getAsBoolean()) {
          return profile.get("profile_id").getAsString();
        }
      }
    }
    return null;
  }

  /**
   * Returns the garden data json object by calling the garden data api passing in the profile id.
   *
   * @return the garden data json object of the profile that is currently selected
   */
  @Override
  public JsonObject getGardenData() {
    String hypixelApiKey = this.apiKeyManager.getHypixelApiKey();
    String profileId = this.getProfileId();

    if (hypixelApiKey == null || profileId == null) {
      return null;
    }
    String URL = "https://api.hypixel.net/v2/skyblock/garden?key=" + hypixelApiKey + "&profile="
        + profileId;
    JsonObject response = getJsonResponse(URL);
    if (response != null && response.has("garden")) {
      return response.getAsJsonObject("garden");
    }
    return null;
  }

  /**
   * Returns the json response based on the URL. Return null if it fails.
   *
   * @param urlString URL of the response you are trying to get
   * @return json response if it succeeds, null if it fails
   */
  private static JsonObject getJsonResponse(String urlString) {
    try {
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", "SkyblockRemaining");
      if (connection.getResponseCode() == 200) {
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        return new JsonParser().parse(reader).getAsJsonObject();
      } else {
        System.out.println("API Request failed. Error Code: " + connection.getResponseCode());
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
