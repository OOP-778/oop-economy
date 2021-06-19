package com.oop.economy.command.style;

import com.oop.economy.command.style.config.CommandStyleConfig;
import com.oop.economy.config.Configurations;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.error.*;
import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import com.oop.inteliframework.command.style.DefaultStyle;
import com.oop.inteliframework.message.Replacer;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import static com.oop.economy.util.Helper.useMessage;

public class CommandConfigurableStyle extends DefaultStyle implements InteliModule {
  @Override
  public void handleError(
      @NotNull @NonNull CommandError[] errors,
      @NonNull CommandRegistry commandRegistry,
      @NonNull CommandParseHistory history) {
    final ExecutorWrapper executor = history.getExecutor();
    final CommandStyleConfig commandStyleConfig =
        platform().safeModuleByClass(Configurations.class).getCommandStyleConfig().getObject();

    CommandError error = errors[0];
    if (error instanceof WrongArgumentsError) {
      useMessage(
          commandStyleConfig.getWrongArgumentsError().get().getMessage(),
          (message, audiences) -> {
            Replacer replacer = new Replacer();
            replacer.replaceLiteral("%command_path%", buildUserFriendlyPath(history.getPath()));
            replacer.replaceLiteral("%argument%", history.getWaitingForParse().poll());

            message.replace(replacer);
            message.send(audiences.sender(executor.as(BukkitCommandExecutor.class).commandSender));
          });
      return;
    }

    if (error instanceof TooMuchArgumentsError) {
      useMessage(
          commandStyleConfig.getTooMuchArgumentsError().get().getMessage(),
          (message, audiences) -> {
            Replacer replacer = new Replacer();
            replacer.replaceLiteral("%command_path%", buildUserFriendlyPath(history.getPath()));
            replacer.replaceLiteral("%argument%", String.join(", ", history.getWaitingForParse()));

            message.replace(replacer);
            message.send(audiences.sender(executor.as(BukkitCommandExecutor.class).commandSender));
          });
      return;
    }

    if (error instanceof InvalidArgumentError) {
      useMessage(
          commandStyleConfig.getInvalidArgumentError().get().getMessage(),
          (message, audiences) -> {
            Replacer replacer = new Replacer();
            replacer.replaceLiteral("%command_path%", buildUserFriendlyPath(history.getPath()));
            replacer.replaceLiteral("%argument%", history.getWaitingForParse().poll());
            replacer.replaceLiteral(
                "%error_message%", ((InvalidArgumentError) error).getResult().getMessage());

            message.replace(replacer);
            message.send(audiences.sender(executor.as(BukkitCommandExecutor.class).commandSender));
          });
      return;
    }

    if (error instanceof MissingArgumentsError) {
      CommandElement lastElement = history.getLastElement();
      executor.sendMessage(
          "Missing arguments for command: {}, available arguments: {}",
          buildUserFriendlyPath(history.getPath()),
          buildAvailableArgs(history, history.getLastElement()));
      return;
    }

    sendHelpMessage(executor, history);
  }
}
