package com.remainingdelta.skyblockremaining;

import net.minecraft.util.ResourceLocation;

/**
 * Represents an abstract todo item in skyblock.
 */
public abstract class AbstractTodoItem implements TodoItem {
  protected String name;
  protected ResourceLocation icon;
  protected boolean enabled;

  /**
   * Constructor of TodoItem which takes in a name.
   *
   * @param name represents the name of the todo item
   */
  public AbstractTodoItem(String name, String iconLocation) {
    this.name = name;
    this.icon = new ResourceLocation("skyblockremaining", iconLocation);
    this.enabled = true;
  }

  /**
   * Gets the display name of the todo item.
   *
   * @return the display name of the todo item
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * Returns the icon location of the todo item.
   *
   * @return the icon of the todo item
   */
  @Override
  public ResourceLocation getIcon() {
    return this.icon;
  }

  /**
   * Sets todoItem as enabled to display.
   *
   * @param display sets enabled to this value
   */
  @Override
  public void setEnabled(boolean display) {
    this.enabled = display;
  }

  /**
   * Checks if todoItem is enabled.
   *
   * @return true if todoItem is enabled, false otherwise
   */
  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

}
