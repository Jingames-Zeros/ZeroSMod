package org.darkoro.zerosmod.mixin.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.client.model.part.hair.ModelHairRenderer;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import org.darkoro.zerosmod.dbchair.gui.HairStrandColors;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Applies ZeroSMod's appended strand colors while DBC Addon renders CNPC hair. */
@Mixin(value = DBCHair.class, remap = false)
public abstract class DBCHairStrandColorMixin {
  @Redirect(method = "renderHairs(Lkamkeel/npcdbc/data/npc/DBCDisplay;)V",
      at = @At(value = "INVOKE",
          target = "Lkamkeel/npcdbc/client/model/part/hair/ModelHairRenderer;render(F)V",
          remap = true),
      remap = false)
  private void zerosmod$renderNpcHairStrandColor(ModelHairRenderer renderer, float scale,
      @Local(index = 32) int strandIndex,
      @Local(index = 12) String renderCode,
      @Local(argsOnly = true) DBCDisplay display) {
    GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    try {
      HairStrandColors.applyNpcRenderColor(renderCode, strandIndex, display);
      renderer.render(scale);
    } finally {
      GL11.glPopAttrib();
    }
  }
}
