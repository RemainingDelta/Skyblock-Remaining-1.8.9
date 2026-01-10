package com.remainingdelta.skyblockremaining.gui;

import com.remainingdelta.skyblockremaining.SkyblockRemaining;
import com.remainingdelta.skyblockremaining.TodoItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.List;

public class HudRenderer extends Gui {

  private static final int START_X = 10;
  private static final int START_Y = 10;

  private static final int BACKGROUND_COLOR = 0x90202020;
  private static final int TITLE_COLOR = 0xFF55FFFF;
  private static final int VALUE_COLOR = 0xFFFFFFFF;

  @SubscribeEvent
  public void onRenderGui(RenderGameOverlayEvent.Post event) {
    if (event.type != RenderGameOverlayEvent.ElementType.TEXT) {
      return;
    }
    Minecraft mc = Minecraft.getMinecraft();
    List<TodoItem> list = SkyblockRemaining.todoList;
    if (list.isEmpty()) return;
    FontRenderer fr = mc.fontRendererObj;
    int padding = 5;
    int lineHeight = fr.FONT_HEIGHT + 2;
    int boxWidth = 0;
    int boxHeight = (list.size() * lineHeight) + (padding * 2);

    for (TodoItem item : list) {
      String text = item.getName() + ": " + item.getStatus();
      int width = fr.getStringWidth(text);
      if (width > boxWidth) {
        boxWidth = width;
      }
    }
    boxWidth += (padding * 2);

    drawRect(START_X, START_Y, START_X + boxWidth, START_Y + boxHeight, BACKGROUND_COLOR);

    int currentY = START_Y + padding;
    for (TodoItem item : list) {
      String name = item.getName() + ": ";
      String status = item.getStatus();

      fr.drawString(name, START_X + padding, currentY, TITLE_COLOR);

      int nameWidth = fr.getStringWidth(name);
      fr.drawString(status, START_X + padding + nameWidth, currentY, VALUE_COLOR);

      currentY += lineHeight;
    }
  }
}