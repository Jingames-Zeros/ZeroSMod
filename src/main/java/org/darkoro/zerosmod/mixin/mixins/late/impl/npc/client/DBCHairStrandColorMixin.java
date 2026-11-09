package org.darkoro.zerosmod.mixin.mixins.late.impl.npc.client;

import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.client.model.part.hair.ModelHairRenderer;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import org.darkoro.zerosmod.dbchair.gui.HairStrandColors;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Applies ZeroSMod's appended strand colors while DBC Addon renders CNPC hair. */
@Mixin(value = DBCHair.class, remap = false)
public abstract class DBCHairStrandColorMixin {
  // Resolved once per renderHairs call; null until the first colored strand needs it.
  @Unique private static Boolean zerosmod$formCheck;
  // Set once this DBC Addon build lacks form colors, so the failure isn't repeated every frame.
  @Unique private static boolean zerosmod$formColorsMissing;

  @Inject(method = "renderHairs(Lkamkeel/npcdbc/data/npc/DBCDisplay;)V", at = @At("HEAD"),
      remap = false)
  private void zerosmod$resetFormCheck(DBCDisplay display, CallbackInfo ci) {
    zerosmod$formCheck = null;
  }

  @Redirect(method = "renderHairs(Lkamkeel/npcdbc/data/npc/DBCDisplay;)V",
      at = @At(value = "INVOKE",
          target = "Lkamkeel/npcdbc/client/model/part/hair/ModelHairRenderer;render(F)V",
          remap = true),
      remap = false)
  private void zerosmod$renderNpcHairStrandColor(ModelHairRenderer renderer, float scale,
      @Local(index = 32) int strandIndex,
      @Local(index = 12) String renderCode,
      @Local(argsOnly = true) DBCDisplay display) {
    int rgb = HairStrandColors.strandColor(renderCode, strandIndex);
    if (rgb == HairStrandColors.NO_COLOR || display == null) {
      renderer.render(scale);
      return;
    }
    if (zerosmod$formCheck == null) {
      zerosmod$formCheck = zerosmod$formColorsHair(display);
    }
    if (zerosmod$formCheck) {
      renderer.render(scale);
      return;
    }
    GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    try {
      HairStrandColors.applyColor(rgb);
      renderer.render(scale);
    } finally {
      GL11.glPopAttrib();
    }
  }

  @Unique private static boolean zerosmod$formColorsHair(DBCDisplay display) {
    if (zerosmod$formColorsMissing) return false;
    try {
      Form form = display.getForm();
      if (form == null || form.display == null || display.formColor == null) return false;
      return display.formColor.hasAnyColor(form.display, "hair")
          || JRMCoreH.isRaceMajin(display.race)
              && display.formColor.hasAnyColor(form.display, "bodycm");
    } catch (LinkageError missing) {
      zerosmod$formColorsMissing = true;
      return false;
    } catch (RuntimeException lookupFailed) {
      return false;
    }
  }
}
