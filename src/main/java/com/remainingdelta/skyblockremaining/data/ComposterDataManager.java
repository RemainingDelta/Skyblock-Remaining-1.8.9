package com.remainingdelta.skyblockremaining.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ComposterDataManager {
  private final File composterFile;
  private final Gson gson;
  public static final ComposterDataManager instance = new ComposterDataManager();

  public ComposterDataManager() {
    this.composterFile = new File(Minecraft.getMinecraft().mcDataDir,
        "config/skyblock_composter.json");
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  public void save(ComposterState state) {
    try (FileWriter writer = new FileWriter(this.composterFile)) {
      gson.toJson(state, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public ComposterState load() {
    if (!this.composterFile.exists()) {
      return new ComposterState();
    }
    try (FileReader reader = new FileReader(this.composterFile)) {
      return gson.fromJson(reader, ComposterState.class);
    } catch (IOException e) {
      e.printStackTrace();
      return new ComposterState();
    }
  }
}
