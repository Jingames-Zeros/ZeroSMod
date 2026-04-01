package org.darkoro.guiapi.guis.clientside.Spirit;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import org.darkoro.zerosmod.proxy.ClientProxy;

public class ClientSpiritHudCommand extends CommandBase {

  @Override
  public String getCommandName() {
    return "spirithud";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/spirithud";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    ClientProxy.openHealthMenu();
  }

  @Override
  public boolean canCommandSenderUseCommand(ICommandSender sender) {
    return true;
  }
}
