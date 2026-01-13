package com.remainingdelta.skyblockremaining;

import net.minecraft.util.ResourceLocation;

/**
 * Interface for the todo item for the todo overlay.
 */
public interface TodoItem {

  /**
   * Gets the display name of the todo item.
   *
   * @return the display name of the todo item
   */
  public String getName();

  /**
   * Gets the current status of the todo item.
   *
   * @return the current status of the todo item
   */
  public String getStatus();

  /**
   * Returns the icon location of the todo item.
   *
   * @return the icon of the todo item
   */
  ResourceLocation getIcon();

  /**
   * Sets todoItem as enabled to display.
   *
   * @param display sets enabled to this value
   */
  void setEnabled(boolean display);

  /**
   * Checks if todoItem is enabled.
   *
   * @return true if todoItem is enabled, false otherwise
   */
  boolean isEnabled();

}
