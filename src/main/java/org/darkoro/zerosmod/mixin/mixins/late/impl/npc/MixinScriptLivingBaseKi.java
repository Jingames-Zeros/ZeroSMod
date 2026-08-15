package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.scripted.entity.ScriptEntity;
import noppes.npcs.scripted.entity.ScriptLivingBase;
import org.darkoro.zerosmod.ki.KiScriptHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ScriptLivingBase.class, remap = false)
public abstract class MixinScriptLivingBaseKi<T extends EntityLivingBase> extends ScriptEntity<T> {

  public MixinScriptLivingBaseKi(T entity) {
    super(entity);
  }

  public int stopKi(int range) {
    return KiScriptHelper.stopKiAround(this.entity, range);
  }

  public int stopKi(int range, int ignoredTicks) {
    return KiScriptHelper.stopKiAround(this.entity, range);
  }

  public int releaseKi(int range) {
    return KiScriptHelper.releaseKiAround(this.entity, range);
  }

  public IEntity[] getNearbyKi(int range) {
    return KiScriptHelper.getNearbyKi(this.entity, range);
  }
}
