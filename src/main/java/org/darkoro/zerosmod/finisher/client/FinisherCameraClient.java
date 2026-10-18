package org.darkoro.zerosmod.finisher.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import org.darkoro.zerosmod.finisher.FinisherCameraEase;
import org.darkoro.zerosmod.finisher.FinisherCameraPacket;
import org.darkoro.zerosmod.finisher.FinisherCameraPathPacket;
import org.darkoro.zerosmod.finisher.FinisherCameraPreset;
import org.darkoro.zerosmod.finisher.FinisherCameraSpline;
import org.lwjgl.input.Mouse;

/**
 * Swaps {@link Minecraft#renderViewEntity} for a cinematic camera that locks shit down
 */
@SideOnly(Side.CLIENT)
public final class FinisherCameraClient {

  public static final FinisherCameraClient INSTANCE = new FinisherCameraClient();

  private final Minecraft mc = Minecraft.getMinecraft();
  // Packets may arrive off the client thread, so they are applied on the next client tick.
  private final AtomicReference<IMessage> pending = new AtomicReference<IMessage>();

  private CameraEntity camera;
  private FinisherCameraPreset preset;
  private int targetId;
  private int duration;
  private int elapsed;
  private double[] lastTarget;
  private double targetHeight;
  private float targetYaw;
  private int sideSign;
  private double introAngle;
  // Set instead of preset while a keyframe path plays
  private FinisherCameraPathPacket path;
  private double[] pathTicks;
  private Frame lastFrame;

  private EntityLivingBase savedView;
  private MovementInput savedInput;
  private MovementInput lockedInput;
  private MouseHelper savedMouse;
  private MouseHelper lockedMouse;
  private boolean savedHideGui;
  private int savedPerspective;

  private FinisherCameraClient() {}

  public void receive(FinisherCameraPacket packet) {
    pending.set(packet);
  }

  public void receive(FinisherCameraPathPacket packet) {
    pending.set(packet);
  }

  @SubscribeEvent
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (event.phase == TickEvent.Phase.START) {
      IMessage packet = pending.getAndSet(null);
      if (packet instanceof FinisherCameraPacket) {
        apply((FinisherCameraPacket)packet);
      } else if (packet instanceof FinisherCameraPathPacket) {
        apply((FinisherCameraPathPacket)packet);
      }
      if (camera != null && !isValid()) {
        stop();
      }
      return;
    }

    if (camera != null && ++elapsed >= duration) {
      stop();
    }
  }

  @SubscribeEvent
  public void onRenderTick(TickEvent.RenderTickEvent event) {
    if (event.phase == TickEvent.Phase.START && camera != null && isValid()) {
      update(event.renderTickTime);
    }
  }

  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (camera != null) {
      KeyBinding.unPressAllKeys();
    }
  }

  @SubscribeEvent
  public void onMouseInput(InputEvent.MouseInputEvent event) {
    if (camera != null) {
      KeyBinding.unPressAllKeys();
    }
  }

  @SubscribeEvent
  public void onRenderHand(RenderHandEvent event) {
    if (camera != null) {
      event.setCanceled(true);
    }
  }

  private void apply(FinisherCameraPacket packet) {
    if (packet.isStop()) {
      stop();
      return;
    }
    if (mc.theWorld == null || mc.thePlayer == null) {
      return;
    }

    preset = FinisherCameraPreset.byId(packet.preset);
    path = null;
    targetId = packet.targetId;
    duration = packet.ticks;
    elapsed = 0;
    lastTarget = null;
    sideSign = 0;
    introAngle = Double.NaN;
    if (camera == null) {
      start();
    }
  }

  private void apply(FinisherCameraPathPacket packet) {
    if (packet.duration() <= 0 || mc.theWorld == null || mc.thePlayer == null) {
      return;
    }

    path = packet;
    pathTicks = new double[packet.ticks.length];
    for (int i = 0; i < pathTicks.length; i++) {
      pathTicks[i] = packet.ticks[i];
    }
    lastFrame = null;
    duration = packet.duration();
    elapsed = 0;
    if (camera == null) {
      start();
    }
  }

  private void start() {
    savedView = mc.renderViewEntity;
    savedInput = mc.thePlayer.movementInput;
    savedMouse = mc.mouseHelper;
    savedHideGui = mc.gameSettings.hideGUI;
    savedPerspective = mc.gameSettings.thirdPersonView;

    lockedInput = new MovementInput();
    lockedMouse = new LockedMouseHelper();
    mc.thePlayer.movementInput = lockedInput;
    mc.mouseHelper = lockedMouse;
    KeyBinding.unPressAllKeys();

    camera = new CameraEntity(mc.theWorld);
    update(0.0F);
    mc.renderViewEntity = camera;
  }

  private void stop() {
    if (camera == null) {
      return;
    }

    if (mc.renderViewEntity == camera) {
      mc.renderViewEntity = canRestoreView() ? savedView : mc.thePlayer;
    }
    if (mc.thePlayer != null && mc.thePlayer.movementInput == lockedInput) {
      mc.thePlayer.movementInput = savedInput;
    }
    if (mc.mouseHelper == lockedMouse) {
      mc.mouseHelper = savedMouse;
    }
    mc.gameSettings.hideGUI = savedHideGui;
    mc.gameSettings.thirdPersonView = savedPerspective;

    camera = null;
    lastTarget = null;
    path = null;
    pathTicks = null;
    lastFrame = null;
    savedView = null;
    savedInput = null;
    lockedInput = null;
    savedMouse = null;
    lockedMouse = null;
  }

  private boolean canRestoreView() {
    return savedView != null && !savedView.isDead && savedView.worldObj == mc.theWorld;
  }

  // Vanilla resets renderViewEntity on respawn and dimension change; shot ending
  private boolean isValid() {
    return mc.theWorld != null && mc.thePlayer != null && camera.worldObj == mc.theWorld
        && mc.renderViewEntity == camera && !mc.thePlayer.isDead && mc.thePlayer.getHealth() > 0.0F;
  }

  private void update(float partialTicks) {
    mc.gameSettings.thirdPersonView = 0;
    mc.gameSettings.hideGUI = true;
    if (path != null) {
      updatePath(partialTicks);
    } else {
      updatePreset(partialTicks);
    }
  }

  private void updatePreset(float partialTicks) {
    double progress = MathHelper.clamp_double((elapsed + partialTicks) / Math.max(1, duration), 0.0D, 1.0D);
    double eased = progress * progress * (3.0D - 2.0D * progress);
    double[] self = center(mc.thePlayer, partialTicks);
    double[] other = targetCenter(partialTicks);

    double distance = 0.0D;
    double heading = Math.toRadians(mc.thePlayer.rotationYaw + 90.0F);
    if (other != null) {
      double dx = other[0] - self[0];
      double dz = other[2] - self[2];
      distance = Math.sqrt(dx * dx + dz * dz);
      if (distance > 0.01D) {
        heading = Math.atan2(dz, dx);
      }
    }
    double forwardX = Math.cos(heading);
    double forwardZ = Math.sin(heading);

    double[] focus = other == null ? self
        : new double[] {(self[0] + other[0]) * 0.5D, (self[1] + other[1]) * 0.5D, (self[2] + other[2]) * 0.5D};
    double[] subject = other == null ? self : other;
    double subjectHeight = other == null ? mc.thePlayer.height : targetHeight;
    double[] anchor;
    double[] lookAt;
    double[] position;

    switch (preset) {
      case SHOULDER: {
        double back = 2.6D - 0.6D * eased;
        double side = 0.9D;
        anchor = self;
        lookAt = other != null ? other : new double[] {self[0] + forwardX * 4.0D, self[1], self[2] + forwardZ * 4.0D};
        position = new double[] {
            self[0] - forwardX * back - forwardZ * side,
            self[1] - 0.1D,
            self[2] - forwardZ * back + forwardX * side};
        break;
      }
      case SIDE: {
        double radius = Math.max(3.5D, distance * 1.2D + 2.0D);
        if (sideSign == 0) {
          sideSign = pickOpenSide(focus, forwardX, forwardZ, radius);
        }
        anchor = focus;
        lookAt = focus;
        position = new double[] {
            focus[0] - forwardZ * radius * sideSign,
            focus[1] + 0.3D,
            focus[2] + forwardX * radius * sideSign};
        break;
      }
      case INTRO: {
        if (Double.isNaN(introAngle)) {
          introAngle = Math.toRadians((other == null ? mc.thePlayer.rotationYawHead : targetYaw) + 90.0F);
        }
        double radius = Math.max(1.0D, subjectHeight / 1.8D) * (6.0D - 3.8D * eased);
        double angle = introAngle - (1.0D - eased) * Math.PI * 0.66D;
        double low = subject[1] - subjectHeight * 0.4D;
        double eye = subject[1] + subjectHeight * 0.25D;
        anchor = subject;
        lookAt = new double[] {subject[0], eye, subject[2]};
        position = new double[] {
            subject[0] + Math.cos(angle) * radius,
            low + (eye - low) * eased,
            subject[2] + Math.sin(angle) * radius};
        break;
      }
      case ORBIT_TARGET: {
        double radius = Math.max(3.0D, subjectHeight * 1.6D + 1.0D);
        double angle = heading + Math.PI * 0.5D + eased * Math.PI * 0.75D;
        anchor = subject;
        lookAt = subject;
        position = new double[] {
            subject[0] + Math.cos(angle) * radius,
            subject[1] + 0.8D + 0.4D * Math.sin(progress * Math.PI),
            subject[2] + Math.sin(angle) * radius};
        break;
      }
      case ORBIT:
      default: {
        double radius = Math.max(3.0D, distance * 0.75D + 2.5D);
        double angle = heading + Math.PI * 0.5D + eased * Math.PI * 0.75D;
        anchor = focus;
        lookAt = focus;
        position = new double[] {
            focus[0] + Math.cos(angle) * radius,
            focus[1] + 0.8D + 0.4D * Math.sin(progress * Math.PI),
            focus[2] + Math.sin(angle) * radius};
        break;
      }
    }

    position = clipToWorld(anchor, position);
    double dx = lookAt[0] - position[0];
    double dy = lookAt[1] - position[1];
    double dz = lookAt[2] - position[2];
    float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
    float pitch = (float)-Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
    camera.moveTo(position[0], position[1], position[2], yaw, pitch);
  }

  private void updatePath(float partialTicks) {
    double progress = MathHelper.clamp_double((elapsed + partialTicks) / Math.max(1, duration), 0.0D, 1.0D);
    double time = FinisherCameraEase.byId(path.ease).apply(progress) * duration;
    Frame frame = frame(partialTicks);
    double[] position = frame.toWorld(
        FinisherCameraSpline.sample(pathTicks, path.x, time),
        FinisherCameraSpline.sample(pathTicks, path.y, time),
        FinisherCameraSpline.sample(pathTicks, path.z, time));

    float yaw;
    float pitch;
    int segment = FinisherCameraSpline.segment(pathTicks, time);
    double[] lookTarget = sharedLookTarget(segment, time, frame, partialTicks);
    if (lookTarget != null) {
      yaw = yawTowards(position, lookTarget);
      pitch = pitchTowards(position, lookTarget);
    } else {
      int keys = pathTicks.length;
      double[] yaws = new double[keys];
      double[] pitches = new double[keys];
      for (int i = 0; i < keys; i++) {
        if (path.lookAt[i]) {
          double[] from = frame.toWorld(path.x[i], path.y[i], path.z[i]);
          double[] at = lookTarget(path.targetIds[i], frame, partialTicks);
          yaws[i] = yawTowards(from, at);
          pitches[i] = pitchTowards(from, at);
        } else {
          yaws[i] = frame.yaw + path.yaw[i];
          pitches[i] = path.pitch[i];
        }
      }
      yaw = (float)FinisherCameraSpline.sample(pathTicks, FinisherCameraSpline.unwrapDegrees(yaws), time);
      pitch = (float)MathHelper.clamp_double(FinisherCameraSpline.sample(pathTicks, pitches, time), -90.0D, 90.0D);
    }

    if (frame.center != null) {
      position = clipToWorld(frame.center, position);
    }
    camera.moveTo(position[0], position[1], position[2], yaw, pitch);
  }

  // Between two keyframes aiming at the same entity, aim from the real camera position so it stays centered.
  private double[] sharedLookTarget(int segment, double time, Frame frame, float partialTicks) {
    int keys = pathTicks.length;
    int from = keys == 1 || time <= pathTicks[0] ? 0 : time >= pathTicks[keys - 1] ? keys - 1 : segment;
    int to = keys == 1 || time <= pathTicks[0] || time >= pathTicks[keys - 1] ? from : segment + 1;
    if (!path.lookAt[from] || !path.lookAt[to] || path.targetIds[from] != path.targetIds[to]) {
      return null;
    }
    return lookTarget(path.targetIds[from], frame, partialTicks);
  }

  private double[] lookTarget(int entityId, Frame frame, float partialTicks) {
    int id = entityId >= 0 ? entityId : path.relativeId;
    Entity entity = id >= 0 ? mc.theWorld.getEntityByID(id) : mc.thePlayer;
    if (entity != null && !entity.isDead) {
      return center(entity, partialTicks);
    }
    return frame.center != null ? frame.center : center(mc.thePlayer, partialTicks);
  }

  // The followed entity's position and body yaw; held at the last known one if it disappears mid-shot.
  private Frame frame(float partialTicks) {
    if (path.relativeId < 0) {
      return Frame.WORLD;
    }

    Entity entity = mc.theWorld.getEntityByID(path.relativeId);
    if ((entity == null || entity.isDead) && lastFrame == null) {
      // Not loaded on this client (yet): follow the player rather than treating offsets as world coordinates
      entity = mc.thePlayer;
    }
    if (entity != null && !entity.isDead) {
      double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - entity.yOffset;
      double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      float yaw = entity instanceof EntityLivingBase
          ? interpolateYaw(((EntityLivingBase)entity).prevRenderYawOffset, ((EntityLivingBase)entity).renderYawOffset, partialTicks)
          : interpolateYaw(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
      lastFrame = new Frame(x, y, z, yaw, center(entity, partialTicks));
    }
    return lastFrame != null ? lastFrame : Frame.WORLD;
  }

  private static float interpolateYaw(float previous, float current, float partialTicks) {
    return previous + MathHelper.wrapAngleTo180_float(current - previous) * partialTicks;
  }

  private static float yawTowards(double[] from, double[] to) {
    return (float)(Math.toDegrees(Math.atan2(to[2] - from[2], to[0] - from[0])) - 90.0D);
  }

  private static float pitchTowards(double[] from, double[] to) {
    double dx = to[0] - from[0];
    double dz = to[2] - from[2];
    return (float)-Math.toDegrees(Math.atan2(to[1] - from[1], Math.sqrt(dx * dx + dz * dz)));
  }

  private double[] targetCenter(float partialTicks) {
    if (targetId < 0) {
      return null;
    }

    Entity target = mc.theWorld.getEntityByID(targetId);
    if (target != null && !target.isDead) {
      lastTarget = center(target, partialTicks);
      targetHeight = target.height;
      targetYaw = target instanceof EntityLivingBase ? ((EntityLivingBase)target).rotationYawHead : target.rotationYaw;
    }
    return lastTarget;
  }

  // Interpolated like RenderGlobal does, so the camera stays locked to the rendered models.
  private static double[] center(Entity entity, float partialTicks) {
    return new double[] {
        entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks,
        entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - entity.yOffset + entity.height * 0.6D,
        entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks};
  }

  // Chosen once per shot so the static camera never flips sides mid-shot.
  private int pickOpenSide(double[] focus, double forwardX, double forwardZ, double radius) {
    double[] right = clipToWorld(focus, new double[] {focus[0] - forwardZ * radius, focus[1] + 0.3D, focus[2] + forwardX * radius});
    double[] left = clipToWorld(focus, new double[] {focus[0] + forwardZ * radius, focus[1] + 0.3D, focus[2] - forwardX * radius});
    return distanceSq(focus, left) > distanceSq(focus, right) + 0.25D ? -1 : 1;
  }

  private static double distanceSq(double[] a, double[] b) {
    double dx = a[0] - b[0];
    double dy = a[1] - b[1];
    double dz = a[2] - b[2];
    return dx * dx + dy * dy + dz * dz;
  }

  private double[] clipToWorld(double[] anchor, double[] position) {
    MovingObjectPosition hit = mc.theWorld.func_147447_a(
        Vec3.createVectorHelper(anchor[0], anchor[1], anchor[2]),
        Vec3.createVectorHelper(position[0], position[1], position[2]), false, true, false);
    if (hit == null || hit.hitVec == null) {
      return position;
    }

    double dx = anchor[0] - hit.hitVec.xCoord;
    double dy = anchor[1] - hit.hitVec.yCoord;
    double dz = anchor[2] - hit.hitVec.zCoord;
    double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
    if (length < 1.0E-4D) {
      return new double[] {hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord};
    }

    double pull = Math.min(0.3D, length) / length;
    return new double[] {hit.hitVec.xCoord + dx * pull, hit.hitVec.yCoord + dy * pull, hit.hitVec.zCoord + dz * pull};
  }

  /** Where path offsets are measured from: world coordinates, or an entity's feet turned with its body yaw. */
  private static final class Frame {

    private static final Frame WORLD = new Frame(0.0D, 0.0D, 0.0D, 0.0F, null);

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    // Middle of the followed entity, which camera positions are clipped against; null in world coordinates
    private final double[] center;

    private Frame(double x, double y, double z, float yaw, double[] center) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = yaw;
      this.center = center;
    }

    // x to the entity's right, y up, z in front of it; yaw 0 faces +Z in Minecraft
    private double[] toWorld(double offsetX, double offsetY, double offsetZ) {
      if (center == null) {
        return new double[] {offsetX, offsetY, offsetZ};
      }

      double radians = Math.toRadians(yaw);
      double sin = Math.sin(radians);
      double cos = Math.cos(radians);
      return new double[] {
          x - cos * offsetX - sin * offsetZ,
          y + offsetY,
          z - sin * offsetX + cos * offsetZ};
    }
  }

  private static final class LockedMouseHelper extends MouseHelper {
    @Override public void mouseXYChange() {
      Mouse.getDX();
      Mouse.getDY();
      deltaX = 0;
      deltaY = 0;
    }
  }

  private static final class CameraEntity extends EntityLivingBase {

    private static final ItemStack[] NO_ITEMS = new ItemStack[5];

    private CameraEntity(World world) {
      super(world);
      // EntityRenderer.orientCamera offsets by (yOffset - 1.62); don't ask me, idfk
      yOffset = 1.62F;
      noClip = true;
    }

    private void moveTo(double x, double y, double z, float yaw, float pitch) {
      setPosition(x, y, z);
      prevPosX = lastTickPosX = x;
      prevPosY = lastTickPosY = y;
      prevPosZ = lastTickPosZ = z;
      rotationYaw = prevRotationYaw = rotationYawHead = prevRotationYawHead = yaw;
      rotationPitch = prevRotationPitch = pitch;
    }

    @Override public ItemStack getHeldItem() {
      return null;
    }

    @Override public ItemStack getEquipmentInSlot(int slot) {
      return null;
    }

    @Override public void setCurrentItemOrArmor(int slot, ItemStack stack) {}

    @Override public ItemStack[] getLastActiveItems() {
      return NO_ITEMS;
    }
  }
}
