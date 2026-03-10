package org.darkoro.zerosmod.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

public class TournamentMenuKeyEvent extends Event {

  private final EntityPlayer player;

  public TournamentMenuKeyEvent(EntityPlayer player) {
    this.player = player;
  }

  public EntityPlayer getPlayer() {
    return this.player;
  }

}
