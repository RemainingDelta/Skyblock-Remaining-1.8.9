package com.remainingdelta.skyblockremaining; // UPDATED THIS LINE

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = SkyblockRemaining.MODID, version = SkyblockRemaining.VERSION)
public class SkyblockRemaining {

  public static final String MODID = "skyblockremaining";
  public static final String VERSION = "1.0";

  public static boolean enabled = true;
  public static List<TodoItem> todoList = new ArrayList<TodoItem>();

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);

    todoList.add(new ComposterTracker());

    System.out.println("Skyblock Remaining initialized!");
  }

  @SubscribeEvent
  public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    Minecraft.getMinecraft().thePlayer.addChatMessage(
        new ChatComponentText(EnumChatFormatting.GOLD + "[Skyblock Remaining] " + EnumChatFormatting.GREEN + "Mod Loaded!")
    );
  }
}