package org.darkoro.zerosmod.finisher;

import java.util.Map;
import java.util.TreeMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IEntity;
import org.darkoro.zerosmod.api.ScriptCameraPath;

/** Server-side {@link ScriptCameraPath}: collects the keyframes a script adds and turns them into one packet. */
public final class FinisherCameraPath implements ScriptCameraPath {

  public static final int MAX_KEYS = 64;

  private final TreeMap<Integer, Key> keys = new TreeMap<Integer, Key>();
  private Entity relativeTo;
  private FinisherCameraEase ease = FinisherCameraEase.LINEAR;

  @Override public ScriptCameraPath relativeTo(IEntity entity) {
    relativeTo = entity == null ? null : entity.getMCEntity();
    return this;
  }

  @Override public ScriptCameraPath key(int tick, double x, double y, double z, float yaw, float pitch) {
    if (!isFinite(yaw) || !isFinite(pitch)) {
      throw new IllegalArgumentException("Camera keyframe rotation must be finite, got yaw " + yaw + ", pitch " + pitch);
    }
    put(new Key(tick, x, y, z, yaw, pitch, false, null));
    return this;
  }

  @Override public ScriptCameraPath keyLookAt(int tick, double x, double y, double z, IEntity target) {
    put(new Key(tick, x, y, z, 0.0F, 0.0F, true, target == null ? null : target.getMCEntity()));
    return this;
  }

  @Override public ScriptCameraPath ease(String name) {
    FinisherCameraEase parsed = FinisherCameraEase.byName(name);
    if (parsed == null) {
      throw new IllegalArgumentException("Unknown camera ease '" + name + "', use linear, in, out or inOut");
    }
    ease = parsed;
    return this;
  }

  @Override public int getDuration() {
    return keys.isEmpty() ? 0 : keys.lastKey();
  }

  @Override public int getKeyCount() {
    return keys.size();
  }

  private void put(Key key) {
    if (key.tick < 0 || key.tick > FinisherCamera.MAX_TICKS) {
      throw new IllegalArgumentException("Camera keyframe tick must be 0 to " + FinisherCamera.MAX_TICKS + ", got " + key.tick);
    }
    if (!isFinite(key.x) || !isFinite(key.y) || !isFinite(key.z)) {
      throw new IllegalArgumentException("Camera keyframe position must be finite, got " + key.x + ", " + key.y + ", " + key.z);
    }
    if (!keys.containsKey(key.tick) && keys.size() >= MAX_KEYS) {
      throw new IllegalArgumentException("A camera path holds at most " + MAX_KEYS + " keyframes");
    }
    keys.put(key.tick, key);
  }

  /** @return true when the path has a length and every entity it uses is alive in the player's world */
  boolean isPlayableFor(EntityPlayerMP player) {
    if (getDuration() <= 0 || !isUsable(relativeTo, player)) {
      return false;
    }
    for (Key key : keys.values()) {
      if (key.lookAt && !isUsable(key.target, player)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isUsable(Entity entity, EntityPlayerMP player) {
    return entity == null || (!entity.isDead && entity.worldObj == player.worldObj);
  }

  FinisherCameraPathPacket toPacket() {
    FinisherCameraPathPacket packet = new FinisherCameraPathPacket(relativeTo == null ? -1 : relativeTo.getEntityId(),
        ease.ordinal(), keys.size());
    int i = 0;
    for (Map.Entry<Integer, Key> entry : keys.entrySet()) {
      Key key = entry.getValue();
      packet.ticks[i] = key.tick;
      packet.x[i] = key.x;
      packet.y[i] = key.y;
      packet.z[i] = key.z;
      packet.lookAt[i] = key.lookAt;
      // The followed entity is sent as -1 like null, so both count as the same target between keyframes
      packet.targetIds[i] = key.target == null || key.target == relativeTo ? -1 : key.target.getEntityId();
      packet.yaw[i] = key.yaw;
      packet.pitch[i] = key.pitch;
      i++;
    }
    return packet;
  }

  private static boolean isFinite(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value);
  }

  private static final class Key {

    private final int tick;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean lookAt;
    private final Entity target;

    private Key(int tick, double x, double y, double z, float yaw, float pitch, boolean lookAt, Entity target) {
      this.tick = tick;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.pitch = pitch;
      this.lookAt = lookAt;
      this.target = target;
    }
  }
}
