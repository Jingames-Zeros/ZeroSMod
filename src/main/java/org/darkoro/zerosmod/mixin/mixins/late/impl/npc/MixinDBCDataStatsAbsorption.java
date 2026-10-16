package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataStats;
import net.minecraft.nbt.NBTTagCompound;
import org.darkoro.zerosmod.dbc.MajinAbsorptionDataUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Preserves absorbed-race metadata when DBCAddon sets or restores absorption points. */
@Mixin(value = DBCDataStats.class, remap = false)
public abstract class MixinDBCDataStatsAbsorption {
  @Shadow @Final private DBCData data;

  @Inject(method = "setAbsorption(I)V", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$setAbsorptionPoints(int points, CallbackInfo ci) {
    if (data == null || data.Race != 5) {
      ci.cancel();
      return;
    }

    NBTTagCompound nbt = data.getRawCompound();
    String updated = MajinAbsorptionDataUtil.withIntegerField(
        nbt.getString("jrmcMajinAbsorptionData"), 0, points);
    if (updated != null) {
      nbt.setString("jrmcMajinAbsorptionData", updated);
      data.MajinAbsorptionData = updated;
    }
    ci.cancel();
  }
}
