package org.darkoro.zerosmod.zsweapons.server;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import noppes.npcs.scripted.event.NpcEvent;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.zsweapons.CachedWeaponState;
import org.darkoro.zerosmod.zsweapons.network.CooldownToClientPacket;
import org.darkoro.zerosmod.zsweapons.network.TargetEntityToServerPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.darkoro.zerosmod.zsweapons.ZSWeaponUtils.*;

public class ServerWeaponHandler {
    public static final ServerWeaponHandler INSTANCE = new ServerWeaponHandler();
    private final Map<UUID, CachedWeaponState> stateMap = new HashMap<>();

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        stateMap.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void hitEvent(AttackEntityEvent event) {
        EntityPlayer player = event.entityPlayer;
        CachedWeaponState state = stateMap.get(player.getUniqueID());
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
        CachedWeaponState state = stateMap.get(player.getUniqueID());
        if(state.attackMultiplier == 1.0F) return;
        float extraDamage = getMultiplierBonusDamage(player, state.attackMultiplier);
        event.setDamage(event.getDamage() + extraDamage);
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        CachedWeaponState state = stateMap.computeIfAbsent(player.getUniqueID(), k -> new CachedWeaponState());

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
    public void startCooldown(EntityPlayerMP player, CachedWeaponState state) {
        state.resetCooldown();
        sendCooldownPacketToClient(player, state);
    }

    /**
     * Sends original cooldown, remaining cooldown and menaTPS to client
     * @param player client to send to
     * @param state client's combat state
     */
    private void sendCooldownPacketToClient(EntityPlayerMP player, CachedWeaponState state) {
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
        CachedWeaponState state = stateMap.get(player.getUniqueID());
        if(!isValidAttack(state, player, target)) return;
        player.attackTargetEntityWithCurrentItem(target);
    }

    public CachedWeaponState getPlayerState(EntityPlayer player) {
        return stateMap.get(player.getUniqueID());
    }
}
