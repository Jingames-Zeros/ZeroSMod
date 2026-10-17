package org.darkoro.zerosmod.mixin.mixins.late.impl.npc;

import kamkeel.npcs.controllers.data.ability.type.energy.AbilityDome;
import org.darkoro.zerosmod.api.ScriptAbilityDomeMethods;
import org.spongepowered.asm.mixin.Mixin;

/** Adds the missing typed script interface to CNPC+'s existing public dome settings. */
@Mixin(value = AbilityDome.class, remap = false)
public abstract class MixinAbilityDomeScriptMethods implements ScriptAbilityDomeMethods {}
