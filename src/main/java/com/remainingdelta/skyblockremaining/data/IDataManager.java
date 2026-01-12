package com.remainingdelta.skyblockremaining.data;

/**
 * A generic interface for saving and loading data.
 *
 * @param <T> The type of object being saved (e.g. ComposterState)
 */
public interface IDataManager<T> {

  /**
   * Saves the data object to storage.
   *
   * @param data The object to save.
   */
  void save(T data);

  /**
   * Loads the data object from storage.
   *
   * @return The loaded object.
   */
  T load();
}
