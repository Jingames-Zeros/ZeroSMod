package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.scripted.entity.ScriptPlayer;
import org.darkoro.zerosmod.api.ScriptPlayerMethods;
import org.darkoro.zerosmod.screen.PlayerScreenData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Adds client-reported display information to CNPC player wrappers. */
@Mixin(value = ScriptPlayer.class, remap = false)
public abstract class MixinScriptPlayerScreenSize<T extends EntityPlayerMP>
    implements ScriptPlayerMethods {

  @Shadow public T player;

  @Override public int getScaledScreenWidth() {
    return zerosmod$getScreenData().zerosmod$getScaledScreenWidth();
  }

  @Override public int getScaledScreenHeight() {
    return zerosmod$getScreenData().zerosmod$getScaledScreenHeight();
  }

  @Override public int getGuiScale() {
    return zerosmod$getScreenData().zerosmod$getGuiScale();
  }

  private PlayerScreenData zerosmod$getScreenData() {
    return (PlayerScreenData)PlayerData.get(player);
  }
}
