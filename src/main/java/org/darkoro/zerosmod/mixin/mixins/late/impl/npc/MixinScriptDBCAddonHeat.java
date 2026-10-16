package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.form.IFormMastery;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.ScriptDBCAddon;
import org.darkoro.zerosmod.api.ScriptDBCAddonHeatMethods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ScriptDBCAddon.class, remap = false)
public abstract class MixinScriptDBCAddonHeat implements ScriptDBCAddonHeatMethods {

  @Shadow public DBCData dbcData;

  @Override public float getCurrentHeat() {
    return dbcData == null ? 0.0F : Math.max(0.0F, dbcData.addonCurrentHeat);
  }

  @Override public int getMaxHeat() {
    if (dbcData == null) {
      return 0;
    }

    IForm form = dbcData.getForm();
    if (form == null) {
      return 0;
    }

    IFormMastery mastery = form.getMastery();
    return mastery != null && mastery.hasHeat() ? mastery.getMaxHeat() : 0;
  }

  @Override public void setCurrentHeat(float amount) {
    int maxHeat = getMaxHeat();
    if (dbcData == null || maxHeat <= 0 || !isFinite(amount)) {
      return;
    }

    dbcData.setAddonHeat(Math.max(0.0F, Math.min(amount, maxHeat)));
  }

  @Override public void setHeatPercentage(float percent) {
    int maxHeat = getMaxHeat();
    if (dbcData == null || maxHeat <= 0 || !isFinite(percent)) {
      return;
    }

    float clampedPercent = Math.max(0.0F, Math.min(percent, 100.0F));
    dbcData.setAddonHeat(maxHeat * clampedPercent / 100.0F);
  }

  @Override public void clearHeat() {
    if (dbcData != null) {
      dbcData.setAddonHeat(0.0F);
    }
  }

  private static boolean isFinite(float value) {
    return !Float.isNaN(value) && !Float.isInfinite(value);
  }
}
