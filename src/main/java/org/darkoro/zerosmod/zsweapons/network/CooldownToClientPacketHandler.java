package org.darkoro.zerosmod.zsweapons.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import org.darkoro.zerosmod.zsweapons.client.ClientWeaponHandler;

public class CooldownToClientPacketHandler implements IMessageHandler<CooldownToClientPacket, IMessage> {
    @Override public IMessage onMessage(CooldownToClientPacket message, MessageContext ctx) {
        ClientWeaponHandler.INSTANCE.handleCooldownPacket(message);
        return null;
    }
}