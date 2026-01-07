package com.remainingdelta.skyblockremaining.commands;

import com.remainingdelta.skyblockremaining.api.ApiKeyManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * Test commands to test the api.
 */
public class TestCommand extends CommandBase {

  /**
   * Gets the name of the command.
   */
  @Override
  public String getCommandName() {
    return "testapi";
  }

  /**
   * Gets the usage string for the command.
   *
   * @param sender of the command
   */
  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/testapi";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  /**
   * Callback when the command is invoked
   *
   * @param sender of the command
   * @param args of the command
   */
  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    String hypixelApiKey = ApiKeyManager.getHypixelApiKey();
    String uuid = ApiKeyManager.getUuid();

    if (hypixelApiKey != null && uuid != null) {
      sender.addChatMessage(new ChatComponentText("Hypixel API Key " + hypixelApiKey));
      sender.addChatMessage(new ChatComponentText("UUID Key " + uuid));
    } else {
      sender.addChatMessage(new ChatComponentText("Invalid API key or UUID"));
    }
  }
}
