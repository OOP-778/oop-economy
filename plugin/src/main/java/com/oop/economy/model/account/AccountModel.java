package com.oop.economy.model.account;

import com.eatthepath.uuid.FastUUID;
import com.oop.datamodule.api.SerializedData;
import com.oop.datamodule.universal.model.UniversalBodyModel;
import com.oop.economy.database.DatabaseController;
import com.oop.economy.util.number.NumberUtil;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class AccountModel implements UniversalBodyModel, InteliModule {
  private final ReentrantLock writeLock = new ReentrantLock();
  @Getter private UUID uuid;
  @Getter private NumberWrapper balance;

  protected AccountModel() {}

  public AccountModel(UUID uuid, NumberWrapper balance) {
    this.uuid = uuid;
    this.balance = balance;
  }

  @Override
  public String[] getStructure() {
    return new String[] {"uuid", "balance"};
  }

  @Override
  public String getIdentifierKey() {
    return "uuid";
  }

  @Override
  public String getKey() {
    return FastUUID.toString(uuid);
  }

  @Override
  public void serialize(SerializedData data) {
    data.write("uuid", getKey());
    data.write("balance", balance.formatWithSuffixes());
  }

  public void setBalance(NumberWrapper newBalance) {
    modifyBalance($ -> newBalance);
  }

  public AccountModel modifyBalance(Function<NumberWrapper, NumberWrapper> modifier) {
    try {
      writeLock.lock();
      this.balance = modifier.apply(balance);
      if (NumberWrapper.of(0).isLessThan(balance)) {
        this.balance = NumberWrapper.of(0);
      }
    } finally {
      writeLock.unlock();
    }

    return this;
  }

  @Override
  public void deserialize(SerializedData data) {
    this.uuid = FastUUID.parseUUID(data.applyAs("uuid"));
    this.balance = NumberWrapper.of(NumberUtil.formattedToBigDecimal(data.applyAs("balance")));
  }

  @Override
  public void save(boolean b, Runnable runnable) {
    platform()
        .safeModuleByClass(DatabaseController.class)
        .getStorage(EconomyAccounts.class)
        .save(this, b, runnable);
  }
}
