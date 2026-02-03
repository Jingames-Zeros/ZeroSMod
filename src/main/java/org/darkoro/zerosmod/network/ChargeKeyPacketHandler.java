package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.darkoro.zerosmod.event.ChargeKeyEvent;

public class ChargeKeyPacketHandler implements IMessageHandler<ChargeKeyPacket, IMessage> {

  @Override public IMessage onMessage(ChargeKeyPacket message, MessageContext ctx) {
    ServerTaskScheduler.schedule(() -> {
      EntityPlayerMP player = ctx.getServerHandler().playerEntity;
      MinecraftForge.EVENT_BUS.post(new ChargeKeyEvent(player, message.isHolding()));
    });
    return null;
  }

}
