package com.oop.economy.command.player;

import com.oop.economy.command.Arguments;
import com.oop.economy.language.Language;
import com.oop.economy.model.account.AccountModel;
import com.oop.economy.model.account.EconomyAccounts;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.plugin.module.InteliModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.oop.economy.util.Helper.listOf;

public class PayCommand extends Command implements InteliModule {
  public PayCommand() {
    labeled("pay");
    addChild(
        Arguments.offlineTargetsArg(listOf(Arguments.TargetsMatch.INDIVIDUAL, Arguments.TargetsMatch.ONLINE))
            .optional(false)
            .addChild(Arguments.numberWrapperArg().labeled("amount").optional(false)));

    onExecute(
        (executor, data) -> {
          final Arguments.TargetMatches targets = data.getAs("targets");
          final NumberWrapper amount = data.getAs("amount");
          final Player sender = (Player) executor.as(BukkitCommandExecutor.class).commandSender;
          final AccountModel senderAccount =
              platform().safeModuleByClass(EconomyAccounts.class).getOrCreate(sender.getUniqueId());

          for (OfflinePlayer target : targets.getMatches()) {
            if (!handlePay(senderAccount, (Player) target, amount)) break;
          }
        });
  }

  public boolean handlePay(AccountModel sender, Player target, NumberWrapper amount) {
    if (!sender.getBalance().isMoreOrEquals(amount)) {
      Language.COMMAND_PAY_NOT_ENOUGH_BALANCE.use(
          (message, audience) -> message.send(audience.player(sender.getOnlinePlayer())));
      return false;
    }

    final AccountModel receiverAccount =
        platform().safeModuleByClass(EconomyAccounts.class).getOrCreate(target.getUniqueId());

    sender.modifyBalance(current -> current.remove(amount));
    receiverAccount.modifyBalance(current -> current.add(amount));

    Replacer replacer = new Replacer();
    replacer.replaceLiteral("%target%", target.getName());
    replacer.replaceLiteral("%player%", sender.getOnlinePlayer().getName());
    replacer.replaceLiteral("%money_formatted%", amount.formatWithSuffixes());

    Language.COMMAND_PAY_YOU.use(
        (message, audience) -> {
          message.replace(replacer);
          message.send(audience.player(sender.getOnlinePlayer()));
        });

    Language.COMMAND_PAY_OTHER.use(
        (message, audience) -> {
          message.replace(replacer);
          message.send(audience.player(target));
        });

    return true;
  }
}
