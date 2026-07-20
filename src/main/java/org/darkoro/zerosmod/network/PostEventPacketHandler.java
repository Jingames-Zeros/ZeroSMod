package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

/**
 * Server-side handler for packets that only post Forge Event. Subclasses supply Event.
 */
public abstract class PostEventPacketHandler<T extends IMessage> implements IMessageHandler<T, IMessage> {

  @Override public final IMessage onMessage(T message, MessageContext ctx) {
    ServerTaskScheduler.schedule(() -> {
      EntityPlayerMP player = ctx.getServerHandler().playerEntity;
      MinecraftForge.EVENT_BUS.post(createEvent(player, message));
    });
    return null;
  }

  protected abstract Event createEvent(EntityPlayerMP player, T message);

}
