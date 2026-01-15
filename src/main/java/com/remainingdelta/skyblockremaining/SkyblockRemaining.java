package com.remainingdelta.skyblockremaining;

import com.remainingdelta.skyblockremaining.api.ApiKeyManager;
import com.remainingdelta.skyblockremaining.api.HypixelApi;
import com.remainingdelta.skyblockremaining.api.IApiKeyManager;
import com.remainingdelta.skyblockremaining.api.IHypixelApi;
import com.remainingdelta.skyblockremaining.commands.SBRCommand;
import com.remainingdelta.skyblockremaining.config.ConfigManager;
import com.remainingdelta.skyblockremaining.config.IConfigManager;
import com.remainingdelta.skyblockremaining.data.ComposterDataManager;
import com.remainingdelta.skyblockremaining.data.ComposterState;
import com.remainingdelta.skyblockremaining.data.IDataManager;
import com.remainingdelta.skyblockremaining.gui.HudRenderer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * The main entry point for the Skyblock Remaining mod.
 */
@Mod(modid = SkyblockRemaining.MODID, version = SkyblockRemaining.VERSION)
public class SkyblockRemaining {

  public static final String MODID = "skyblockremaining";
  public static final String VERSION = "1.0";

  public static boolean enabled = true;
  public static List<TodoItem> todoList = new ArrayList<TodoItem>();

  public static IConfigManager configManager;

  public static int guiX = 10;
  public static int guiY = 10;

  /**
   * The main initialization event for the mod.
   *
   * @param event The FML initialization event.
   */
  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);
    File minecraftDir = net.minecraft.client.Minecraft.getMinecraft().mcDataDir;
    configManager = new ConfigManager(minecraftDir);
    File modConfigDir = configManager.getConfigDirectory();
    IApiKeyManager keyManager = new ApiKeyManager(modConfigDir);
    IHypixelApi apiService = new HypixelApi(keyManager);
    IDataManager<ComposterState> dataManager = new ComposterDataManager(modConfigDir);
    ComposterTracker composter = new ComposterTracker(keyManager, apiService, dataManager);
    todoList.add(composter);
    configManager.loadConfig();
    MinecraftForge.EVENT_BUS.register(composter);
    MinecraftForge.EVENT_BUS.register(new HudRenderer(todoList));
    ClientCommandHandler.instance.registerCommand(new SBRCommand());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println("Stopping SkyblockRemaining...");
      composter.shutdown();
    }));
    System.out.println("Skyblock Remaining initialized!");
  }

  /**
   * Triggered when the player logs into the server. Sends a welcome message to the chat to confirm
   * to the user that the mod has loaded successfully and is active.
   *
   * @param event The player login event.
   */
  @SubscribeEvent
  public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    event.player.addChatMessage(
        new ChatComponentText(EnumChatFormatting.GOLD + "[Skyblock Remaining] " + EnumChatFormatting.GREEN + "Mod Loaded!")
    );
  }
}