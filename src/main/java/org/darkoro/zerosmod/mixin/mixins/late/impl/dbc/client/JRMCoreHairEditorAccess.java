package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)
public interface JRMCoreHairEditorAccess {
  @Accessor("dnsH")
  static String zerosmod$getHairCode() {
    throw new AssertionError();
  }

  @Accessor("dnsH")
  static void zerosmod$setHairCode(String code) {
    throw new AssertionError();
  }

  @Accessor("dnsHSent")
  static void zerosmod$setLastSentHairCode(String code) {
    throw new AssertionError();
  }
}
