package com.remainingdelta.skyblockremaining;

/**
 * Tracker for the composter for the todo menu.
 */
public class ComposterTracker extends TodoItem {

  /**
   * Constructor of Composter Tracker which takes in nothing.
   */
  public ComposterTracker() {
    super("Composter");
  }

  /**
   * Gets the current status of the Composter.
   *
   * @return the current status of the todo item
   */
  @Override
  public String getStatus() {
    return "Ready!";
  }
}