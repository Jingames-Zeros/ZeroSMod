package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import java.util.ArrayList;
import java.util.List;
import kamkeel.npcs.entity.EntityEnergyDome;
import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.entity.ScriptEnergyDome;
import org.darkoro.zerosmod.api.ScriptEnergyDomeMethods;
import org.darkoro.zerosmod.dome.EnergyDomeExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ScriptEnergyDome.class, remap = false)
public abstract class MixinScriptEnergyDomeMethods implements ScriptEnergyDomeMethods {

  @Override public float getDamage() {
    return zerosmod$extension().zerosmod$getDamage();
  }

  @Override public void setDamage(float damage) {
    zerosmod$extension().zerosmod$setDamage(damage);
  }

  @Override public int getDamageInterval() {
    return zerosmod$extension().zerosmod$getDamageInterval();
  }

  @Override public void setDamageInterval(int ticks) {
    zerosmod$extension().zerosmod$setDamageInterval(ticks);
  }

  @Override public boolean affectOwner() {
    return zerosmod$extension().zerosmod$affectOwner();
  }

  @Override public void setAffectOwner(boolean affects) {
    zerosmod$extension().zerosmod$setAffectOwner(affects);
  }

  @Override public IEntity getFollower() {
    Entity follower = zerosmod$extension().zerosmod$getFollower();
    return follower == null ? null : NpcAPI.Instance().getIEntity(follower);
  }

  @Override public void setFollower(IEntity entity) {
    zerosmod$extension().zerosmod$setFollower(entity == null ? null : entity.getMCEntity());
  }

  @Override public IEntity[] getEntitiesInside() {
    Entity[] entities = zerosmod$extension().zerosmod$getEntitiesInside();
    List<IEntity> wrapped = new ArrayList<IEntity>(entities.length);
    for (Entity entity : entities) {
      IEntity scriptEntity = NpcAPI.Instance().getIEntity(entity);
      if (scriptEntity != null) {
        wrapped.add(scriptEntity);
      }
    }
    return wrapped.toArray(new IEntity[wrapped.size()]);
  }

  @Override public boolean isInside(IEntity entity) {
    return entity != null && zerosmod$extension().zerosmod$isInside(entity.getMCEntity());
  }

  @Override public void setTargetRadius(float radius, int ticks) {
    zerosmod$extension().zerosmod$setTargetRadius(radius, ticks);
  }

  @Unique private EnergyDomeExtension zerosmod$extension() {
    return (EnergyDomeExtension)zerosmod$dome();
  }

  @Unique private EntityEnergyDome zerosmod$dome() {
    return (EntityEnergyDome)((ScriptEnergyDome)(Object)this).getMCEntity();
  }
}
