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
    return ComposterDataManager.instance.load().toString();
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
}