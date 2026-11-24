package org.darkoro.zerosmod.dome;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kamkeel.npcs.entity.EntityEnergyDome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import noppes.npcs.EventHooks;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.DataScript;
import noppes.npcs.controllers.data.IScriptUnit;
import noppes.npcs.controllers.data.PlayerDataScript;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.api.entity.IEnergyDome;
import noppes.npcs.api.entity.IEntity;

public final class EnergyDomeEventHooks {

  public static final String ENTER_HOOK = "energyDomeEnter";
  public static final String EXIT_HOOK = "energyDomeExit";
  public static final EnergyDomeEventHooks INSTANCE = new EnergyDomeEventHooks();

  /** Domes with entities inside; held strongly so killed domes still fire their exits. */
  private static final Set<EntityEnergyDome> OCCUPIED_DOMES = new HashSet<EntityEnergyDome>();
  /** Unloaded domes; their exits wait for the next server tick so scripts don't run mid-unload. */
  private static final List<EntityEnergyDome> UNLOADED_DOMES = new ArrayList<EntityEnergyDome>();

  private EnergyDomeEventHooks() {}

  public static void setOccupied(EntityEnergyDome dome, boolean occupied) {
    if (occupied) {
      OCCUPIED_DOMES.add(dome);
    } else {
      OCCUPIED_DOMES.remove(dome);
    }
  }

  /** Drops domes of a stopping server, so a singleplayer restart starts clean. */
  public static void clear() {
    OCCUPIED_DOMES.clear();
    UNLOADED_DOMES.clear();
  }

  @SubscribeEvent public void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase != TickEvent.Phase.END
        || OCCUPIED_DOMES.isEmpty() && UNLOADED_DOMES.isEmpty()) {
      return;
    }

    List<EntityEnergyDome> removed = null;
    if (!UNLOADED_DOMES.isEmpty()) {
      removed = new ArrayList<EntityEnergyDome>(UNLOADED_DOMES);
      UNLOADED_DOMES.clear();
    }
    // Killed domes are removed before their next tick.
    for (EntityEnergyDome dome : OCCUPIED_DOMES) {
      if (dome.isDead) {
        if (removed == null) {
          removed = new ArrayList<EntityEnergyDome>();
        }
        removed.add(dome);
      }
    }
    if (removed != null) {
      release(removed);
    }
  }

  @SubscribeEvent public void onChunkUnload(ChunkEvent.Unload event) {
    Chunk chunk = event.getChunk();
    if (OCCUPIED_DOMES.isEmpty() || event.world == null || event.world.isRemote
        || chunk == null) {
      return;
    }

    for (Iterator<EntityEnergyDome> it = OCCUPIED_DOMES.iterator(); it.hasNext(); ) {
      EntityEnergyDome dome = it.next();
      if (dome.worldObj == event.world && dome.addedToChunk
          && dome.chunkCoordX == chunk.xPosition && dome.chunkCoordZ == chunk.zPosition) {
        it.remove();
        UNLOADED_DOMES.add(dome);
      }
    }
  }

  @SubscribeEvent public void onWorldUnload(WorldEvent.Unload event) {
    World world = event.world;
    if (OCCUPIED_DOMES.isEmpty() || world == null || world.isRemote) {
      return;
    }

    for (Iterator<EntityEnergyDome> it = OCCUPIED_DOMES.iterator(); it.hasNext(); ) {
      EntityEnergyDome dome = it.next();
      if (dome.worldObj == world) {
        it.remove();
        UNLOADED_DOMES.add(dome);
      }
    }
  }

  private static void release(List<EntityEnergyDome> domes) {
    for (EntityEnergyDome dome : domes) {
      ((EnergyDomeExtension)dome).zerosmod$releaseInsideEntities();
    }
  }

  public static void fireEnter(EntityEnergyDome dome, Entity entity) {
    fire(dome, entity, true);
  }

  public static void fireExit(EntityEnergyDome dome, Entity entity) {
    fire(dome, entity, false);
  }

  private static void fire(EntityEnergyDome dome, Entity entity, boolean entering) {
    if (dome == null || entity == null || dome.worldObj == null || dome.worldObj.isRemote
        || ScriptController.Instance == null) {
      return;
    }

    IEnergyDome scriptDome = (IEnergyDome)NpcAPI.Instance().getIEntity(dome);
    IEntity scriptEntity = NpcAPI.Instance().getIEntity(entity);
    EnergyDomeEvent event = entering
        ? new EnergyDomeEvent.Enter(scriptDome, scriptEntity)
        : new EnergyDomeEvent.Exit(scriptDome, scriptEntity);

    Entity owner = dome.getOwnerEntity();
    if (owner instanceof EntityNPCInterface) {
      callNpcScripts((EntityNPCInterface)owner, event);
    } else if (owner instanceof EntityPlayer) {
      PlayerDataScript scripts = ScriptController.Instance.getPlayerScripts((EntityPlayer)owner);
      if (scripts != null) {
        scripts.callScript(event.getHookName(), event);
      }
    }

    NpcAPI.EVENT_BUS.post(event);
  }

  /** NPC callScript(String) only knows CNPC+'s built-in hooks, so this mirrors its housekeeping. */
  private static void callNpcScripts(EntityNPCInterface npc, EnergyDomeEvent event) {
    DataScript scripts = npc.script;
    applyPendingUpdates(npc, scripts);
    if (!scripts.isEnabled()) {
      return;
    }
    if (!scripts.hasInited) {
      scripts.hasInited = true;
      for (IScriptUnit unit : scripts.getScripts()) {
        unit.setErrored(false);
      }
      EventHooks.onNPCInit(npc);
    }
    for (IScriptUnit unit : scripts.getScripts()) {
      unit.run(event.getHookName(), event);
    }
    applyPendingUpdates(npc, scripts);
  }

  private static void applyPendingUpdates(EntityNPCInterface npc, DataScript scripts) {
    if (scripts.aiNeedsUpdate) {
      npc.updateAI = true;
      scripts.aiNeedsUpdate = false;
    }
    if (scripts.clientNeedsUpdate) {
      npc.updateClient = true;
      scripts.clientNeedsUpdate = false;
    }
  }
}
