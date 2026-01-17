package com.remainingdelta.skyblockremaining.commands;

import com.remainingdelta.skyblockremaining.SkyblockRemaining;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * Command Class for saving api key with `/sbrapikey <apikey>`.
 */
public class CommandApiKey extends CommandBase {
  /**
   * Gets the name of the command
   */
  @Override
  public String getCommandName() {
    return "sbrapikey";
  }

  /**
   * Gets the usage string for the command.
   *
   * @param sender of the command
   */
  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/sbrapikey <apikey>";
  }

  /**
   * Lets normal players use the command.
   */
  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  /**
   * Callback when the command is invoked
   *
   * @param sender of the command
   * @param args additional args of the command
   */
  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    if (args.length < 1) {
      sender.addChatMessage(new ChatComponentText(
          EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
      return;
    }

    String apikey = args[0];
    if (SkyblockRemaining.keyManager != null) {
      SkyblockRemaining.keyManager.setApiKey(apikey);
      sender.addChatMessage(new ChatComponentText(
          EnumChatFormatting.GOLD + "[SkyblockRemaining] " +
              EnumChatFormatting.GREEN + "API Key updated successfully!"
      ));
    } else {
      sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Error: KeyManager is not initialized."));
    }
  }
}
