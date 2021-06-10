package com.oop.economy.model.account;

import com.oop.datamodule.api.StorageRegistry;
import com.oop.datamodule.universal.UniversalStorage;
import com.oop.economy.config.Configurations;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class EconomyAccounts extends UniversalStorage<AccountModel> implements InteliModule {

  private final Map<UUID, AccountModel> accounts = new ConcurrentHashMap<>();

  public EconomyAccounts(@NonNull StorageRegistry storageRegistry) {
    super(storageRegistry);
    addVariant("accounts", AccountModel.class);
  }

  @Override
  protected void onAdd(AccountModel accountModel) {
    accounts.put(accountModel.getUuid(), accountModel);
  }

  @Override
  protected void onRemove(AccountModel accountModel) {
    accounts.remove(accountModel.getUuid());
  }

  @Override
  public Stream<AccountModel> stream() {
    return accounts.values().stream();
  }

  @NotNull
  @Override
  public Iterator<AccountModel> iterator() {
    return accounts.values().iterator();
  }

  private AccountModel makeDefaultAccount(UUID uuid) {
    final NumberWrapper startingBalance =
        platform()
            .safeModuleByClass(Configurations.class)
            .getMainConfig()
            .getStartingBalance()
            .get();

    return new AccountModel(uuid, startingBalance);
  }

  public AccountModel getOrCreate(UUID uuid) {
    return accounts.computeIfAbsent(uuid, this::makeDefaultAccount);
  }
}
