package com.remainingdelta.skyblockremaining;

import com.remainingdelta.skyblockremaining.data.ComposterDataManager;
import com.remainingdelta.skyblockremaining.data.ComposterState;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Tracker for the composter for the todo menu.
 */
public class ComposterTracker extends AbstractTodoItem {
  private int tickCounter = 0;
  private static final Pattern composterPattern = Pattern.compile(
      "(Organic Matter|Fuel):\\s*([\\d\\.]+)([kM]?)");

  private static final double BASE_MATTER_COST = 4000.0;
  private static final double BASE_FUEL_COST = 2000.0;
  private static final double BASE_TIME = 600.0;


  /**
   * Constructor of Composter Tracker which takes in nothing.
   */
  public ComposterTracker() {
    super("Composter");
  }

  /**
   * Gets the current status of the Composter.
   *
   * @return the current status of the todo item
   */
  @Override
  public String getStatus() {
    return calculateTimeRemaining(ComposterDataManager.instance.load());
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    this.tickCounter++;
    if (this.tickCounter < 20) {
      return;
    }
    this.tickCounter = 0;

    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.thePlayer == null || minecraft.theWorld == null) {
      return;
    }
    this.parseTabList(minecraft);
  }

  private void parseTabList(Minecraft minecraft) {
    Collection<NetworkPlayerInfo> tabListData = minecraft.getNetHandler().getPlayerInfoMap();
    boolean foundData = false;
    ComposterState composterstate = ComposterDataManager.instance.load();
    for (NetworkPlayerInfo playerInfo : tabListData) {
      IChatComponent displayName = playerInfo.getDisplayName();
      if (displayName == null) {
        continue;
      }
      String text = displayName.getUnformattedText().replace(",", "");
      Matcher matcher = composterPattern.matcher(text);
      if (matcher.find()) {
        String type = matcher.group(1);
        String numberStr = matcher.group(2);
        String suffix = matcher.group(3);
        double value = parseValue(numberStr, suffix);
        if (type.contains("Organic Matter")) {
          composterstate.organicMatter = value;
          foundData = true;
        } else if (type.contains("Fuel")) {
          composterstate.fuel = value;
          foundData = true;
        }
      }
    }
    if (foundData) {
      composterstate.lastTimestamp = System.currentTimeMillis();
      ComposterDataManager.instance.save(composterstate);
      System.out.println("COMP_TEST: Updated! Time Remaining: " + calculateTimeRemaining(composterstate));
    }
  }

  private double parseValue(String numberStr, String suffix) {
    try {
      double val = Double.parseDouble(numberStr);
      switch (suffix) {
        case "k": return val * 1000;
        case "M": return val * 1000000;
        default: return val;
      }
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private String formatTime(double totalSeconds) {
    if (totalSeconds <= 0) {
      return "INACTIVE";
    }
    int hours = (int) totalSeconds / 3600;
    int minutes = (int) (totalSeconds % 3600) / 60;
    int seconds = (int) totalSeconds % 60;
    return String.format("%d:%02d:%02d", hours, minutes, seconds);
  }

  private String calculateTimeRemaining(ComposterState state) {
    double speedMultiplier = 1 + (state.speedLevel * 0.2);
    double costReductionMultiplier = 1 - (state.costLevel * 0.1);

    double timePerCompost = BASE_TIME / speedMultiplier;
    double matterCost = BASE_MATTER_COST * costReductionMultiplier;
    double fuelCost = BASE_FUEL_COST * costReductionMultiplier;


    double totalMatterTime = (state.organicMatter / matterCost) * timePerCompost;
    double totalFuelTime = (state.fuel / fuelCost) * timePerCompost;

    double minTime = Math.min(totalMatterTime, totalFuelTime);

    double timeElapsed = (System.currentTimeMillis() - state.lastTimestamp) / 1000.0;
    return formatTime(minTime - timeElapsed);
  }
}