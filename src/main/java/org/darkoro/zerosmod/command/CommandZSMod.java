package org.darkoro.zerosmod.command;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.BiomeConfig;
import org.darkoro.zerosmod.event.SaiyanMasteryMergeEvent;
import org.darkoro.zerosmod.network.BiomeVisualSyncUtil;

public class CommandZSMod extends CommandBase {

  @Override
  public String getCommandName() {
    return "zsmod";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/zsmod biomesync | /zsmod saiyanmerge [player]";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 2; // ops
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    if (args == null || args.length == 0) {
      throw new WrongUsageException(getCommandUsage(sender));
    }

    String sub = args[0];

    if ("biomesync".equalsIgnoreCase(sub)) {
      // 1) Reload server config from disk
      BiomeConfig.reload();

      // 2) Push snapshot to all clients
      IMessage pkt = BiomeVisualSyncUtil.buildFullPacket();
      ZeroSMod.network.sendToAll(pkt);

      int players = MinecraftServer.getServer().getConfigurationManager().playerEntityList.size();
      sender.addChatMessage(new ChatComponentText("[ZeroSMod] Biome visuals reloaded and synced to " + players + " player(s)."));
      return;
    }

    if ("saiyanmerge".equalsIgnoreCase(sub)) {
      EntityPlayerMP player = args.length > 1
          ? getPlayer(sender, args[1])
          : getCommandSenderAsPlayer(sender);
      String result = SaiyanMasteryMergeEvent.forceMerge(player);
      sender.addChatMessage(new ChatComponentText("[ZeroSMod] Saiyan merge for " + player.getCommandSenderName() + ": " + result));
      return;
    }

    throw new WrongUsageException(getCommandUsage(sender));
  }
}
