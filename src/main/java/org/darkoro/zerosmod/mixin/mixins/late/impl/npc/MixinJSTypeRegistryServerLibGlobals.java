package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import noppes.npcs.client.gui.util.script.interpreter.js_parser.JSTypeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Restores editor-only types for globals created by the server's Forge scripts. */
@Mixin(value = JSTypeRegistry.class, remap = false)
public abstract class MixinJSTypeRegistryServerLibGlobals {

  @Shadow public abstract void registerGlobalObject(String name, String typeName);
  @Shadow public abstract void registerTypeAlias(String alias, String typeName);

  @Inject(method = "registerEngineGlobalObjects", at = @At("RETURN"), remap = false)
  private void zerosmod$registerServerLibGlobals(CallbackInfo ci) {
    registerGlobalObject("lib", "ZSServerLib");
    registerGlobalObject("libPaths", "ZSServerLibPaths");
    registerGlobalObject("libEraserIds", "ZSServerLibEraserIds");

    registerTypeAlias("lib.animationHandler", "ZSLibAnimationHandler");
    registerTypeAlias("lib.dbcDisplayHandler", "ZSLibDBCDisplayHandler");
    registerTypeAlias("lib.progressBar", "ZSLibProgressBar");
    registerTypeAlias("lib.deck", "ZSLibDeck");
    registerTypeAlias("lib.raidBossHandler", "ZSLibRaidBossHandler");
  }
}
