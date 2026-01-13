package com.remainingdelta.skyblockremaining.gui;

import com.remainingdelta.skyblockremaining.SkyblockRemaining;
import com.remainingdelta.skyblockremaining.TodoItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * HUD render for todo overlay.
 */
public class HudRenderer extends Gui {

  private static final int BACKGROUND_COLOR = 0x90202020;
  private static final int TITLE_COLOR = 0xFF55FFFF;
  private static final int VALUE_COLOR = 0xFFFFFFFF;

  private static final int ICON_SIZE = 16;
  private static final int ICON_PADDING = 4;

  private final List<TodoItem> todoItems;

  /**
   * Constructor that accepts the list of items to draw.
   *
   * @param todoItems The shared list of tracker items.
   */
  public HudRenderer(List<TodoItem> todoItems) {
    this.todoItems = todoItems;
  }

  /**
   * Render todo overlay.
   *
   * @param event gives information about what Minecraft is currently drawing
   */
  @SubscribeEvent
  public void onRenderGui(RenderGameOverlayEvent.Post event) {
    if (event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
    if (!isOnSkyblock()) return;

    Minecraft mc = Minecraft.getMinecraft();
    List<TodoItem> list = SkyblockRemaining.todoList;
    if (list.isEmpty()) return;

    FontRenderer fr = mc.fontRendererObj;
    int padding = 5;
    int lineHeight = Math.max(fr.FONT_HEIGHT, ICON_SIZE) + 4;

    int boxWidth = 0;
    int enabledCount = 0;

    for (TodoItem item : list) {
      if (!item.isEnabled()) continue;

      enabledCount++;

      String text = item.getName() + ": " + item.getStatus();
      int totalWidth = ICON_SIZE + ICON_PADDING + fr.getStringWidth(text);
      if (totalWidth > boxWidth) boxWidth = totalWidth;
    }

    if (enabledCount == 0) {
      return;
    }

    boxWidth += (padding * 2);
    int boxHeight = (enabledCount * lineHeight) + (padding * 2);

    int startX = SkyblockRemaining.guiX;
    int startY = SkyblockRemaining.guiY;

    drawRect(startX, startY, startX + boxWidth, startY + boxHeight, BACKGROUND_COLOR);

    int currentY = startY + padding;

    for (TodoItem item : list) {
      if (!item.isEnabled()) continue;

      ResourceLocation icon = item.getIcon();
      if (icon != null) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(icon);
        Gui.drawModalRectWithCustomSizedTexture(startX + padding, currentY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
      }

      int textStartX = startX + padding + ICON_SIZE + ICON_PADDING;
      int textOffsetY = (lineHeight - fr.FONT_HEIGHT) / 2;
      int textY = currentY + textOffsetY;

      String name = item.getName() + ": ";
      String status = item.getStatus();

      fr.drawString(name, textStartX, textY, TITLE_COLOR);
      fr.drawString(status, textStartX + fr.getStringWidth(name), textY, VALUE_COLOR);

      currentY += lineHeight;
    }
  }

  /**
   * Checks if the player is currently on Hypixel Skyblock by looking at the sidebar scoreboard
   * title.
   */
  private boolean isOnSkyblock() {
    Minecraft mc = Minecraft.getMinecraft();
    if (mc.theWorld == null || mc.thePlayer == null) {
      return false;
    }
    Scoreboard scoreboard = mc.theWorld.getScoreboard();
    if (scoreboard == null) {
      return false;
    }
    ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
    if (objective == null) {
      return false;
    }
    String title = objective.getDisplayName();
    String cleanTitle = EnumChatFormatting.getTextWithoutFormattingCodes(title);
    return cleanTitle != null && cleanTitle.toUpperCase().contains("SKYBLOCK");
  }
}