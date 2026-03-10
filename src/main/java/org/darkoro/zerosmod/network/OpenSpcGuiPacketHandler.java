package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import org.darkoro.zerosmod.event.SpiritControlMenuKeyEvent;

public class OpenSpcGuiPacketHandler implements IMessageHandler<OpenSpcGuiPacket, IMessage> {

  @Override public IMessage onMessage(OpenSpcGuiPacket message, MessageContext ctx) {
    ServerTaskScheduler.schedule(() -> {
      EntityPlayerMP player = ctx.getServerHandler().playerEntity;
      MinecraftForge.EVENT_BUS.post(new SpiritControlMenuKeyEvent(player));
    });
    return null;
  }

}
