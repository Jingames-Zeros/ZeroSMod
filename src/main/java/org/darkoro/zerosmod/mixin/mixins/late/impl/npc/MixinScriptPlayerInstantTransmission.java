package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.scripted.entity.ScriptPlayer;
import org.darkoro.zerosmod.it.InstantTransmissionScriptHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ScriptPlayer.class, remap = false)
public abstract class MixinScriptPlayerInstantTransmission<T extends EntityPlayerMP> {

  @Shadow public T player;

  public String getInstantTransmissionLookTargetName() {
    return InstantTransmissionScriptHelper.getInstantTransmissionLookTargetName(this.player);
  }
}
