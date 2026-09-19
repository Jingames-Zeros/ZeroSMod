package org.darkoro.zerosmod.zsweapons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.darkoro.zerosmod.zsweapons.server.ServerWeaponHandler;

public class TargetEntityToServerPacketHandler implements IMessageHandler<TargetEntityToServerPacket, IMessage> {
    @Override public IMessage onMessage(TargetEntityToServerPacket message, MessageContext ctx) {
        ServerWeaponHandler.INSTANCE.handleTargetEntityPacket(message, ctx);
        return null;
    }
}