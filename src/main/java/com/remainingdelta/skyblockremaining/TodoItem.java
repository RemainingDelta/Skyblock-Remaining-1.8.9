package com.remainingdelta.skyblockremaining;

/**
 * Represents a todo item in skyblock.
 */
public abstract class TodoItem {
  private String name;

  /**
   * Constructor of TodoItem which takes in a name.
   *
   * @param name represents the name of the todo item
   */
  public TodoItem(String name) {
    this.name = name;
  }

  /**
   * Gets the current status of the todo item.
   *
   * @return the current status of the todo item
   */
  public abstract String getStatus();
}
