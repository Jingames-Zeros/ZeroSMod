package org.darkoro.zerosmod.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

public class SpiritControlMenuKeyEvent extends Event {

  private final EntityPlayer player;

  public SpiritControlMenuKeyEvent(EntityPlayer player) {
    this.player = player;
  }

  public EntityPlayer getPlayer() {
    return this.player;
  }

}
