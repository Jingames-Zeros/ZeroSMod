package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import net.minecraft.client.gui.GuiButton;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets Majins edit the hair-color field in DBC's barber screen independently of body color. */
@Mixin(value = JRMCoreGuiScreen.class, remap = false)
public abstract class JRMCoreMajinHairColorMixin {
  @Inject(method = "initGui()V", at = @At("TAIL"), remap = true)
  private void zerosmod$enableMajinHairColorOnOpen(CallbackInfo ci) {
    JRMCoreGuiScreen screen = (JRMCoreGuiScreen) (Object) this;
    if (screen.guiID == 8 && JRMCoreH.isRaceMajin(JRMCoreGuiScreen.RaceSlcted)) {
      JRMCoreGuiScreen.CanColor = true;
    }
  }

  @Inject(method = "drawScreen(IIF)V", at = @At("HEAD"), remap = true)
  private void zerosmod$enableMajinHairColorBeforeButtons(int mouseX, int mouseY,
      float partialTicks, CallbackInfo ci) {
    JRMCoreGuiScreen screen = (JRMCoreGuiScreen) (Object) this;
    if (screen.guiID == 8 && JRMCoreH.isRaceMajin(JRMCoreGuiScreen.RaceSlcted)) {
      JRMCoreGuiScreen.CanColor = true;
    }
  }

  @Redirect(method = "drawScreen(IIF)V",
      at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreGuiScreen;CanColor:Z",
          opcode = Opcodes.PUTSTATIC, remap = false), remap = false)
  private void zerosmod$showMajinHairColorOption(boolean canColor) {
    JRMCoreGuiScreen screen = (JRMCoreGuiScreen) (Object) this;
    if ((screen.guiID == 8 || screen.guiIDprev == 8)
        && JRMCoreH.isRaceMajin(JRMCoreGuiScreen.RaceSlcted)) {
      JRMCoreGuiScreen.CanColor = true;
    } else {
      JRMCoreGuiScreen.CanColor = canColor;
    }
  }

  @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("HEAD"),
      cancellable = true, remap = true)
  private void zerosmod$cycleAllMajinHairChoices(GuiButton button, CallbackInfo ci) {
    JRMCoreGuiScreen screen = (JRMCoreGuiScreen) (Object) this;
    if (screen.guiID != 8 || !JRMCoreH.isRaceMajin(JRMCoreGuiScreen.RaceSlcted)
        || (button.id != 15 && button.id != -15)) return;
    int hair = JRMCoreGuiScreen.HairSlcted;
    if (button.id == 15) {
      JRMCoreGuiScreen.HairSlcted = hair == 10 ? 11 : hair == 11 ? 12 : 10;
    } else {
      JRMCoreGuiScreen.HairSlcted = hair == 10 ? 12 : hair == 12 ? 11 : 10;
    }
    JRMCoreGuiScreen.setdnsForHair();
    ci.cancel();
  }

  @Inject(method = "updateMajinHairToBodyColor()V", at = @At("HEAD"), cancellable = true,
      remap = false)
  private void zerosmod$keepMajinHairColorIndependent(CallbackInfo ci) {
    JRMCoreGuiScreen screen = (JRMCoreGuiScreen) (Object) this;
    if (JRMCoreH.isRaceMajin(JRMCoreGuiScreen.RaceSlcted)
        && (screen.guiID == 8 || screen.guiIDprev == 8)) {
      ci.cancel();
    }
  }
}
