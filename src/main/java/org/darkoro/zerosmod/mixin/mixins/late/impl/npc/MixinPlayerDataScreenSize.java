package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import noppes.npcs.controllers.data.PlayerData;
import org.darkoro.zerosmod.screen.PlayerScreenData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stores client-reported scaled display values with CNPC+'s per-player data. */
@Mixin(value = PlayerData.class, remap = false)
public abstract class MixinPlayerDataScreenSize implements PlayerScreenData {

  @Unique private int zerosmod$scaledScreenWidth = -1;
  @Unique private int zerosmod$scaledScreenHeight = -1;
  @Unique private int zerosmod$guiScale = -1;

  @Inject(method = "onLogin()V", at = @At("HEAD"), remap = false)
  private void zerosmod$resetScreenData(CallbackInfo ci) {
    zerosmod$setScreenData(-1, -1, -1);
  }

  @Override public int zerosmod$getScaledScreenWidth() {
    return zerosmod$scaledScreenWidth;
  }

  @Override public int zerosmod$getScaledScreenHeight() {
    return zerosmod$scaledScreenHeight;
  }

  @Override public int zerosmod$getGuiScale() {
    return zerosmod$guiScale;
  }

  @Override public void zerosmod$setScreenData(int width, int height, int guiScale) {
    zerosmod$scaledScreenWidth = width;
    zerosmod$scaledScreenHeight = height;
    zerosmod$guiScale = guiScale;
  }
}
