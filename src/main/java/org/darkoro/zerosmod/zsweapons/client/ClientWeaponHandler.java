package org.darkoro.zerosmod.zsweapons.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.ClientWeaponConfig;
import org.darkoro.zerosmod.zsweapons.network.CooldownToClientPacket;
import org.darkoro.zerosmod.zsweapons.network.TargetEntityToServerPacket;

import java.util.List;

public class ClientWeaponHandler {
    public static final ClientWeaponHandler INSTANCE = new ClientWeaponHandler();

    private double remainingCooldown = -1;
    private int lastCooldown = 20;
    private double currentTps = 20.0D;
    private final ClientWeaponConfig.hudConfig config = ClientWeaponConfig.getHudConfig();

    private int x1;
    private int x2;
    private int y1;
    private int y2;

    public ClientWeaponHandler() {}

    public void handleCooldownPacket(CooldownToClientPacket message) {
        this.remainingCooldown = message.cooldown;
        this.lastCooldown = message.fullCooldown;
        this.currentTps = message.tps;
        calculateOverlayDimensions();
    }

    @SubscribeEvent
    public void mouseClick(MouseEvent event) {
        if(event.button != 0 || !event.buttonstate || remainingCooldown > 0) return;
        EntityLivingBase target = getExtendedReachTarget();
        if(target != null) {
            TargetEntityToServerPacket packet = new TargetEntityToServerPacket(target.getEntityId());
            ZeroSMod.network.sendToServer(packet);
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END && remainingCooldown > 0) {
            remainingCooldown -= currentTps / 20.0D;
        }
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if(remainingCooldown > 0) updateOverlay();
    }

    /**
     * Calculate overlay dimensions before displaying rather than every tick
     */
    public void calculateOverlayDimensions() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        x1 = (int) (sr.getScaledWidth() * config.x1);
        y1 = (int) (sr.getScaledHeight() * config.y1);
        x2 = (int) (sr.getScaledWidth() * config.x2);
        y2 = (int) (sr.getScaledHeight() * config.y2);
    }

    /**
     * Draw overlay on screen
     */
    public void updateOverlay() {
        // Add shadow
        Gui.drawRect(x1 - 1, y1 - 1, x2 + 1, y2 + 1, config.progressBarShadowColour);
        Gui.drawRect(x1, y1, x1 + (int) ((x2 - x1) * remainingCooldown / lastCooldown), y2, config.progressBarColour);
    }

    /**
     * Finds a target further than vanilla range but within extended reach range
     */
    public EntityLivingBase getExtendedReachTarget() {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.renderViewEntity == null || mc.theWorld == null) return null;
        double maxHitDistance = 10.0D;
        double closestHitDistance = maxHitDistance;
        double vanillaReach = 3.0D;
        double searchPadding = 1.0F;
        EntityLivingBase targetEntity = null;

        Vec3 eyePosition = mc.renderViewEntity.getPosition(1.0F);
        Vec3 playerLookVector = mc.renderViewEntity.getLook(1.0F);
        Vec3 rayEnd = eyePosition.addVector(playerLookVector.xCoord * maxHitDistance, playerLookVector.yCoord * maxHitDistance, playerLookVector.zCoord * maxHitDistance);
        List<Entity> nearbyEntities = mc.theWorld
                .getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox
                .addCoord(playerLookVector.xCoord * maxHitDistance, playerLookVector.yCoord * maxHitDistance, playerLookVector.zCoord * maxHitDistance)
                .expand(searchPadding, searchPadding, searchPadding));

        for(Entity obj : nearbyEntities) {
            if(!(obj instanceof EntityLivingBase entity) || !entity.canBeCollidedWith()) continue;

            float collisionBorderSize = entity.getCollisionBorderSize();
            AxisAlignedBB expandedHitbox = entity.boundingBox.expand((double)collisionBorderSize, (double)collisionBorderSize, (double)collisionBorderSize);
            MovingObjectPosition intercept = expandedHitbox.calculateIntercept(eyePosition, rayEnd);

            // If player eye position is inside entity hitbox
            if(expandedHitbox.isVecInside(eyePosition)) {
                    targetEntity = entity;
                    closestHitDistance = 0.0D;
                    continue;
            }
            // Raycast doesn't an entity hitbox
            else if(intercept == null) continue;

            double distanceToHitVec = eyePosition.distanceTo(intercept.hitVec);
            if(distanceToHitVec < closestHitDistance) {
                targetEntity = entity;
                closestHitDistance = distanceToHitVec;
            }
        }
        // If entity is within extended range but outside of vanilla range
        if(closestHitDistance <= vanillaReach) {
            return null;
        } else {
            return targetEntity;
        }
    }
}
