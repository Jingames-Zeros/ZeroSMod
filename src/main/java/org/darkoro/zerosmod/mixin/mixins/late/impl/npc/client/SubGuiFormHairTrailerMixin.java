package org.darkoro.zerosmod.mixin.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormDisplay;
import kamkeel.npcdbc.data.form.FormDisplay;
import net.minecraft.client.gui.GuiButton;
import org.darkoro.zerosmod.dbchair.gui.HairStrandColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets the DBC Addon form customizer paste trailer-bearing hair codes unchanged. */
@Mixin(value = SubGuiFormDisplay.class, remap = false)
public abstract class SubGuiFormHairTrailerMixin {
  @Shadow private FormDisplay display;
  @Shadow public abstract String getClipboardContents();
  @Shadow private void updateButtons() { }

  @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V",
      at = @At("HEAD"), cancellable = true, remap = true)
  private void zerosmod$pasteExtendedFormHairCode(GuiButton button, CallbackInfo ci) {
    if (button == null || button.id != 101) return;
    String code = getClipboardContents();
    if (!HairStrandColors.hasOverrides(code)) return;
    display.hairCode = code;
    updateButtons();
    ci.cancel();
  }
}
