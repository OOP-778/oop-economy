package com.oop.economy.command;

import com.oop.economy.util.Players;
import com.oop.economy.util.number.NumberUtil;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface Arguments {
  static Argument<NumberWrapper> numberWrapperArg(Consumer<Argument<NumberWrapper>>... consumer) {
    final Argument<NumberWrapper> numberWrapperArgument = new Argument<>();
    numberWrapperArgument.labeled("number");
    numberWrapperArgument.parser(
        in -> {
          try {
            return new ParseResult<>(NumberWrapper.of(NumberUtil.formattedToBigDecimal(in.poll())));
          } catch (Throwable throwable) {
            return new ParseResult<>(throwable.getMessage());
          }
        });

    numberWrapperArgument.tabComplete(($, $1) -> Arrays.asList("1", "100", "1000", "1000000"));

    if (consumer.length > 0) {
      consumer[0].accept(numberWrapperArgument);
    }

    return numberWrapperArgument;
  }

  static Argument<OfflineTargetsMatch> offlineTargetsArg(
      Consumer<Argument<OfflineTargetsMatch>>... consumer) {
    final Argument<OfflineTargetsMatch> offlineTargetsArgument = new Argument<>();
    offlineTargetsArgument.labeled("targets");

    offlineTargetsArgument.parser(
        in -> {
          String poll = in.poll();
          OfflineTargetsMatch match = new OfflineTargetsMatch(new LinkedList<>(), poll);

          // Everyone selector
          if (poll.equalsIgnoreCase("*")) {
            match.getMatches().addAll(Players.allBukkit());
            return new ParseResult<>(match);
          }

          // Online selector
          if (poll.equalsIgnoreCase("online")) {
              match.getMatches().addAll(Players.bukkitOnlinePlayers());
            return new ParseResult<>(match);
          }

          // Offline selector
          if (poll.equalsIgnoreCase("offline")) {
              match.getMatches().addAll(
                Players.allBukkit().stream()
                    .filter(player -> !player.isOnline())
                    .collect(Collectors.toList()));
            return new ParseResult<>(match);
          }

          String[] split = StringUtils.split(poll, ",");
          for (String s : split) {
            match.getMatches().add(Bukkit.getOfflinePlayer(s));
          }

          return new ParseResult<>(poll);
        });

    offlineTargetsArgument.tabComplete(
        ($, $1) -> {
          final List<String> completions =
              new LinkedList<>(Arrays.asList("*", "online", "offline"));
          completions.addAll(
              Players.allBukkit().stream()
                  .map(OfflinePlayer::getName)
                  .collect(Collectors.toList()));
          return completions;
        });

    if (consumer.length > 0) {
      consumer[0].accept(offlineTargetsArgument);
    }

    return offlineTargetsArgument;
  }

  @AllArgsConstructor
  @Getter
  public static class OfflineTargetsMatch {
    private List<OfflinePlayer> matches;
    private String identifier;
  }
}
