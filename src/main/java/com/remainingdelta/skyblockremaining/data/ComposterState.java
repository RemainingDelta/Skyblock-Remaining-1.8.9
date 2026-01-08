package com.remainingdelta.skyblockremaining.data;

/**
 * Holds the state of the Composter.
 */
public class ComposterState {
  public double organicMatter = 0;
  public double fuel = 0;
  public long lastTimestamp = 0;
  public int speedLevel = 0;
  public int costLevel = 0;

  /**
   * Returns the values of the matter and fuel as a string.
   *
   * @return the values of the matter and fuel as a string
   */
  @Override
  public String toString() {
    return ("Matter: " + this.organicMatter + ", Fuel: " + this.fuel);
  }
}
