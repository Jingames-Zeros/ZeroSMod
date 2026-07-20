package org.darkoro.zerosmod.network;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import org.darkoro.zerosmod.event.TournamentMenuKeyEvent;

public class OpenTournamentGuiPacketHandler extends PostEventPacketHandler<OpenTournamentGuiPacket> {

  @Override protected Event createEvent(EntityPlayerMP player, OpenTournamentGuiPacket message) {
    return new TournamentMenuKeyEvent(player);
  }

}
