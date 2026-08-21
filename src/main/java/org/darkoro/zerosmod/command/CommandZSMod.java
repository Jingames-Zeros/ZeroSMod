package org.darkoro.zerosmod.command;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import noppes.npcs.Server;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.item.ScriptItemStack;
import org.darkoro.zerosmod.ZeroSMod;
import org.darkoro.zerosmod.api.ScriptZSWeapon;
import org.darkoro.zerosmod.config.*;
import org.darkoro.zerosmod.event.SaiyanMasteryMergeEvent;
import org.darkoro.zerosmod.network.BiomeVisualSyncUtil;
import org.darkoro.zerosmod.network.SyncDimensionConfigPacket;
import org.darkoro.zerosmod.scripted.ZeroSAPI;
import org.darkoro.zerosmod.zsweapons.ZSWeaponUtils;
import org.darkoro.zerosmod.zsweapons.cache.CachedWeaponStats;
import org.darkoro.zerosmod.zsweapons.network.packets.WeaponTypesToClientPacket;

import java.util.*;

public class CommandZSMod extends CommandBase {

  private static final String PREFIX = EnumChatFormatting.AQUA + "" + EnumChatFormatting.BOLD + "["
      + EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.BOLD + "Zero"
      + EnumChatFormatting.GREEN + EnumChatFormatting.BOLD + "S"
      + EnumChatFormatting.GOLD + EnumChatFormatting.BOLD + "Mod"
      + EnumChatFormatting.AQUA + EnumChatFormatting.BOLD + "] "
      + EnumChatFormatting.RESET;

  private final Map<String, ZSSubCommand> subCommands = new LinkedHashMap<String, ZSSubCommand>();

  public CommandZSMod() {
    registerSubCommand(new HelpSubCommand());
    registerSubCommand(new ReloadSubCommand());
    registerSubCommand(new SaiyanMergeSubCommand());
    registerSubCommand(new SetItemTypeCommand());
  }

  @Override
  public String getCommandName() {
    return "zsmod";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/zsmod [help|reload|saiyanmerge]";
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public boolean canCommandSenderUseCommand(ICommandSender sender) {
    return true;
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    if (args == null || args.length == 0) {
      sendHelp(sender);
      return;
    }

    ZSSubCommand subCommand = getSubCommand(args[0]);
    if (subCommand == null) {
      sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + "Unknown subcommand: " + args[0]));
      sendHelp(sender);
      return;
    }

    if (!subCommand.canUse(sender)) {
      throw new CommandException("You do not have permission to use /zsmod " + subCommand.getName());
    }

    subCommand.process(sender, withoutFirstArg(args));
  }

  @Override
  public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
    if (args == null || args.length == 0) {
      return Collections.emptyList();
    }

    if (args.length == 1) {
      return getListOfStringsMatchingLastWord(args, getUsableSubCommandNames(sender));
    }

    ZSSubCommand subCommand = getSubCommand(args[0]);
    if (subCommand == null || !subCommand.canUse(sender)) {
      return Collections.emptyList();
    }

    return subCommand.addTabCompletionOptions(sender, withoutFirstArg(args));
  }

  @Override
  public boolean isUsernameIndex(String[] args, int index) {
    if (args == null || args.length == 0 || index == 0) {
      return false;
    }

    ZSSubCommand subCommand = getSubCommand(args[0]);
    return subCommand != null && subCommand.isUsernameIndex(withoutFirstArg(args), index - 1);
  }

  private void registerSubCommand(ZSSubCommand subCommand) {
    subCommands.put(subCommand.getName().toLowerCase(), subCommand);
  }

  private ZSSubCommand getSubCommand(String name) {
    if (name == null) {
      return null;
    }

    return subCommands.get(name.toLowerCase());
  }

  private String[] getUsableSubCommandNames(ICommandSender sender) {
    List<String> names = new ArrayList<String>();
    for (ZSSubCommand subCommand : subCommands.values()) {
      if (subCommand.canUse(sender)) {
        names.add(subCommand.getName());
      }
    }

    return names.toArray(new String[names.size()]);
  }

  private String[] withoutFirstArg(String[] args) {
    return Arrays.copyOfRange(args, 1, args.length);
  }

  private void sendHelp(ICommandSender sender) {
    sender.addChatMessage(createHelpHeader());

    for (ZSSubCommand subCommand : subCommands.values()) {
      sender.addChatMessage(createHelpLine(subCommand));
    }
  }

  private IChatComponent createHelpHeader() {
    IChatComponent header = chat("-----", EnumChatFormatting.AQUA, true);
    header.appendSibling(chat("Zero", EnumChatFormatting.DARK_PURPLE, true));
    header.appendSibling(chat(" ", EnumChatFormatting.GRAY, true));
    header.appendSibling(chat("S", EnumChatFormatting.GREEN, true));
    header.appendSibling(chat(" ", EnumChatFormatting.GRAY, true));
    header.appendSibling(chat("Mod", EnumChatFormatting.GOLD, true));
    header.appendSibling(chat(" Commands", EnumChatFormatting.GOLD, true));
    header.appendSibling(chat("-----", EnumChatFormatting.AQUA, true));
    return header;
  }

  private IChatComponent createHelpLine(ZSSubCommand subCommand) {
    IChatComponent line = chat("> ", EnumChatFormatting.GRAY);
    line.appendSibling(chat(getHelpUsage(subCommand), EnumChatFormatting.GREEN));

    line.appendSibling(chat(": ", EnumChatFormatting.DARK_GRAY));
    line.appendSibling(chat(subCommand.getDescription(), EnumChatFormatting.GRAY));
    return line;
  }

  private String getHelpUsage(ZSSubCommand subCommand) {
    String usage = subCommand.getUsage();
    return usage.startsWith("/zsmod ") ? usage.substring("/zsmod ".length()) : usage;
  }

  private IChatComponent chat(String text, EnumChatFormatting color) {
    return chat(text, color, false);
  }

  private IChatComponent chat(String text, EnumChatFormatting color, boolean bold) {
    ChatComponentText component = new ChatComponentText(text);
    component.setChatStyle(new ChatStyle().setColor(color).setBold(Boolean.valueOf(bold)));
    return component;
  }

  private abstract class ZSSubCommand {

    private final String name;
    private final String usage;
    private final String description;
    private final int permissionLevel;

    private ZSSubCommand(String name, String usage, String description, int permissionLevel) {
      this.name = name;
      this.usage = usage;
      this.description = description;
      this.permissionLevel = permissionLevel;
    }

    protected String getName() {
      return name;
    }

    protected String getUsage() {
      return usage;
    }

    protected String getDescription() {
      return description;
    }

    private boolean canUse(ICommandSender sender) {
      return permissionLevel <= 0 || sender.canCommandSenderUseCommand(permissionLevel, getCommandName());
    }

    private boolean requiresOp() {
      return permissionLevel > 0;
    }

    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      return Collections.emptyList();
    }

    protected boolean isUsernameIndex(String[] args, int index) {
      return false;
    }

    protected abstract void process(ICommandSender sender, String[] args);
  }

  private class HelpSubCommand extends ZSSubCommand {

    private HelpSubCommand() {
      super("help", "/zsmod help", "Shows the ZeroSMod command menu.", 0);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length > 0) {
        throw new WrongUsageException(getUsage());
      }

      sendHelp(sender);
    }
  }

  private class ReloadSubCommand extends ZSSubCommand {

    private ReloadSubCommand() {
      super("reload", "/zsmod reload", "Reloads ZeroSMod configs and syncs clients.", 2);
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length > 0) {
        throw new WrongUsageException(getUsage());
      }

      PathConfig.reload();
      BiomeConfig.reload();
      DimensionConfig.reload();
      KiAttackConfig.reload();
      ServerWeaponConfig.reload();

      IMessage pkt = BiomeVisualSyncUtil.buildFullPacket();
      ZeroSMod.network.sendToAll(pkt);
      ZeroSMod.network.sendToAll(SyncDimensionConfigPacket.buildCurrent());

      WeaponTypesToClientPacket weaponTypesPacket = new WeaponTypesToClientPacket(ServerWeaponConfig.loadedWeaponStats);
      ZeroSMod.network.sendToAll(weaponTypesPacket);

      int players = MinecraftServer.getServer().getConfigurationManager().playerEntityList.size();
      sender.addChatMessage(new ChatComponentText(
          PREFIX + EnumChatFormatting.GRAY + "ZeroSMod configs reloaded and synced to " + players + " player(s)."));
    }
  }

  private class SetItemTypeCommand extends ZSSubCommand {

    private SetItemTypeCommand() {
      super("weapons", "/zsmod weapons [types, setItemType] [TYPE]", "Sets item type of currently held item.", 2);
    }

    @Override
    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      if (args.length == 1) {
        List<String> options = new ArrayList<>();
        options.add("types");
        options.add("setItemType");
        return options;
      } else if (args.length == 2) {
        return new ArrayList<>(ZSWeaponUtils.getLoadedStats().keySet());
      }

      return Collections.emptyList();
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length < 1 || args.length > 2) {
        throw new WrongUsageException(getUsage());
      }

      switch(args[0].toLowerCase()) {
        case "types":
          StringBuilder message = new StringBuilder(PREFIX + EnumChatFormatting.WHITE + "Loaded weapon types: ");
          Set<String> loadedStats = ZSWeaponUtils.getLoadedStats().keySet();
          for(String key : loadedStats) {
            message.append(key).append(", ");
          }
          sender.addChatMessage(new ChatComponentText(message.toString()));
          break;

        case "setitemtype":
          EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(sender.getCommandSenderName());
          if(player == null) {
            sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.DARK_RED + "PLAYER NOT FOUND"));
            return;
          }
          ItemStack heldItem = player.getHeldItem();
          if(heldItem == null) {
            sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + "Please hold the item you wish to change"));
            return;
          }

          try {
            ScriptZSWeapon weapon = ZeroSAPI.Instance().getZSWeapon(new ScriptItemStack(heldItem));
            weapon.setType(args[1]);
            ZeroSAPI.Instance().getPlayerCombatState((IPlayer) NpcAPI.Instance().getIEntity(player)).setCurrentZSWeapon(weapon, true);
            sender.addChatMessage(new ChatComponentText(
                    PREFIX + EnumChatFormatting.GRAY + "Item type successfully set to " + args[1]));
          } catch (CachedWeaponStats.UnknownWeaponTypeException e) {
            sender.addChatMessage(new ChatComponentText(PREFIX + EnumChatFormatting.RED + "Unknown weapon type: " + args[1]));
          }
          break;
      }
    }
  }

  private class SaiyanMergeSubCommand extends ZSSubCommand {

    private SaiyanMergeSubCommand() {
      super("saiyanmerge", "/zsmod saiyanmerge [player]", "Manually forces the Saiyan merge for a player if it does not work automatically.", 2);
    }

    @Override
    protected List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
      if (args.length == 1) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
      }

      return Collections.emptyList();
    }

    @Override
    protected boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
    }

    @Override
    protected void process(ICommandSender sender, String[] args) {
      if (args.length > 1) {
        throw new WrongUsageException(getUsage());
      }

      EntityPlayerMP player = args.length == 1
          ? getPlayer(sender, args[0])
          : getCommandSenderAsPlayer(sender);
      String result = SaiyanMasteryMergeEvent.forceMerge(player);
      sender.addChatMessage(new ChatComponentText(
          PREFIX + EnumChatFormatting.GRAY + "Saiyan merge for " + player.getCommandSenderName() + ": " + result));
    }
  }
}
