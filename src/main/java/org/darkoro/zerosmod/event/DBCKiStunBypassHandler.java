package org.darkoro.zerosmod.event;

import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import JinRyuu.JRMCore.entity.EntityEnergyAttJ;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.lang.reflect.Field;

public class DBCKiStunBypassHandler {

    private static Field targetField;
    private static Field targetJField;
    private static Field hadTargetField;
    private static Field hadTargetJField;

    static {
        try {
            targetField = EntityEnergyAtt.class.getDeclaredField("target");
            targetField.setAccessible(true);
            hadTargetField = EntityEnergyAtt.class.getDeclaredField("hadTarget");
            hadTargetField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            targetJField = EntityEnergyAttJ.class.getDeclaredField("target");
            targetJField.setAccessible(true);
            hadTargetJField = EntityEnergyAttJ.class.getDeclaredField("hadTarget");
            hadTargetJField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void clearBeamTarget(Entity proj) {
        try {
            if (proj instanceof EntityEnergyAtt) {
                if (targetField != null) targetField.set(proj, null);
                if (hadTargetField != null) hadTargetField.set(proj, false);
            } else if (proj instanceof EntityEnergyAttJ) {
                if (targetJField != null) targetJField.set(proj, null);
                if (hadTargetJField != null) hadTargetJField.set(proj, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        // Applica a tutte le entità viventi tranne i giocatori
        if (event.entityLiving == null || event.entityLiving instanceof EntityPlayer) {
            return;
        }

        Entity attacker = event.source.getSourceOfDamage();
        if (attacker instanceof EntityEnergyAtt || attacker instanceof EntityEnergyAttJ) {
            // Annulla il puntamento del raggio sull'entità colpita in modo che DBC
            // non possa applicare lo slowdown o gli effetti di spinta/trascinamento.
            clearBeamTarget(attacker);
        }
    }
}