package org.darkoro.zerosmod.server;

import org.darkoro.zerosmod.zsweapons.PlayerCombatState;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ServerWeaponHandler {
    private final Map<UUID, PlayerCombatState> stateMap = new HashMap<>();

    @SubscribeEvent
    public void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        stateMap.remove(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void hitEvent(AttackEntityEvent event) {
        EntityPlayer player = event.entityPlayer;
        if(player.worldObj.isRemote) return;
        PlayerCombatState state = stateMap.get(player.getUniqueID());
        // Cancel event if attack is invalid
        if(!(event.target instanceof EntityLiving target) || !isValidAttack(state, player, target)) {
            event.setCanceled(true);
            return;
        }
        // Reset cooldown if event is valid
        state.handleAttack();
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event) {
        if(event.side.isClient()) return;
        EntityPlayer player = event.player;
        PlayerCombatState state = stateMap.computeIfAbsent(player.getUniqueID(), k -> new PlayerCombatState());

        // Run combat tick at the start of the tick
        if(event.phase == TickEvent.Phase.START) {
            state.tick();
        } else {
            // Detect item slot change and update attack cooldown
            ItemStack newItem = player.getHeldItem();
            if(!(itemsAreEqual(state.currentItem, newItem))) {
                state.changeItem(newItem);
            }
        }
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
    public static boolean isValidAttack(PlayerCombatState state, EntityPlayer player, EntityLiving target) {
        return (
                player != null &&
                target != null &&
                state != null &&
                state.remainingCooldown <= 0 &&
                player.getDistanceToEntity(target) < state.range &&
                itemsAreEqual(state.currentItem, player.getHeldItem())
        );
    }
}
