package com.remainingdelta.skyblockremaining;

import com.google.gson.JsonObject;
import com.remainingdelta.skyblockremaining.api.IApiKeyManager;
import com.remainingdelta.skyblockremaining.api.IHypixelApi;
import com.remainingdelta.skyblockremaining.data.ComposterState;
import com.remainingdelta.skyblockremaining.data.IDataManager;
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
 * Tracker for the composter for the todo menu. Calculates the time left for the composter,
 * otherwise is "INACTIVE".
 */
public class ComposterTracker extends AbstractTodoItem {

  /* Constants */
  private static final Pattern COMPOSTER_PATTERN = Pattern.compile(
      "(Organic Matter|Fuel):\\s*([\\d\\.]+)([kM]?)");
  private static final Pattern TIME_LEFT_PATTERN = Pattern.compile(
      "Time Left:\\s*(\\d+)m\\s*(\\d+)s");
  private static final double BASE_MATTER_COST = 4000.0;
  private static final double BASE_FUEL_COST = 2000.0;
  private static final double BASE_TIME = 600.0;

  /* Instance Variables */
  public static ComposterTracker instance;
  private volatile ComposterState cachedState;
  private int tickCounter = 0;

  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
      1, runnable -> {
    Thread t = new Thread(runnable);
    t.setDaemon(true);
    t.setName("Composter-Tracker-Thread");
    return t;
  });
  private long lastApiFetchTime = 0;

  private final IApiKeyManager apiKeyManager;
  private final IHypixelApi apiService;
  private final IDataManager<ComposterState> dataManager;


  /**
   * Constructs a new ComposterTracker with the required dependencies. Initializes the tracker,
   * loads the saved state from disk,and starts a background scheduler to fetch data from the
   * Hypixel API every 5 minutes.
   *
   * @param apiKeyManager The manager responsible for providing the Hypixel API key
   * @param apiService    The service used to make network requests to the Hypixel API
   * @param dataManager   The manager used to save and load the composter state from the disk
   */
  public ComposterTracker(IApiKeyManager apiKeyManager, IHypixelApi apiService,
                          IDataManager<ComposterState> dataManager) {
    super("Composter", "textures/icons/composter.png");
    this.apiKeyManager = apiKeyManager;
    this.apiService = apiService;
    this.dataManager = dataManager;
    instance = this;
    this.cachedState = dataManager.load();
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

  /**
   * Called on every client tick. Handles the periodic scraping of the tab list to update composter
   * data.
   *
   * @param event The client tick event provided by Forge.
   */
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
    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.thePlayer == null || minecraft.theWorld == null) {
      return;
    }
    this.parseTabList(minecraft);
  }

  /**
   * Scans the current tab list to extract Composter data such as Organic Matter, Fuel, and Time
   * Left.
   *
   * @param minecraft The Minecraft client instance used to access the network handler.
   */
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
      Matcher matcher = COMPOSTER_PATTERN.matcher(text);
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

      Matcher timeMatcher = TIME_LEFT_PATTERN.matcher(text);
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
        this.dataManager.save(this.cachedState);
        System.out.println("COMP_TEST: Updated! Time Remaining: " + calculateTimeRemaining(this.cachedState));
      }
    }
  }

  /**
   * Parse the fuel and matter values accounting for k or M to return final amount.
   *
   * @param numberStr number part of the string
   * @param suffix letter part of the string
   * @return the final amount based on the number and suffix
   */
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

  /**
   * Formats a duration in seconds into a human-readable string (e.g., "1h05m")
   *
   * @param totalSeconds seconds to format
   * @return formated time as a string
   */
  private String formatTime(double totalSeconds) {
    if (totalSeconds <= 0) {
      return EnumChatFormatting.RED + "INACTIVE";
    }
    int hours = (int) totalSeconds / 3600;
    int minutes = (int) (totalSeconds % 3600) / 60 + 1;
    return EnumChatFormatting.WHITE + String.format("%dh%02dm", hours, minutes);
  }

  /**
   * Calculates the remaining time for the composter based on the current state.
   *
   * @param state of the composter
   * @return time left as a string or INACTIVE if inactive.
   */
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

  /**
   * Fetches the composter data, updating the upgrade levels with the given json.
   */
  private void fetchComposterData() {
    if (System.currentTimeMillis() - lastApiFetchTime < 15000) {
      return;
    }
    lastApiFetchTime = System.currentTimeMillis();
    try {
      String key = this.apiKeyManager.getHypixelApiKey();
      if (key == null || key.isEmpty()) return;
      JsonObject gardenData = this.apiService.getGardenData();
      if (gardenData != null && gardenData.has("composter_data")) {
        this.cachedState.updateUpgradesFromApi(gardenData.getAsJsonObject("composter_data"));
        // System.out.println("[SkyblockRemaining] API Sync Complete");
        this.dataManager.save(this.cachedState);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Detects when the player joins a world and triggers an immediate API sync.
   *
   * @param event The entity join event provided by Forge.
   */
  @SubscribeEvent
  public void onWorldJoin(EntityJoinWorldEvent event) {
    if (event.entity == Minecraft.getMinecraft().thePlayer) {
      this.scheduler.submit(this::fetchComposterData);
    }
  }

  /**
   * Called on mod shutdown
   */
  public void shutdown() {
    this.scheduler.shutdown();
  }
}