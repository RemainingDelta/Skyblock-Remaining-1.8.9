package com.remainingdelta.skyblockremaining.gui;

import com.remainingdelta.skyblockremaining.SkyblockRemaining;
import com.remainingdelta.skyblockremaining.TodoItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class HudRenderer extends Gui {

  private static final int START_X = 10;
  private static final int START_Y = 10;

  private static final int BACKGROUND_COLOR = 0x90202020;
  private static final int TITLE_COLOR = 0xFF55FFFF;
  private static final int VALUE_COLOR = 0xFFFFFFFF;

  private static final int ICON_SIZE = 16;
  private static final int ICON_PADDING = 4;

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
    int lineHeight = Math.max(fr.FONT_HEIGHT, ICON_SIZE) + 4;

    int boxHeight = (list.size() * lineHeight) + (padding * 2);
    int boxWidth = 0;

    for (TodoItem item : list) {
      String text = item.getName() + ": " + item.getStatus();
      int textWidth = fr.getStringWidth(text);

      int totalWidth = ICON_SIZE + ICON_PADDING + textWidth;

      if (totalWidth > boxWidth) {
        boxWidth = totalWidth;
      }
    }
    boxWidth += (padding * 2);

    drawRect(START_X, START_Y, START_X + boxWidth, START_Y + boxHeight, BACKGROUND_COLOR);

    int currentY = START_Y + padding;

    for (TodoItem item : list) {
      ResourceLocation icon = item.getIcon();

      if (icon != null) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(icon);

        Gui.drawModalRectWithCustomSizedTexture(START_X + padding, currentY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
      }

      int textStartX = START_X + padding + ICON_SIZE + ICON_PADDING;

      int textOffsetY = (lineHeight - fr.FONT_HEIGHT) / 2;
      int textY = currentY + textOffsetY;

      String name = item.getName() + ": ";
      String status = item.getStatus();

      fr.drawString(name, textStartX, textY, TITLE_COLOR);

      int nameWidth = fr.getStringWidth(name);
      fr.drawString(status, textStartX + nameWidth, textY, VALUE_COLOR);

      currentY += lineHeight;
    }
  }
}