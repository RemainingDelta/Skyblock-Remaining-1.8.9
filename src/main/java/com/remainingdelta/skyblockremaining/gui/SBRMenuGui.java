package com.remainingdelta.skyblockremaining.gui;

import com.remainingdelta.skyblockremaining.SkyblockRemaining;
import com.remainingdelta.skyblockremaining.TodoItem;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

/**
 * Main Menu GUI for config.
 */
public class SBRMenuGui extends GuiScreen {
  private static final int BUTTON_MOVE_GUI = 0;
  private static final int BUTTON_CLOSE = 1;
  private static final int LIST_BUTTON_START_ID = 10;

  /**
   * Initializes the GUI screen.
   */
  @Override
  public void initGui() {
    super.initGui();
    this.buttonList.clear();
    int centerX = this.width / 2;
    int startY = this.height / 4;
    this.buttonList.add(new GuiButton(BUTTON_MOVE_GUI, centerX - 100, startY, 200, 20, "Move HUD Elements"));

    int currentY = startY + 25;

    if (SkyblockRemaining.todoList != null) {
      for (int i = 0; i < SkyblockRemaining.todoList.size(); i++) {
        TodoItem item = SkyblockRemaining.todoList.get(i);
        int buttonId = LIST_BUTTON_START_ID + i;
        String buttonText = getButtonText(item);
        this.buttonList.add(new GuiButton(buttonId, centerX - 100, currentY, 200, 20, buttonText));
        currentY += 25;
      }
    }
    this.buttonList.add(new GuiButton(BUTTON_CLOSE, centerX - 100, currentY + 10, 200, 20, "Close"));
  }

  /**
   * Handles static buttons (Move GUI, Close) explicitly.
   *
   * @param button The button that was clicked
   * @throws IOException If an input/output error occurs
   */
  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.id == BUTTON_MOVE_GUI) {
      mc.displayGuiScreen(new GuiEditHud());
      return;
    }
    if (button.id == BUTTON_CLOSE) {
      mc.displayGuiScreen(null);
      return;
    }
    if (button.id >= LIST_BUTTON_START_ID) {
      int index = button.id - LIST_BUTTON_START_ID;

      // Safety check to ensure the list hasn't changed since the GUI opened
      if (index >= 0 && index < SkyblockRemaining.todoList.size()) {
        TodoItem item = SkyblockRemaining.todoList.get(index);

        item.setEnabled(!item.isEnabled());
        button.displayString = getButtonText(item);
      }
    }
  }

  /**
   * Formats the text for todoItem buttons as "Name: ON" (Green) or "Name: OFF" (Red).
   *
   * @param item The TodoItem to generate text for
   * @return The formatted string to display on the button
   */
  private String getButtonText(TodoItem item) {
    String status = item.isEnabled() ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
    return item.getName() + ": " + status;
  }

  /**
   * Draws the screen and all its components.
   *
   * @param mouseX       The current x position of the mouse
   * @param mouseY       The current y position of the mouse
   * @param partialTicks The partial tick time
   */
  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);
    this.drawCenteredString(this.fontRendererObj, "Skyblock Remaining Settings", this.width / 2, 20, 0xFFFFFF);
  }

  /**
   * Determines if the game should pause while this GUI is open (always returns false).
   *
   * @return false, allowing the game (and server connection) to continue running.
   */
  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }
}
