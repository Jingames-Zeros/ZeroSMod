package org.darkoro.zerosmod.mixin.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.gui.GuiModelDBC;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.darkoro.zerosmod.dbchair.gui.HairStrandColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets DBC Addon's CNPC hair paste button accept ZeroSMod's extended hair strings. */
@Mixin(value = GuiModelDBC.class, remap = false)
public abstract class GuiModelDBCHairTrailerMixin {
  @Shadow private DBCDisplay display;
  @Shadow public abstract String getClipboardContents();

  @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V",
      at = @At("HEAD"), cancellable = true, remap = true)
  private void zerosmod$pasteExtendedHairCode(GuiButton button, CallbackInfo ci) {
    if (button == null || button.id != 101) return;
    String code = getClipboardContents();
    if (!HairStrandColors.hasOverrides(code)) return;
    display.hairCode = code;
    ((GuiScreen) (Object) this).initGui();
    ci.cancel();
  }
}
