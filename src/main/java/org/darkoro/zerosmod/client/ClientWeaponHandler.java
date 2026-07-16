package org.darkoro.zerosmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.overlay.IOverlayLine;
import noppes.npcs.scripted.entity.ScriptPlayer;
import noppes.npcs.scripted.overlay.ScriptOverlay;

/**
 * TODO:
 * 1. Add listener to left click sending a packet on entity found
 * 2. Receive packet to start cooldown overlay
 */

public class ClientWeaponHandler {
    private int remainingCooldown = -1;
    private int lastCooldown;
    private ScriptPlayer sp;
    private ScriptOverlay cooldownOverlay;
    private Minecraft mc;

    public ClientWeaponHandler() {}

    /**
     * Updates the player's attack cooldown based on current held item
     */
    public void updateAttackCooldown(EntityPlayer player) {
        ItemStack item = player.getHeldItem();
        int cooldown = 20;
        if(item != null && item.getTagCompound() != null && item.getTagCompound().hasKey("AttackCooldown")) {
            cooldown = item.getTagCompound().getInteger("AttackCooldown");
        }
        this.remainingCooldown = cooldown;
        this.lastCooldown = cooldown;
        setUpOverlay();
    }

    /**
     * Sets up attack cooldown overlay updating position relative to screen size
     */
    public void setUpOverlay() {
        if(this.mc == null) return;
        ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int actualX1 = (int) (sr.getScaledWidth() * 0.47);
        int actualY1 = (int) (sr.getScaledHeight() * 0.55);
        int actualX2 = (int) (sr.getScaledWidth() * 0.53);
        int actualY2 = (int) (sr.getScaledHeight() * 0.55);

        // Add shadow
        this.cooldownOverlay = new ScriptOverlay(742);
        IOverlayLine shadowLine = this.cooldownOverlay.addLine(1,actualX1 - 1,actualY1 + 1,actualX2 + 1,actualY2 + 1);
        shadowLine.setColor(0);
        shadowLine.setThickness((actualX2 - actualX1) / 7);
        shadowLine.setAlpha(0.5F);
        IOverlayLine mainLine = this.cooldownOverlay.addLine(2, actualX1, actualY1, actualX2, actualY2);
        mainLine.setColor(0x00FFFF);
        mainLine.setThickness((actualX2 - actualX1) / 7 - 2);
        mainLine.setAlpha(0.7F);
        this.sp.showCustomOverlay(this.cooldownOverlay);
    }

    /**
     * Updates existing overlay with updated bar progress
     */
    public void updateCooldownOverlay() {
        if(this.sp == null || this.cooldownOverlay == null) return;
        if(this.remainingCooldown == 0) {
            this.sp.closeOverlay(742);
            this.remainingCooldown = -1;
        } else if(this.remainingCooldown > 0) {
            IOverlayLine mainLine = (IOverlayLine) this.cooldownOverlay.getComponent(2);
            int width = (int) (mainLine.getX1() / 0.47);
            int newX2 = (int) (width * 0.53 - width * 0.06 * (1 - (double)this.remainingCooldown / this.lastCooldown));
            mainLine.setX2(newX2);
            this.cooldownOverlay.update(this.sp);
        }
    }
}
