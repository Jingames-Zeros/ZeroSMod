package org.darkoro.zerosmod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import noppes.npcs.api.overlay.ICustomOverlayComponent;
import noppes.npcs.api.overlay.IOverlayLine;
import noppes.npcs.scripted.entity.ScriptPlayer;
import noppes.npcs.scripted.overlay.ScriptOverlay;

public class ZSWeaponHandler {
    private int remainingCooldown = -1;
    private int lastCooldown;
    private ItemStack heldItem;
    private ScriptPlayer sp;
    private ScriptOverlay cooldownOverlay;
    private Minecraft mc;


    public ZSWeaponHandler() {

    }

    @SubscribeEvent
    public void logIn(PlayerEvent.PlayerLoggedInEvent event) {
        this.mc = Minecraft.getMinecraft();
        this.sp = new ScriptPlayer((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void hitEvent(AttackEntityEvent event) {
        EntityPlayer player = event.entityPlayer;
        if(player == null || player.worldObj.isRemote) return;
        // Cancel event if player attack on cooldown
        if(
                this.remainingCooldown > 0 ||
                !(ItemStack.areItemStacksEqual(this.heldItem, player.getHeldItem()) &&
                ItemStack.areItemStackTagsEqual(this.heldItem, player.getHeldItem()))
        ) {
            event.setCanceled(true);
            return;
        }
        updateAttackCooldown(player);
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.thePlayer == null) return;
        // Check item at the start of the tick to prevent attribute swapping
        if(event.phase == TickEvent.Phase.START) {
            this.heldItem = mc.thePlayer.getHeldItem();
            if(this.remainingCooldown > 0) this.remainingCooldown --;
        } else {
            // Detect item slot change and update attack cooldown
            if(!(ItemStack.areItemStacksEqual(this.heldItem, mc.thePlayer.getHeldItem()) && ItemStack.areItemStackTagsEqual(this.heldItem, mc.thePlayer.getHeldItem()))) {
                mc.thePlayer.getHeldItem();
                updateAttackCooldown(mc.thePlayer);
            }
            updateCooldownOverlay(mc.thePlayer);
        }
    }

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
     */
    public void updateCooldownOverlay(EntityPlayer player) {
        if(this.sp == null || this.cooldownOverlay == null) return;
        if(this.remainingCooldown < 1) {
            this.sp.closeOverlay(742);
        } else {
            IOverlayLine mainLine = (IOverlayLine) this.cooldownOverlay.getComponent(2);
            int width = (int) (mainLine.getX1() / 0.47);
            int newX2 = (int) (width * 0.53 - width * 0.06 * (1 - (double)this.remainingCooldown / this.lastCooldown));
            mainLine.setX2(newX2);
            this.cooldownOverlay.update(this.sp);
        }
    }

    public void setUpOverlay() {
        if(this.mc == null) return;
        ScaledResolution sr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        int actualX1 = (int) (sr.getScaledWidth() * 0.47);
        int actualY1 = (int) (sr.getScaledHeight() * 0.55);
        int actualX2 = (int) (sr.getScaledWidth() * 0.53);
        int actualY2 = (int) (sr.getScaledHeight() * 0.55);

        // Add shadow
        this.cooldownOverlay = new ScriptOverlay(742);
        IOverlayLine shadowLine = this.cooldownOverlay.addLine(1, (int) actualX1 - 1, (int) actualY1 + 1, (int) actualX2 + 1, (int) actualY2 + 1);
        shadowLine.setColor(0);
        shadowLine.setThickness((actualX2 - actualX1) / 7);
        shadowLine.setAlpha(0.5F);
        IOverlayLine mainLine = this.cooldownOverlay.addLine(2, actualX1, actualY1, actualX2, actualY2);
        mainLine.setColor(0x00FFFF);
        mainLine.setThickness((actualX2 - actualX1) / 7 - 2);
        mainLine.setAlpha(0.7F);
        this.sp.showCustomOverlay(this.cooldownOverlay);
    }
}
