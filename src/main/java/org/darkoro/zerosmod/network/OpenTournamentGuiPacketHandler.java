package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.darkoro.zskeybinds.event.TournamentMenuKeyEvent;

public class OpenTournamentGuiPacketHandler implements IMessageHandler<OpenTournamentGuiPacket, IMessage> {

  @Override
  public IMessage onMessage(OpenTournamentGuiPacket message, MessageContext ctx) {
    ServerTaskScheduler.schedule(() -> {
      EntityPlayerMP player = ctx.getServerHandler().playerEntity;
      MinecraftForge.EVENT_BUS.post(new TournamentMenuKeyEvent(player));
    });
    return null;
  }

}
