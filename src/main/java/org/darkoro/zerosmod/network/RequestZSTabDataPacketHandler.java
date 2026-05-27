package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.tab.ZSTabDataProvider;

public class RequestZSTabDataPacketHandler implements IMessageHandler<RequestZSTabDataPacket, IMessage> {

  @Override
  public IMessage onMessage(RequestZSTabDataPacket message, final MessageContext ctx) {
    ServerTaskScheduler.schedule(new Runnable() {
      @Override
      public void run() {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        ZeroSMod.network.sendTo(ZSTabDataProvider.buildPacket(player), player);
      }
    });
    return null;
  }
}
