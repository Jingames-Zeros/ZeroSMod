package org.darkoro.zerosmod.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

public class ChargeKeyEvent extends Event {

  private final EntityPlayer player;
  private final boolean holding;

  public ChargeKeyEvent(EntityPlayer player, boolean pressed) {
    this.player = player;
    this.holding = pressed;
  }

  public EntityPlayer getPlayer() {
    return this.player;
  }

  public boolean isHolding() {
    return this.holding;
  }

}
