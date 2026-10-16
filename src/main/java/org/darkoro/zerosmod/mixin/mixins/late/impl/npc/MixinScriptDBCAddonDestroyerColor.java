package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.api.IKiAttack;
import kamkeel.npcdbc.scripted.ScriptDBCAddon;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.darkoro.zerosmod.ki.DestroyerKiColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ScriptDBCAddon.class, remap = false)
public abstract class MixinScriptDBCAddonDestroyerColor {

  @Redirect(
      method = "fireKiAttack(Lkamkeel/npcdbc/api/IKiAttack;)V",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z",
          remap = true),
      remap = false)
  private boolean zerosmod$copyDestroyerColor(World world, Entity spawnedEntity,
      @Local(argsOnly = true) IKiAttack configuredAttack) {
    DestroyerKiColor.applyToSpawnedAttack(configuredAttack, spawnedEntity);
    return world.spawnEntityInWorld(spawnedEntity);
  }
}
