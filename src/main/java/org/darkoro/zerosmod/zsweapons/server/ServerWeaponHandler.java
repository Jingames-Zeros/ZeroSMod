package org.darkoro.zerosmod.zsweapons.server;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import noppes.npcs.scripted.event.NpcEvent;
import kamkeel.npcdbc.util.DBCUtils;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.zsweapons.PlayerCombatState;
import org.darkoro.zerosmod.zsweapons.network.CooldownToClientPacket;
import org.darkoro.zerosmod.zsweapons.network.TargetEntityToServerPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerWeaponHandler {
    public static final ServerWeaponHandler INSTANCE = new ServerWeaponHandler();
    private final Map<UUID, PlayerCombatState> stateMap = new HashMap<>();

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        stateMap.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void hitEvent(AttackEntityEvent event) {
        EntityPlayer player = event.entityPlayer;
        PlayerCombatState state = stateMap.get(player.getUniqueID());
        // Cancel event if attack is invalid
        if(!(event.target instanceof EntityLivingBase target) || !isValidAttack(state, player, target)) {
            event.setCanceled(true);
            return;
        }
        // Reset cooldown if event is valid
        state.handleAttack();
        sendCooldownPacketToClient((EntityPlayerMP) player, state);
    }

    // Add on weapon multiplier damage on npc hits
    @SubscribeEvent
    public void damageNpc(NpcEvent.DamagedEvent event) {
        if(event.getSource() == null || !(event.getSource().getMCEntity() instanceof EntityPlayer player)) return;
        PlayerCombatState state = stateMap.get(player.getUniqueID());
        if(state.attackMultiplier == 1.0F) return;
        float extraDamage = getMultiplierBonusDamage(player, state.attackMultiplier);
        event.setDamage(event.getDamage() + extraDamage);
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        PlayerCombatState state = stateMap.computeIfAbsent(player.getUniqueID(), k -> new PlayerCombatState());

        // Run combat tick at the start of the tick
        if(event.phase == TickEvent.Phase.START) {
            state.tick();
            // Periodically send cooldown to resync with server
            if(state.remainingCooldown > 0 && state.remainingCooldown % 5 == 0) {
                sendCooldownPacketToClient((EntityPlayerMP) player, state);
            }
        } else {
            // Detect item slot change and update attack cooldown
            ItemStack newItem = player.getHeldItem();
            if(!(itemsAreEqual(state.currentItem, newItem))) {
                state.changeItem(newItem);
                startCooldown((EntityPlayerMP) player, state);
            }
        }
    }

    /**
     * Start attack cooldown and send client packet
     * @param player client to send to
     * @param state state to reset cooldown of
     */
    public void startCooldown(EntityPlayerMP player, PlayerCombatState state) {
        state.resetCooldown();
        sendCooldownPacketToClient(player, state);
    }

    /**
     * Sends original cooldown, remaining cooldown and menaTPS to client
     * @param player client to send to
     * @param state client's combat state
     */
    private void sendCooldownPacketToClient(EntityPlayerMP player, PlayerCombatState state) {
        // Calculate current server tps
        long sum = 0L;
        long[] tickTimeArray = MinecraftServer.getServer().tickTimeArray;
        for (long v : tickTimeArray) sum+=v;
        sum /= tickTimeArray.length;
        double meanTickTime = sum * 1.0E-6D;
        double meanTPS = Math.min(1000.0/meanTickTime, 20);
        
        // Build and send packet
        CooldownToClientPacket packet = new CooldownToClientPacket(state.cooldown, state.remainingCooldown, meanTPS);
        ZeroSMod.network.sendTo(packet, player);
    }

    /**
     * Handles attacking extended reach targets received from client
     * @param message .
     * @param ctx .
     */
    public void handleTargetEntityPacket(TargetEntityToServerPacket message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        EntityLivingBase target = (EntityLivingBase) player.worldObj.getEntityByID(message.entityId);
        PlayerCombatState state = stateMap.get(player.getUniqueID());
        if(!isValidAttack(state, player, target)) return;
        player.attackTargetEntityWithCurrentItem(target);
    }

    /**
     * Calculates additional damage from multiplier
     * @param player .
     * @param multiplier float multiplier
     * @return float
     */
    public float getMultiplierBonusDamage(EntityPlayer player, float multiplier) {
        float meleeDamage = DBCUtils.calculateAttackStat(player, 0, DamageSource.causePlayerDamage(player));
        return meleeDamage * multiplier - meleeDamage;
    }

    /**
     * Compares two item stacks and their NBTs
     * @param item1 ItemStack
     * @param item2 ItemStack
     * @return if items are equal
     */
    public static boolean itemsAreEqual(ItemStack item1, ItemStack item2) {
        return ItemStack.areItemStacksEqual(item1, item2) && ItemStack.areItemStackTagsEqual(item1, item2);
    }

    /**
     * Checks attack conditions to determine if an attack is valid
     * @param state PlayerCombatState
     * @param player Player doing the attack
     * @param target Target of the attack
     * @return Boolean
     */
    public static boolean isValidAttack(PlayerCombatState state, EntityPlayer player, EntityLivingBase target) {
        return (
                player != null &&
                target != null &&
                state != null &&
                state.remainingCooldown <= 0 &&
                distanceSqToHitBox(player, target) < state.getRangeSq() &&
                itemsAreEqual(state.currentItem, player.getHeldItem())
        );
    }

    /**
     * Gets an estimated distance to target hitbox from the player's eye height
     * @param attacker attacking player
     * @param target .
     * @return double
     */
    public static double distanceSqToHitBox(EntityPlayer attacker, EntityLivingBase target) {
        AxisAlignedBB hitbox = target.boundingBox;
        double x = Math.max(hitbox.minX, Math.min(attacker.posX, hitbox.maxX));
        double y = Math.max(hitbox.minY, Math.min(attacker.posY + attacker.getEyeHeight(), hitbox.maxY));
        double z = Math.max(hitbox.minZ, Math.min(attacker.posZ, hitbox.maxZ));

        double dx = attacker.posX - x;
        double dy = attacker.posY + attacker.getEyeHeight() - y;
        double dz = attacker.posZ - z;

        return dx * dx + dy * dy + dz * dz;
    }
}
