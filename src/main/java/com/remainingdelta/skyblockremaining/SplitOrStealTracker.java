package com.remainingdelta.skyblockremaining;

import com.remainingdelta.skyblockremaining.data.IDataManager;
import com.remainingdelta.skyblockremaining.data.SplitOrStealState;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Tracker for the Rift Split or Steal for the todo menu. Calculates times left until you can play
 * split or steal again.
 */
public class SplitOrStealTracker extends AbstractTodoItem {

  private static final String TRIGGER_PHRASE = "(final): you chose";
  private static final long COOLDOWN_MS = 2 * 60 * 60 * 1000; // 2 Hours

  private final IDataManager<SplitOrStealState> dataManager;
  private SplitOrStealState cachedState;


  /**
   * Constructor of TodoItem which takes in a name.
   *
   * @param dataManager of the RiftSplitOrSteal
   */
  public SplitOrStealTracker(IDataManager<SplitOrStealState> dataManager) {
    super("Split or Steal", "textures/icons/ubiks_cube.png");
    this.dataManager = dataManager;
    this.cachedState = dataManager.load();

    if (this.cachedState == null) {
      this.cachedState = new SplitOrStealState();
    }
  }

  /**
   * Gets the current status of the split or steal.
   *
   * @return the current status of the split or steal
   */
  @Override
  public String getStatus() {
    long now = System.currentTimeMillis();
    if (this.cachedState.targetTimestamp <= now) {
      return EnumChatFormatting.GREEN + "Ready!";
    }
    long remaining = this.cachedState.targetTimestamp - now;
    return formatTime(remaining);
  }

  /**
   * Listens for chat packets to detect the Split/Steal game.
   */
  @SubscribeEvent
  public void onChat(ClientChatReceivedEvent event) {
    if (event.type == 2) {
      return;
    }
    String cleanMessage = event.message.getUnformattedText().toLowerCase();
    if (cleanMessage.contains(TRIGGER_PHRASE)) {
      this.resetTimer();
    }
  }

  /**
   * Sets the timer to 2 hours
   */
  private void resetTimer() {
    long now = System.currentTimeMillis();
    this.cachedState.targetTimestamp = now + COOLDOWN_MS;
    this.dataManager.save(this.cachedState);
  }

  /**
   * Formats the remaining time in milliseconds into a human-readable string (e.g., "1h05m")
   *
   * @param remainingMillis seconds to format
   * @return formated time as a string
   */
  private String formatTime(long remainingMillis) {
    if (remainingMillis <= 0) {
      return EnumChatFormatting.GREEN + "Ready!";
    }
    long hours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(remainingMillis);
    long minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60;
    return EnumChatFormatting.WHITE + String.format("%dh%02dm", hours, minutes);
  }

}
