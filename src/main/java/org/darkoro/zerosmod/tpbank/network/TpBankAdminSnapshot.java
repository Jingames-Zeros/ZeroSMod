package org.darkoro.zerosmod.tpbank.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import org.darkoro.zerosmod.ZeroSMod;

/** Everything the admin screen shows. Sent whole after each action. */
public final class TpBankAdminSnapshot implements IMessage {
  public long session;
  public int requestId;
  public boolean open, close, error, configSaved;
  public String message = "";
  public long deposited, withdrawn, eatenByDepositLoss, eatenByWithdrawFee, eatenByInterest, gainedFromInterest;
  /** Totals over every slot; {@link #rows} may be cut short on large servers. */
  public long totalBanked;
  public int slotCount;
  public Settings settings = new Settings();
  public final List<Row> rows = new ArrayList<>();

  public static final class Settings {
    public boolean enabled, unlockedByDefault, notifyOnDeposit;
    public long maxBalance;
    public double depositLoss, withdrawFee, interest;
    public int periodMinutes;

    void read(ByteBuf buf) {
      enabled = buf.readBoolean();
      unlockedByDefault = buf.readBoolean();
      notifyOnDeposit = buf.readBoolean();
      maxBalance = buf.readLong();
      depositLoss = buf.readDouble();
      withdrawFee = buf.readDouble();
      interest = buf.readDouble();
      periodMinutes = buf.readInt();
    }

    void write(ByteBuf buf) {
      buf.writeBoolean(enabled);
      buf.writeBoolean(unlockedByDefault);
      buf.writeBoolean(notifyOnDeposit);
      buf.writeLong(maxBalance);
      buf.writeDouble(depositLoss);
      buf.writeDouble(withdrawFee);
      buf.writeDouble(interest);
      buf.writeInt(periodMinutes);
    }
  }

  public static final class Row {
    public String name = "", slot = "";
    public long banked, deposited, withdrawn;
    public boolean unlocked;
    /** The player is online on this very slot, so the balance is exact and can be edited. */
    public boolean live;
  }

  @Override public void fromBytes(ByteBuf buf) {
    session = buf.readLong();
    requestId = buf.readInt();
    open = buf.readBoolean();
    close = buf.readBoolean();
    error = buf.readBoolean();
    configSaved = buf.readBoolean();
    message = ByteBufUtils.readUTF8String(buf);
    deposited = buf.readLong();
    withdrawn = buf.readLong();
    eatenByDepositLoss = buf.readLong();
    eatenByWithdrawFee = buf.readLong();
    eatenByInterest = buf.readLong();
    gainedFromInterest = buf.readLong();
    totalBanked = buf.readLong();
    slotCount = buf.readInt();
    settings.read(buf);
    int count = buf.readInt();
    for (int i = 0; i < count; i++) {
      Row row = new Row();
      row.name = ByteBufUtils.readUTF8String(buf);
      row.slot = ByteBufUtils.readUTF8String(buf);
      row.banked = buf.readLong();
      row.deposited = buf.readLong();
      row.withdrawn = buf.readLong();
      row.unlocked = buf.readBoolean();
      row.live = buf.readBoolean();
      rows.add(row);
    }
  }

  @Override public void toBytes(ByteBuf buf) {
    buf.writeLong(session);
    buf.writeInt(requestId);
    buf.writeBoolean(open);
    buf.writeBoolean(close);
    buf.writeBoolean(error);
    buf.writeBoolean(configSaved);
    ByteBufUtils.writeUTF8String(buf, message);
    buf.writeLong(deposited);
    buf.writeLong(withdrawn);
    buf.writeLong(eatenByDepositLoss);
    buf.writeLong(eatenByWithdrawFee);
    buf.writeLong(eatenByInterest);
    buf.writeLong(gainedFromInterest);
    buf.writeLong(totalBanked);
    buf.writeInt(slotCount);
    settings.write(buf);
    buf.writeInt(rows.size());
    for (Row row : rows) {
      ByteBufUtils.writeUTF8String(buf, row.name);
      ByteBufUtils.writeUTF8String(buf, row.slot);
      buf.writeLong(row.banked);
      buf.writeLong(row.deposited);
      buf.writeLong(row.withdrawn);
      buf.writeBoolean(row.unlocked);
      buf.writeBoolean(row.live);
    }
  }

  public static final class Handler implements IMessageHandler<TpBankAdminSnapshot, IMessage> {
    @Override public IMessage onMessage(TpBankAdminSnapshot message, MessageContext context) {
      ZeroSMod.proxy.receiveTpBankAdminSnapshot(message, context.netHandler);
      return null;
    }
  }
}
