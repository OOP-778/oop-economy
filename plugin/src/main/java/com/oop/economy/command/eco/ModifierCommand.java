package com.oop.economy.command.eco;

import com.oop.economy.command.Arguments;
import com.oop.economy.database.DatabaseController;
import com.oop.economy.language.Language;
import com.oop.economy.model.account.AccountModel;
import com.oop.economy.model.account.EconomyAccounts;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.command.api.ParentableElement;
import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.bukkit.requirement.BukkitRequirements;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.plugin.InteliPlatform;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class ModifierCommand {
  public static void register(ParentableElement<?> element) {
    element.addChild(
        createModifierCommand(
            Modifier.ADD,
            "oopeconomy.command.give",
            "give",
            Language.COMMAND_MODIFIER_GIVE_SENDER,
            Language.COMMAND_MODIFIER_GIVE_RECEIVER));

    element.addChild(
        createModifierCommand(
            Modifier.SET,
            "oopeconomy.command.set",
            "set",
            Language.COMMAND_MODIFIER_SET_SENDER,
            Language.COMMAND_MODIFIER_SET_RECEIVER));
  }

  private static Command createModifierCommand(
      Modifier modifier, String permission, String label, Language sender, Language receiver) {
    Command command = new Command();
    command.labeled(label);

    command.addChild(
        Arguments.numberWrapperArg().addChild(new NoValueArgument().labeled("--silent")));

    command.filters().add("permission", BukkitRequirements.permission(permission));
    command.onExecute(
        (executor, data) -> {
          Arguments.OfflineTargetsMatch targetsMatch =
              data.getAs("targets", Arguments.OfflineTargetsMatch.class);
          NumberWrapper number = data.getAs("number", NumberWrapper.class);

          Replacer replacer = new Replacer();
          replacer.replaceLiteral("%target%", targetsMatch.getIdentifier());
          replacer.replaceLiteral("%amount_formatted%", number.formatWithSuffixes());

          if (!executor.isConsole()) {
            sender.use(
                (message, audiences) -> {
                  message.replace(replacer);
                  message.send(
                      audiences.sender(executor.as(BukkitCommandExecutor.class).commandSender));
                });
          }

          for (OfflinePlayer target : targetsMatch.getMatches()) {
            doOnTarget(
                target,
                executor.as(BukkitCommandExecutor.class).commandSender,
                modifier,
                number,
                receiver,
                data.hasKey("--silent"));
          }
        });

    return command;
  }

  private static void doOnTarget(
      OfflinePlayer target,
      CommandSender executor,
      Modifier modifier,
      NumberWrapper number,
      Language receiver,
      boolean silent) {
    AccountModel accountModel =
        InteliPlatform.getInstance()
            .safeModuleByClass(DatabaseController.class)
            .getStorage(EconomyAccounts.class)
            .getOrCreate(target.getUniqueId());

    switch (modifier) {
      case ADD:
        accountModel.modifyBalance(balance -> balance.add(number.toNumber()));
        break;

      case SET:
        accountModel.modifyBalance($ -> number);
        break;

      case REMOVE:
        accountModel.modifyBalance(balance -> balance.remove(number.toNumber()));
        break;
    }

    Replacer replacer = new Replacer();
    replacer.replaceLiteral("%target%", target.getName());
    replacer.replaceLiteral("%sender%", executor.getName());
    replacer.replaceLiteral("%amount_formatted%", number.formatWithSuffixes());

    if (target.isOnline() && !silent) {
      receiver.use(
          (message, audiences) -> {
            message.replace(replacer);
            message.send(audiences.player(target.getPlayer()));
          });
    }
  }

  private static enum Modifier {
    ADD,
    SET,
    REMOVE,
  }
}
