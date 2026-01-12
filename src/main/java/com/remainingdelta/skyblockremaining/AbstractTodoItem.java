package com.remainingdelta.skyblockremaining;

import net.minecraft.util.ResourceLocation;

/**
 * Represents an abstract todo item in skyblock.
 */
public abstract class AbstractTodoItem implements TodoItem {
  protected String name;
  protected ResourceLocation icon;

  /**
   * Constructor of TodoItem which takes in a name.
   *
   * @param name represents the name of the todo item
   */
  public AbstractTodoItem(String name, String iconLocation) {
    this.name = name;
    this.icon = new ResourceLocation("skyblockremaining", iconLocation);
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

}
