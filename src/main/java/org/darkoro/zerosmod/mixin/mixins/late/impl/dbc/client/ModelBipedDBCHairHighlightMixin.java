package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.JBRA.ModelRendererJBRA;
import JinRyuu.JBRA.ModelBipedDBC;
import com.llamalad7.mixinextras.sugar.Local;
import org.darkoro.zerosmod.dbchair.gui.HairEditorScreen;
import org.darkoro.zerosmod.dbchair.gui.HairStrandColors;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ModelBipedDBC.class, remap = false)
public abstract class ModelBipedDBCHairHighlightMixin {
  @Redirect(method = "renderHairsV2(FLjava/lang/String;FIIIILJinRyuu/JBRA/RenderPlayerJBRA;Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
      at = @At(value = "INVOKE", target = "LJinRyuu/JBRA/ModelRendererJBRA;render(F)V", remap = false),
      remap = false)
  private void zerosmod$highlightSelectedHairSegment(ModelRendererJBRA renderer, float scale,
      @Local(index = 31) int segmentIndex,
      @Local(argsOnly = true) String hairCode,
      @Local(argsOnly = true) AbstractClientPlayer player) {
    HairEditorScreen.zerosmod$recordPreviewHairHit(segmentIndex, renderer, scale);
    int rgb = HairStrandColors.playerStrandColor(hairCode, segmentIndex, player);
    if (rgb == HairStrandColors.NO_COLOR) {
      renderer.render(scale);
    } else {
      GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
      try {
        HairStrandColors.applyColor(rgb);
        renderer.render(scale);
      } finally {
        GL11.glPopAttrib();
      }
    }
    if (HairEditorScreen.zerosmod$isPreviewDrawActive()
        && segmentIndex == HairEditorScreen.zerosmod$getPreviewSelectedSegment()) {
      zerosmod$outline(renderer, scale);
    }
  }

  /** Draws the selected strand's edges in white so its own color stays visible. */
  @Unique private static void zerosmod$outline(ModelRendererJBRA renderer, float scale) {
    GL11.glPushAttrib(GL11.GL_CURRENT_BIT | GL11.GL_ENABLE_BIT | GL11.GL_LINE_BIT
        | GL11.GL_POLYGON_BIT);
    try {
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_CULL_FACE);
      GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
      GL11.glPolygonOffset(-1F, -1F);
      GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
      GL11.glLineWidth(2.5F);
      GL11.glColor4f(1F, 1F, 1F, 1F);
      renderer.render(scale);
    } finally {
      GL11.glPopAttrib();
    }
  }
}
