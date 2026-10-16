package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import kamkeel.npcdbc.data.KiAttack;
import org.darkoro.zerosmod.api.ScriptKiAttackBuilderMethods;
import org.darkoro.zerosmod.ki.DestroyerKiColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = KiAttack.class, remap = false)
public abstract class MixinKiAttackDestroyerColor
    implements ScriptKiAttackBuilderMethods, DestroyerKiColor.Holder {

  @Unique private int zerosmod$destroyerColor = DestroyerKiColor.DEFAULT;

  @Override public boolean setDestroyerColor(int rgb) {
    return DestroyerKiColor.set(this, rgb);
  }

  @Override public int getDestroyerColor() {
    return this.zerosmod$destroyerColor;
  }

  @Override public void clearDestroyerColor() {
    this.zerosmod$destroyerColor = DestroyerKiColor.DEFAULT;
  }

  @Unique @Override public int zerosmod$getDestroyerColor() {
    return this.zerosmod$destroyerColor;
  }

  @Unique @Override public void zerosmod$setDestroyerColor(int rgb) {
    this.zerosmod$destroyerColor = rgb;
  }
}
