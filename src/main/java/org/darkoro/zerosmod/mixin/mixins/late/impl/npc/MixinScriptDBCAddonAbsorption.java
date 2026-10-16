package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.ScriptDBCAddon;
import net.minecraft.nbt.NBTTagCompound;
import org.darkoro.zerosmod.dbc.MajinAbsorptionDataUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Repairs DBCAddon's CNPC-facing Majin absorption setters without changing their API. */
@Mixin(value = ScriptDBCAddon.class, remap = false)
public abstract class MixinScriptDBCAddonAbsorption {
  @Shadow public NBTTagCompound nbt;
  @Shadow public DBCData dbcData;

  @Inject(method = "setMajinAbsorptionRace(I)V", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$setAbsorptionRace(int race, CallbackInfo ci) {
    updateAbsorptionField(1, race);
    ci.cancel();
  }

  @Inject(method = "setMajinAbsorptionPower(I)V", at = @At("HEAD"), cancellable = true, remap = false)
  private void zerosmod$setAbsorptionPoints(int points, CallbackInfo ci) {
    updateAbsorptionField(0, points);
    ci.cancel();
  }

  private void updateAbsorptionField(int field, int value) {
    if (nbt == null || nbt.getByte("jrmcRace") != 5) {
      return;
    }

    String updated = MajinAbsorptionDataUtil.withIntegerField(
        nbt.getString("jrmcMajinAbsorptionData"), field, value);
    if (updated == null) {
      return;
    }

    nbt.setString("jrmcMajinAbsorptionData", updated);
    if (dbcData != null) {
      dbcData.MajinAbsorptionData = updated;
    }
  }
}
