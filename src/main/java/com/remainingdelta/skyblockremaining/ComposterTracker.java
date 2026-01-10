package com.remainingdelta.skyblockremaining;

import com.remainingdelta.skyblockremaining.data.ComposterDataManager;
import com.remainingdelta.skyblockremaining.data.ComposterState;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Tracker for the composter for the todo menu.
 */
public class ComposterTracker extends AbstractTodoItem {
  private int tickCounter = 0;
  public static ComposterTracker instance;
  private ComposterState cachedState;
  private static final Pattern composterPattern = Pattern.compile(
      "(Organic Matter|Fuel):\\s*([\\d\\.]+)([kM]?)");
  private static final Pattern timeLeftPattern = Pattern.compile(
      "Time Left:\\s*(\\d+)m\\s*(\\d+)s");
  private static final double BASE_MATTER_COST = 4000.0;
  private static final double BASE_FUEL_COST = 2000.0;
  private static final double BASE_TIME = 600.0;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private long lastApiFetchTime = 0;


  /**
   * Constructor of Composter Tracker which takes in nothing.
   */
  public ComposterTracker() {
    super("Composter", "textures/icons/composter.png");
    instance = this;
    this.cachedState = ComposterDataManager.instance.load();
    this.scheduler.scheduleAtFixedRate(this::fetchComposterData, 5, 5, TimeUnit.MINUTES);
  }

  /**
   * Gets the current status of the Composter.
   *
   * @return the current status of the todo item
   */
  @Override
  public String getStatus() {
    return calculateTimeRemaining(this.cachedState);
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }
    this.tickCounter++;
    if (this.tickCounter < 60) {
      return;
    }
    this.tickCounter = 0;
    System.out.println("[DEBUG] Current Status: " + calculateTimeRemaining(this.cachedState)
        + " | Matter: " + this.cachedState.organicMatter
        + " | Fuel: " + this.cachedState.fuel);
    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.thePlayer == null || minecraft.theWorld == null) {
      return;
    }
    this.parseTabList(minecraft);
  }

  private void parseTabList(Minecraft minecraft) {
    Collection<NetworkPlayerInfo> tabListData = minecraft.getNetHandler().getPlayerInfoMap();
    boolean foundData = false;
    boolean currentPassInactive = false;

    double previousOrganic = this.cachedState.organicMatter;
    double previousFuel = this.cachedState.fuel;
    boolean previousInactive = this.cachedState.isInactive;
    long previousCycleTime = this.cachedState.cycleTimeSeconds;

    long foundCycleTime = -1;

    for (NetworkPlayerInfo playerInfo : tabListData) {
      IChatComponent displayName = playerInfo.getDisplayName();
      if (displayName == null) {
        continue;
      }
      String text = displayName.getUnformattedText().replace(",", "");
      if (text.contains("INACTIVE") && !text.contains("Bonus")) {
        currentPassInactive = true;
        foundData = true;
      }
      Matcher matcher = composterPattern.matcher(text);
      if (matcher.find()) {
        String type = matcher.group(1);
        String numberStr = matcher.group(2);
        String suffix = matcher.group(3);
        double value = parseValue(numberStr, suffix);
        if (type.contains("Organic Matter")) {
          this.cachedState.organicMatter = value;
          foundData = true;
        } else if (type.contains("Fuel")) {
          this.cachedState.fuel = value;
          foundData = true;
        }
      }

      Matcher timeMatcher = timeLeftPattern.matcher(text);
      if (timeMatcher.find()) {
        int mins = Integer.parseInt(timeMatcher.group(1));
        int secs = Integer.parseInt(timeMatcher.group(2));
        foundCycleTime = (mins * 60L) + secs;
        foundData = true;
      }
    }
    if (foundData) {
      this.cachedState.isInactive = currentPassInactive;
      if (foundCycleTime != -1) {
        this.cachedState.cycleTimeSeconds = foundCycleTime;
      }
      if (this.cachedState.organicMatter != previousOrganic
          || this.cachedState.fuel != previousFuel
          || this.cachedState.isInactive != previousInactive
          || this.cachedState.cycleTimeSeconds != previousCycleTime) {
        this.cachedState.lastTimestamp = System.currentTimeMillis();
        ComposterDataManager.instance.save(this.cachedState);
        System.out.println("COMP_TEST: Updated! Time Remaining: " + calculateTimeRemaining(this.cachedState));
      }
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
      return EnumChatFormatting.RED + "INACTIVE";
    }
    int hours = (int) totalSeconds / 3600;
    int minutes = (int) (totalSeconds % 3600) / 60 + 1;
    return EnumChatFormatting.WHITE + String.format("%dh%02dm", hours, minutes);
  }

  private String calculateTimeRemaining(ComposterState state) {
    if (state.isInactive) {
      return EnumChatFormatting.RED + "INACTIVE";
    }

    double speedMultiplier = 1 + (state.speedLevel * 0.2);
    double costReductionMultiplier = 1 - (state.costLevel * 0.01);

    double timePerCompost = BASE_TIME / speedMultiplier;
    double matterCost = BASE_MATTER_COST * costReductionMultiplier;
    double fuelCost = BASE_FUEL_COST * costReductionMultiplier;

    int maxMatterCycles = (int) (state.organicMatter / matterCost);
    int maxFuelCycles = (int) (state.fuel / fuelCost);
    int totalFullCycles = Math.min(maxMatterCycles, maxFuelCycles);

    double totalTimeSeconds;

    if (state.cycleTimeSeconds > 0) {
      double futureTime = Math.max(0, totalFullCycles - 1) * timePerCompost;
      totalTimeSeconds = state.cycleTimeSeconds + futureTime;
    } else {
      totalTimeSeconds = totalFullCycles * timePerCompost;
    }

    double timeElapsed = (System.currentTimeMillis() - state.lastTimestamp) / 1000.0;
    return formatTime(totalTimeSeconds - timeElapsed);
  }

  private void fetchComposterData() {
    if (System.currentTimeMillis() - lastApiFetchTime < 15000) {
      return;
    }
    lastApiFetchTime = System.currentTimeMillis();
    new Thread(() -> {
      try {
        String key = com.remainingdelta.skyblockremaining.api.ApiKeyManager.getHypixelApiKey();
        if (key == null || key.isEmpty()) {
          return;
        }
        com.google.gson.JsonObject gardenData =
            com.remainingdelta.skyblockremaining.api.HypixelApi.getGardenData();
        if (gardenData != null && gardenData.has("composter_data")) {
          this.cachedState.updateUpgradesFromApi(gardenData.getAsJsonObject(
              "composter_data"));
          System.out.println("[SkyblockRemaining] API Sync Complete: "
              + this.cachedState.toString());
          ComposterDataManager.instance.save(this.cachedState);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).start();
  }

  @SubscribeEvent
  public void onWorldJoin(EntityJoinWorldEvent event) {
    if (event.entity == Minecraft.getMinecraft().thePlayer) {
      fetchComposterData();
    }
  }
}