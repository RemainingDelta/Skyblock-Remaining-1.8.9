package com.remainingdelta.skyblockremaining.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Saves and loads Split or Steal Data.
 */
public class SplitOrStealDataManager implements IDataManager<SplitOrStealState> {

  private final File dataFile;
  private final Gson gson;

  /**
   * Constructor that uses the provided directory to save the composter.json file.
   *
   * @param configDir The mod's specific config directory (config/skyblock-remaining/).
   */
  public SplitOrStealDataManager(File configDir) {
    this.dataFile = new File(configDir, "split_or_steal_data.json");
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }


  /**
   * Saves the split or steal state to storage.
   *
   * @param state The split or steal state to save.
   */
  @Override
  public void save(SplitOrStealState state) {
    try (FileWriter writer = new FileWriter(dataFile)) {
      gson.toJson(state, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads the split or steal state from storage.
   *
   * @return The loaded split or steal state
   */
  @Override
  public SplitOrStealState load() {
    if (!dataFile.exists()) {
      return new SplitOrStealState();
    }
    try (FileReader reader = new FileReader(dataFile)) {
      return gson.fromJson(reader, SplitOrStealState.class);
    } catch (IOException e) {
      e.printStackTrace();
      return new SplitOrStealState();
    }
  }
}