package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.scripted.entity.ScriptPlayer;
import org.darkoro.zerosmod.api.ScriptPlayerTransformationMethods;
import org.darkoro.zerosmod.transformation.CustomFormTransformationEventHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ScriptPlayer.class, remap = false)
public abstract class MixinScriptPlayerTransformation<T extends EntityPlayerMP>
    implements ScriptPlayerTransformationMethods {

  @Shadow public T player;

  @Override public boolean isTransforming() {
    return CustomFormTransformationEventHooks.isTransforming(this.player);
  }
}
