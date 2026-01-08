package com.remainingdelta.skyblockremaining;

/**
 * Represents a todo item in skyblock.
 */
public abstract class AbstractTodoItem implements TodoItem {
  private String name;
  /**
   * Constructor of TodoItem which takes in a name.
   *
   * @param name represents the name of the todo item
   */
  public AbstractTodoItem(String name) {
    this.name = name;
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
}
