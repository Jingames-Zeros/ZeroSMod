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
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.config.ServerWeaponConfig;
import org.darkoro.zerosmod.zsweapons.PlayerCombatState;
import org.darkoro.zerosmod.zsweapons.network.packets.CooldownToClientPacket;
import org.darkoro.zerosmod.zsweapons.network.packets.TargetEntityToServerPacket;
import org.darkoro.zerosmod.zsweapons.network.packets.WeaponTypesToClientPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.darkoro.zerosmod.zsweapons.ZSWeaponUtils.*;

public class ServerWeaponHandler {
    public static final ServerWeaponHandler INSTANCE = new ServerWeaponHandler();
    private final Map<UUID, PlayerCombatState> stateMap = new HashMap<>();

    @SubscribeEvent
    public void login(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.player instanceof EntityPlayerMP player) {
            getPlayerState(player).getItemStats().changeItem(player.getHeldItem());
            sendWeaponTypesPacketToClient(player);
        }
    }

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        stateMap.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void hitEvent(AttackEntityEvent event) {
        EntityPlayer player = event.entityPlayer;
        if(player.worldObj.isRemote) return;
        PlayerCombatState state = getPlayerState(player);
        // Cancel event if attack is invalid
        if(!(event.target instanceof EntityLivingBase target) || !isValidAttack(state, player, target)) {
            event.setCanceled(true);
            return;
        }
        // Reset cooldown if event is valid
        state.handleAttack();
        sendCooldownPacketToClient((EntityPlayerMP) player, state);
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if(event.side.isClient()) return;
        EntityPlayer player = event.player;
        PlayerCombatState state = stateMap.computeIfAbsent(player.getUniqueID(), k -> new PlayerCombatState());

        // Run combat tick at the start of the tick
        if(event.phase == TickEvent.Phase.START) {
            state.tick(1.0D);
            // Periodically send cooldown to resync with server
            if(state.getRemainingAttackCooldown() > 0 && state.getRemainingAttackCooldown() % 5 == 0) {
                sendCooldownPacketToClient((EntityPlayerMP) player, state);
            }
        } else {
            // Detect item slot change and update attack cooldown
            ItemStack newItem = player.getHeldItem();
            if(!(itemsAreEqual(state.getCurrentItem(), newItem))) {
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
     * Returns player's current combat state
     * @param player .
     * @return CachedWeaponState
     */
    public PlayerCombatState getPlayerState(EntityPlayer player) {
        return stateMap.computeIfAbsent(player.getUniqueID(), k -> new PlayerCombatState());
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
        CooldownToClientPacket packet = new CooldownToClientPacket(state.getRemainingAttackCooldown(), meanTPS);
        ZeroSMod.network.sendTo(packet, player);
    }

    /**
     * Sends a weapon types packet to the client
     * @param player - MP player
     */
    private void sendWeaponTypesPacketToClient(EntityPlayerMP player) {
        WeaponTypesToClientPacket packet = new WeaponTypesToClientPacket(ServerWeaponConfig.loadedWeaponStats);
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
        PlayerCombatState state = getPlayerState(player);
        if(!isValidAttack(state, player, target)) return;
        player.attackTargetEntityWithCurrentItem(target);
    }
}
