package org.darkoro.zerosmod.mixin.late.impl.npc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.scripted.entity.ScriptEntity;
import org.darkoro.zerosmod.it.InstantTransmissionScriptHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ScriptEntity.class, remap = false)
public abstract class MixinScriptEntityInstantTransmission<T extends Entity> {

  @Shadow protected T entity;

  public String getInstantTransmissionLookTargetName() {
    if (!(this.entity instanceof EntityPlayerMP)) {
      return "";
    }

    return InstantTransmissionScriptHelper.getInstantTransmissionLookTargetName((EntityPlayerMP)this.entity);
  }
}
