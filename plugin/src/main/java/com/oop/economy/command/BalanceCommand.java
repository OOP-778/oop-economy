package com.oop.economy.command;

import com.oop.economy.OOPEconomyAPI;
import com.oop.economy.language.Language;
import com.oop.economy.model.account.AccountModel;
import com.oop.inteliframework.command.bukkit.BukkitArguments;
import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.plugin.module.InteliModule;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BalanceCommand extends Command implements InteliModule {
  public BalanceCommand() {
    labeled("balance");
    addAlias("bal");

    addChild(BukkitArguments.playerArgument().optional(true));
    onExecute(
        (executor, data) -> {
          Player player = (Player) executor.as(BukkitCommandExecutor.class).commandSender;
          Optional<Player> optionalTargetModel = data.getAsOptional("player", Player.class);
          AccountModel accountModel =
              optionalTargetModel
                  .map(target -> OOPEconomyAPI.getAccountModel(target.getUniqueId()))
                  .orElseGet(() -> OOPEconomyAPI.getAccountModel(player.getUniqueId()));

          Replacer replacer = new Replacer();
          replacer.replaceLiteral(
              "%target%", optionalTargetModel.map(HumanEntity::getName).orElse("None"));
          replacer.replaceLiteral(
              "%money_formatted%", accountModel.getBalance().formatWithSuffixes());

          Language language;

          // The target and executor is the same
          if (!optionalTargetModel.isPresent() || (optionalTargetModel.isPresent() && optionalTargetModel.get().equals(player))) {
            language = Language.COMMAND_BALANCE_YOUR;
          } else {
            language = Language.COMMAND_BALANCE_OTHER;
          }

          language.use(
              (message, audiences) -> {
                message.replace(replacer);
                message.send(audiences.player(player));
              });
        });
  }
}
