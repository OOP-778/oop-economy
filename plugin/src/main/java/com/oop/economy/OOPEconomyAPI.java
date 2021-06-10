package com.oop.economy;

import com.eatthepath.uuid.FastUUID;
import com.oop.economy.database.DatabaseController;
import com.oop.economy.model.account.AccountModel;
import com.oop.economy.model.account.EconomyAccounts;
import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.regex.Pattern;

@UtilityClass
public class OOPEconomyAPI {
  private static final Pattern uuidPattern =
      Pattern.compile(
          "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

  private static OOPEconomy plugin() {
    return (OOPEconomy) InteliPlatform.getInstance().starter();
  }

  private static EconomyAccounts accounts() {
    return plugin().safeModuleByClass(DatabaseController.class).getStorage(EconomyAccounts.class);
  }

  /** Get account model for an specific player */
  public static AccountModel getAccountModel(@NonNull UUID uuid) {
    return accounts().getOrCreate(uuid);
  }

  /** Get account model for unknown type of value it can be either name or uuid */
  public static AccountModel getAccountModel(@NonNull String unknown) {
    // We've got an UUID
    if (uuidPattern.matcher(unknown).find()) {
      return getAccountModel(FastUUID.parseUUID(unknown));
    }

    OfflinePlayer player = Bukkit.getOfflinePlayer(unknown);
    Preconditions.checkArgument(player != null, "Failed to find a player by name: {}", unknown);

    return getAccountModel(player.getUniqueId());
  }
}
