package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import net.minecraft.entity.Entity;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.scripted.entity.ScriptEntity;
import org.darkoro.zerosmod.ki.KiScriptHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ScriptEntity.class, remap = false)
public abstract class MixinScriptEntityKi<T extends Entity> {

  @Shadow protected T entity;

  public boolean isKiAttack() {
    return KiScriptHelper.isKiAttack(this.entity);
  }

  public boolean isPlayerKi() {
    return KiScriptHelper.isPlayerKi(this.entity);
  }

  public boolean isNpcKi() {
    return KiScriptHelper.isNpcKi(this.entity);
  }

  public boolean isDestroyerAttack() {
    return KiScriptHelper.isDestroyerAttack(this.entity);
  }

  public int getKiId() {
    return KiScriptHelper.getKiId(this.entity);
  }

  public boolean isKiStopped() {
    return KiScriptHelper.isKiStopped(this.entity);
  }

  public String getPlayerName() {
    return KiScriptHelper.getPlayerName(this.entity);
  }

  public String getKiOwnerName() {
    return KiScriptHelper.getKiOwnerName(this.entity);
  }

  public IEntity getKiOwner() {
    return KiScriptHelper.getKiOwner(this.entity);
  }

  public String getKiType() {
    return KiScriptHelper.getKiType(this.entity);
  }

  public int getKiTypeId() {
    return KiScriptHelper.getKiTypeId(this.entity);
  }

  public double getKiDamage() {
    return KiScriptHelper.getKiDamage(this.entity);
  }

  public boolean setKiDamage(double damage) {
    return KiScriptHelper.setKiDamage(this.entity, damage);
  }

  public boolean setKiColor(String color) {
    return KiScriptHelper.setKiColor(this.entity, color);
  }

  public boolean setKiColor(int color) {
    return KiScriptHelper.setKiColor(this.entity, color);
  }

  public boolean setKiColor(int color, int color2) {
    return KiScriptHelper.setKiColor(this.entity, color, color2);
  }

  public boolean stopKi() {
    return KiScriptHelper.stopKi(this.entity);
  }

  public boolean stopKi(int ignoredTicks) {
    return KiScriptHelper.stopKi(this.entity);
  }

  public boolean releaseKi() {
    return KiScriptHelper.releaseKi(this.entity);
  }

  public boolean resetKiLifetime() {
    return KiScriptHelper.resetKiLifetime(this.entity);
  }

  public boolean killKi() {
    return KiScriptHelper.killKi(this.entity);
  }

  public boolean stealKi(String color, IEntity owner) {
    return KiScriptHelper.stealKi(this.entity, color, owner);
  }

  public boolean stealKi(int color, IEntity owner) {
    return KiScriptHelper.stealKi(this.entity, color, owner);
  }

  public boolean redirectKi(IEntity target) {
    return KiScriptHelper.redirectKi(this.entity, target);
  }

  public boolean directKi(double x, double y, double z) {
    return KiScriptHelper.directKi(this.entity, x, y, z);
  }
}
