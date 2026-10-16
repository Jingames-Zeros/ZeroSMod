package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.JBRA.ModelRendererJBRA;
import JinRyuu.JBRA.ModelBipedDBC;
import com.llamalad7.mixinextras.sugar.Local;
import org.darkoro.zerosmod.dbchair.gui.HairEditorScreen;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModelBipedDBC.class, remap = false)
public abstract class ModelBipedDBCHairHighlightMixin {
  @Redirect(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
      at = @At(value = "INVOKE", target = "LJinRyuu/JBRA/ModelRendererJBRA;render(F)V", remap = false),
      remap = false)
  private void zerosmod$highlightSelectedHairSegment(ModelRendererJBRA renderer, float scale,
      @Local(index = 31) int segmentIndex) {
    HairEditorScreen.zerosmod$recordPreviewHairHit(segmentIndex, renderer, scale);
    if (HairEditorScreen.zerosmod$isPreviewDrawActive()
        && segmentIndex == HairEditorScreen.zerosmod$getPreviewSelectedSegment()) {
      GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
      GL11.glColor4f(0.48F, 1F, 0.34F, 1F);
      try {
        renderer.render(scale);
      } finally {
        GL11.glPopAttrib();
      }
    } else {
      renderer.render(scale);
    }
  }
}
