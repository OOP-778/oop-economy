package com.oop.economy.command;

import com.oop.economy.util.Players;
import com.oop.economy.util.number.NumberUtil;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface Arguments {
  static Argument<NumberWrapper> numberWrapperArg(Consumer<Argument<NumberWrapper>>... consumer) {
    final Argument<NumberWrapper> numberWrapperArgument = new Argument<>();
    numberWrapperArgument.labeled("number");
    numberWrapperArgument.parser(
        (in, $) -> {
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

  static Argument<TargetMatches> offlineTargetsArg(Consumer<Argument<TargetMatches>>... consumer) {
    return offlineTargetsArg(
        Arrays.asList(
            TargetsMatch.EVERYONE, TargetsMatch.ONLINE, TargetsMatch.OFFLINE, TargetsMatch.SELF),
        consumer);
  }

  static Argument<TargetMatches> offlineTargetsArg(
      List<TargetsMatch> whitelistedTargets, Consumer<Argument<TargetMatches>>... consumer) {
    final Argument<TargetMatches> offlineTargetsArgument = new Argument<>();
    offlineTargetsArgument.labeled("targets");

    // Filter by whitelisted targets
    final BiFunction<Collection<OfflinePlayer>, CommandSender, Collection<OfflinePlayer>> filter =
        ((players, executor) -> {
          players.removeIf(
              player -> {
                // If whitelisted targets doesn't contain SELF, we filter it out
                if (!whitelistedTargets.contains(TargetsMatch.SELF) && player.equals(executor))
                  return true;

                // If we have all the targets, then we just return false
                if (whitelistedTargets.size() == TargetsMatch.values().length) return false;

                // If it contains EVERYONE, we also just return false
                if (whitelistedTargets.contains(TargetsMatch.EVERYONE)) return false;

                // If it contains ONLINE && OFFLINE == EVERYONE, so we also return false
                if (whitelistedTargets.containsAll(
                    Arrays.asList(TargetsMatch.ONLINE, TargetsMatch.OFFLINE))) return false;

                // Then we just filter by whatever we have here
                boolean yesOrNo = true;

                boolean isOnline = player.isOnline();
                for (TargetsMatch whitelistedTarget : whitelistedTargets) {
                  if (whitelistedTarget == TargetsMatch.INDIVIDUAL) {
                    if (isOnline && !whitelistedTargets.contains(TargetsMatch.ONLINE)) {
                      yesOrNo = true;
                      break;
                    }

                    if (!isOnline && !whitelistedTargets.contains(TargetsMatch.OFFLINE)) {
                      yesOrNo = true;
                      break;
                    }

                    yesOrNo = false;
                    break;
                  }

                  if (whitelistedTarget == TargetsMatch.ONLINE && isOnline) {
                    yesOrNo = false;
                    break;
                  }

                  if (whitelistedTarget == TargetsMatch.OFFLINE && !isOnline) {
                    yesOrNo = false;
                    break;
                  }
                }

                return yesOrNo;
              });
          return players;
        });

    // Parser for the argument
    offlineTargetsArgument.parser(
        (in, history) -> {
          final String poll = in.poll();
          final TargetMatches match = new TargetMatches(new LinkedHashSet<>(), poll);
          final CommandSender sender =
              history.getExecutor().as(BukkitCommandExecutor.class).commandSender;

          final Collection<OfflinePlayer> possiblePlayers =
              filter.apply(Players.allBukkit(), sender);
          if (possiblePlayers.isEmpty()) {
            return new ParseResult<>("Not a valid target");
          }

          // Everyone selector
          if (whitelistedTargets.contains(TargetsMatch.EVERYONE) && poll.equalsIgnoreCase("*")) {
            match.getMatches().addAll(possiblePlayers);
            return new ParseResult<>(match);
          }

          // Online selector
          if (whitelistedTargets.contains(TargetsMatch.ONLINE) && poll.equalsIgnoreCase("online")) {
            match.getMatches().addAll(possiblePlayers);
            return new ParseResult<>(match);
          }

          // Offline selector
          if (whitelistedTargets.contains(TargetsMatch.OFFLINE)
              && poll.equalsIgnoreCase("offline")) {
            match.getMatches().addAll(possiblePlayers);
            return new ParseResult<>(match);
          }

          String[] split = StringUtils.split(poll, ",");
          for (String s : split) {
            possiblePlayers.stream()
                .filter(player -> player.getName().equalsIgnoreCase(s))
                .findFirst()
                .ifPresent(player -> match.getMatches().add(player));
          }

          if (whitelistedTargets.contains(TargetsMatch.INDIVIDUAL)
              && match.getMatches().size() > 1) {
            return new ParseResult<>("Required a single target, found many");
          }

          if (!whitelistedTargets.contains(TargetsMatch.SELF)
              && match.getMatches().contains(sender)) {
            return new ParseResult<>("The target cannot be you!");
          }

          if (match.getMatches().isEmpty()) {
            return new ParseResult<>("Not a valid target");
          }

          return new ParseResult<>(match);
        });

    // Tab completion for the argument
    offlineTargetsArgument.tabComplete(
        ($, parseHistory) -> {
          final List<String> completions = new ArrayList<>();
          if (!whitelistedTargets.contains(TargetsMatch.INDIVIDUAL)) {
            completions.addAll(
                whitelistedTargets.stream()
                    .map(TargetsMatch::getIdentifier)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
          }

          completions.addAll(
              filter
                  .apply(
                      Players.allBukkit(),
                      parseHistory.getExecutor().as(BukkitCommandExecutor.class).commandSender)
                  .stream()
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
  enum TargetsMatch {
    EVERYONE("*"),
    ONLINE("online"),
    OFFLINE("offline"),
    INDIVIDUAL(null),
    SELF(null);

    private final String identifier;
  }

  @AllArgsConstructor
  @Getter
  class TargetMatches {
    private final Collection<OfflinePlayer> matches;
    private final String input;
  }
}
