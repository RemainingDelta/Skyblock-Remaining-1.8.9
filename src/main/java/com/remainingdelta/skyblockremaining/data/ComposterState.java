package com.remainingdelta.skyblockremaining.data;

import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Holds the state of the Composter.
 */
public class ComposterState {
  public double organicMatter = 0;
  public double fuel = 0;
  public long lastTimestamp = 0;
  public int speedLevel = 0;
  public int costLevel = 0;
  public boolean isInactive = false;
  public long cycleTimeSeconds = 0;


  /**
   * Updates speedLevel and costLevel based on passed in composter data.
   *
   * @param composterData updates speedLevel and costLevel
   */
  public void updateUpgradesFromApi(JsonObject composterData) {
    if (composterData == null) {
      return;
    }
    if (composterData.has("upgrades")) {
      JsonObject upgrades = composterData.getAsJsonObject("upgrades");
      if (upgrades.has("speed")) {
        this.speedLevel = upgrades.get("speed").getAsInt();
      }
      if (upgrades.has("cost_reduction")) {
        this.costLevel = upgrades.get("cost_reduction").getAsInt();
      }
    }
  }

  /**
   * Returns the values of the matter and fuel as a string.
   *
   * @return the values of the matter and fuel as a string
   */
  @Override
  public String toString() {
    return ("Matter: " + this.organicMatter + " | Fuel: " + this.fuel + " | Speed: "
        + this.speedLevel + " | Cost: " + this.costLevel);
  }
}
