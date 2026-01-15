package com.remainingdelta.skyblockremaining.gui;

import com.remainingdelta.skyblockremaining.SkyblockRemaining;
import com.remainingdelta.skyblockremaining.TodoItem;
import net.minecraft.client.gui.GuiScreen;
import java.awt.Color;
import java.io.IOException;
import java.util.List;

/**
 * A screen that displays a preview of the HUD and allows the user to click and drag it to a new
 * position.
 */
public class GuiEditHud extends GuiScreen {

  private boolean dragging = false;
  private int dragOffsetX = 0;
  private int dragOffsetY = 0;

  /**
   * Renders the screen, including the background, the HUD preview, and the white selection outline.
   *
   * @param mouseX       The current X coordinate of the mouse.
   * @param mouseY       The current Y coordinate of the mouse.
   * @param partialTicks The time elapsed since the last tick (used for smooth rendering).
   */
  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    drawCenteredString(fontRendererObj, "Drag the box to move it", width / 2, 20, 0xFFFFFF);
    drawCenteredString(fontRendererObj, "Press ESC to save", width / 2, 35, 0xAAAAAA);

    List<TodoItem> list = SkyblockRemaining.todoList;

    int padding = 5;
    int iconSize = 16;
    int lineHeight = Math.max(fontRendererObj.FONT_HEIGHT, iconSize) + 4;

    int boxWidth = 0;
    int enabledCount = 0;

    for (TodoItem item : list) {
      if (!item.isEnabled()) continue;
      enabledCount++;

      String text = item.getName() + ": " + item.getStatus();
      int totalWidth = iconSize + 4 + fontRendererObj.getStringWidth(text);

      if (totalWidth > boxWidth) boxWidth = totalWidth;
    }

    if (enabledCount == 0) {
      String text = "Mod Overlay (Empty)";
      boxWidth = fontRendererObj.getStringWidth(text);
      enabledCount = 1;
    }

    int totalWidth = boxWidth + (padding * 2);
    int totalHeight = (enabledCount * lineHeight) + (padding * 2);
    int x = SkyblockRemaining.guiX;
    int y = SkyblockRemaining.guiY;
    drawRect(x, y, x + totalWidth, y + totalHeight, 0x90202020);
    int currentY = y + padding;
    if (enabledCount > 0 && !list.isEmpty()) {
      for (TodoItem item : list) {
        if (!item.isEnabled()) continue;
        String text = item.getName() + ": " + item.getStatus();
        fontRendererObj.drawStringWithShadow(text, x + padding + iconSize + 4, currentY + 4, 0xFFFFFF);
        currentY += lineHeight;
      }
    } else {
      fontRendererObj.drawStringWithShadow("Mod Overlay (Empty)", x + padding, y + padding + 4, 0xFFFFFF);
    }
    drawHorizontalLine(x - 1, x + totalWidth, y - 1, Color.WHITE.getRGB());       // Top
    drawHorizontalLine(x - 1, x + totalWidth, y + totalHeight, Color.WHITE.getRGB()); // Bottom
    drawVerticalLine(x - 1, y - 1, y + totalHeight, Color.WHITE.getRGB());        // Left
    drawVerticalLine(x + totalWidth, y - 1, y + totalHeight, Color.WHITE.getRGB()); // Right
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  /**
   * Called when the screen is unloaded (closed). Saves the new position to disk.
   */
  @Override
  public void onGuiClosed() {
    if (SkyblockRemaining.configManager != null) {
      SkyblockRemaining.configManager.saveConfig();
    }
    super.onGuiClosed();
  }

  /**
   * Handles mouse clicks to detect if the user clicked inside the HUD box to start dragging.
   *
   * @param mouseX      The X coordinate of the mouse click.
   * @param mouseY      The Y coordinate of the mouse click.
   * @param mouseButton The ID of the mouse button clicked (0 for left click).
   * @throws IOException If an input/output error occurs during the click handling.
   */
  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if (mouseButton == 0) {
      int width = getBoxWidth();
      int height = getBoxHeight();
      int x = SkyblockRemaining.guiX;
      int y = SkyblockRemaining.guiY;
      if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
        this.dragging = true;
        this.dragOffsetX = mouseX - x;
        this.dragOffsetY = mouseY - y;
      }
    }
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  /**
   * Stops the dragging action when the user releases the mouse button.
   *
   * @param mouseX The X coordinate where the mouse was released.
   * @param mouseY The Y coordinate where the mouse was released.
   * @param state  The state of the mouse button (typically 0 for release).
   */
  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    this.dragging = false;
    super.mouseReleased(mouseX, mouseY, state);
  }

  /**
   * Updates the HUD position globally while the user drags the mouse.
   *
   * @param mouseX             The current X coordinate of the mouse.
   * @param mouseY             The current Y coordinate of the mouse.
   * @param clickedMouseButton The mouse button currently being held down.
   * @param timeSinceLastClick The time in milliseconds since the mouse was clicked.
   */
  @Override
  protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton,
                                long timeSinceLastClick) {
    if (this.dragging) {
      SkyblockRemaining.guiX = mouseX - this.dragOffsetX;
      SkyblockRemaining.guiY = mouseY - this.dragOffsetY;
    }
  }

  /**
   * Determines whether the game should pause while this screen is open.
   *
   * @return false, allowing the game to continue running (essential for multiplayer).
   */
  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  /**
   * Calculates the total width of the HUD box based on the longest enabled item.
   *
   * @return The calculated width of the bounding box in pixels.
   */
  private int getBoxWidth() {
    int padding = 5;
    int iconSize = 16;
    int boxWidth = 0;
    boolean empty = true;

    for (TodoItem item : SkyblockRemaining.todoList) {
      if (!item.isEnabled()) continue;
      empty = false;
      String text = item.getName() + ": " + item.getStatus();
      int w = iconSize + 4 + fontRendererObj.getStringWidth(text);
      if (w > boxWidth) boxWidth = w;
    }
    if (empty) boxWidth = fontRendererObj.getStringWidth("Mod Overlay (Empty)");
    return boxWidth + (padding * 2);
  }

  /**
   * Calculates the total height of the HUD box based on the number of enabled items.
   *
   * @return The calculated height of the bounding box in pixels.
   */
  private int getBoxHeight() {
    int padding = 5;
    int iconSize = 16;
    int lineHeight = Math.max(fontRendererObj.FONT_HEIGHT, iconSize) + 4;
    int count = 0;

    for (TodoItem item : SkyblockRemaining.todoList) {
      if (item.isEnabled()) count++;
    }
    if (count == 0) count = 1;
    return (count * lineHeight) + (padding * 2);
  }
}