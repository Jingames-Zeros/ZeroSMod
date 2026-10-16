package org.darkoro.zerosmod.mixin.mixins.late.impl.dbc.client;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHJFC;
import JinRyuu.JRMCore.items.ItemBarberSnC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import org.darkoro.zerosmod.dbchair.gui.HairEditorScreen;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)
public abstract class JRMCoreHairEditorMixin {
  @Redirect(method = "head(IIIFFLnet/minecraft/entity/EntityLivingBase;)V",
      at = @At(value = "INVOKE",
          target = "LJinRyuu/JRMCore/JRMCoreGuiScreen;renderEntityWithPosYaw(Lnet/minecraft/entity/Entity;DDDFF)V",
          remap = false), remap = false)
  private static void zerosmod$rotateHairPreviewAtModelOrigin(Entity entity, double x, double y, double z,
      float renderYaw, float partialTicks) {
    if (!HairEditorScreen.zerosmod$isPreviewDrawActive()) {
      JRMCoreGuiScreen.renderEntityWithPosYaw(entity, x, y, z, renderYaw, partialTicks);
      return;
    }
    GL11.glPushMatrix();
    try {
      GL11.glRotatef(HairEditorScreen.zerosmod$getPreviewYaw(), 0, 1, 0);
      JRMCoreGuiScreen.renderEntityWithPosYaw(entity, x, y, z, renderYaw, partialTicks);
    } finally {
      GL11.glPopMatrix();
    }
  }

  @Inject(method = "actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at = @At("HEAD"),
      cancellable = true, remap = true)
  private void zerosmod$openHairEditor(GuiButton button, CallbackInfo ci) {
    if (button.id < 5100 || button.id > 5104) return;
    JRMCoreGuiScreen origin = (JRMCoreGuiScreen) (Object) this;
    Entity target = ItemBarberSnC.barberTarget;
    String code = JRMCoreH.JFC() && target != null && JRMCoreHJFC.isChildNPC(target)
        ? JRMCoreHJFC.childDNSH(target) : JRMCoreH.dnsH;
    if (code == null || code.length() < 3) code = JRMCoreHairEditorAccess.zerosmod$getHairCode();
    if (code == null || code.length() < 3) return;
    JRMCoreHairEditorAccess.zerosmod$setHairCode(code);
    Minecraft.getMinecraft().displayGuiScreen(new HairEditorScreen(origin, button.id - 5100, code));
    ci.cancel();
  }
}
