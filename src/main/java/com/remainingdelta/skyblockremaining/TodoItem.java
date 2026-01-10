package com.remainingdelta.skyblockremaining;

import net.minecraft.util.ResourceLocation;

/**
 * Interface for the todo item.
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
   * Returns the icon of the todo item
   *
   * @return the icon of the todo item
   */
  ResourceLocation getIcon();
}
