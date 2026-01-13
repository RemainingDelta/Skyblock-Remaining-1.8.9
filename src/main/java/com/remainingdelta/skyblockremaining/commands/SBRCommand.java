package com.remainingdelta.skyblockremaining.commands;

import com.remainingdelta.skyblockremaining.gui.SBRMenuGui;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Adds the `/sbr` command to game which opens up a configuration menu,
 */
public class SBRCommand extends CommandBase {

  /**
   * Gets the name of the command.
   */
  @Override
  public String getCommandName() {
    return "sbr";
  }

  /**
   * Gets the usage string for the command.
   *
   * @param sender of the command
   */
  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/sbr";
  }

  /**
   * Lets normal players use the command.
   */
  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  /**
   * Delays GUI opening to the next client tick.
   *
   * @param sender of the command
   * @param args additional args of the command
   */
  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    MinecraftForge.EVENT_BUS.register(this);
  }

  /**
   * Waits for the end of the next tick to safely open the GUI on the main thread.
   *
   * @param event The client tick event.
   */
  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      Minecraft.getMinecraft().displayGuiScreen(new SBRMenuGui());
      MinecraftForge.EVENT_BUS.unregister(this);
    }
  }
}
