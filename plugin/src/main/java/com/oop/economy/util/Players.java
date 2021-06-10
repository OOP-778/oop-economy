package com.oop.economy.util;

import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.InteliCache;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Players {
  private static final InteliCache<UUID, WrappedPlayer> playersCache =
      InteliCache.builder()
          .concurrencyLevel(1)
          .resetExpireAfterAccess(true)
          .expireAfter(10, TimeUnit.MINUTES)
          .build();

  private static final OfflineCache offlineCache = new OfflineCache(TimeUnit.SECONDS.toMillis(10));
  private static final OnlineCache onlineCache = new OnlineCache(TimeUnit.SECONDS.toMillis(10));

  private static final InteliCache<String, Collection<Player>> worldPlayers =
      InteliCache.builder()
          .concurrencyLevel(1)
          .resetExpireAfterAccess(false)
          .expireAfter(3, TimeUnit.SECONDS)
          .build();

  public static Collection<OfflinePlayer> allBukkit() {
    return offlineCache.get();
  }

  public static Collection<Player> bukkitWorldPlayers(World world) {
    Collection<Player> players =
        worldPlayers.getIfAbsent(world.getName(), () -> new HashSet<>(world.getPlayers()));
    players.removeIf(
        player -> player.getLocation().getWorld().getName().contentEquals(world.getName()));

    return players;
  }

  public static Set<WrappedPlayer> wrap() {
    return offlineCache.get().stream().map(WrappedPlayer::new).collect(Collectors.toSet());
  }

  public static WrappedPlayer wrap(UUID uuid) {
    return playersCache.getIfAbsent(uuid, () -> new WrappedPlayer(uuid));
  }

  public static WrappedPlayer wrap(OfflinePlayer player) {
    return playersCache.getIfAbsent(player.getUniqueId(), () -> new WrappedPlayer(player));
  }

  public static Collection<? extends OfflinePlayer> bukkitOnlinePlayers() {
    return Bukkit.getOnlinePlayers();
  }

  @Getter
  public static class WrappedPlayer {
    private final UUID uuid;
    private final OfflinePlayer player;
    private Player onlinePlayer;

    public WrappedPlayer(@NonNull OfflinePlayer player) {
      this.uuid = player.getUniqueId();

      if (player instanceof Player) {
        this.onlinePlayer = (Player) player;
      } else if (player.isOnline()) this.onlinePlayer = player.getPlayer();
      this.player = player;
    }

    public WrappedPlayer(@NonNull UUID uuid) {
      this(Bukkit.getOfflinePlayer(uuid));
    }

    public boolean isOnline() {
      return onlinePlayer != null;
    }

    public WrappedPlayer ifOnline(Consumer<Player> ifOnline) {
      if (isOnline()) {
        ifOnline.accept(onlinePlayer);
      }
      return this;
    }

    public WrappedPlayer ifOffline(Consumer<OfflinePlayer> ifOffline) {
      if (!isOnline()) {
        ifOffline.accept(player);
      }

      return this;
    }
  }

  private abstract static class Cache<T> {
    private final long invalidateAt;
    private long expireAt;

    public Cache(long invalidateAt) {
      this.invalidateAt = invalidateAt;
    }

    protected void reset() {
      this.expireAt = System.currentTimeMillis() + invalidateAt;
      load();
    }

    public boolean isExpired() {
      return System.currentTimeMillis() >= expireAt;
    }

    public abstract void load();

    public Collection<T> get() {
      if (isExpired()) {
        reset();
      }

      return new HashSet<>(_get());
    }

    protected abstract Collection<T> _get();
  }

  public static class OfflineCache extends Cache<OfflinePlayer> {
    private final Set<OfflinePlayer> offlinePlayers = Sets.newConcurrentHashSet();

    public OfflineCache(long invalidateAt) {
      super(invalidateAt);
      reset();
    }

    @Override
    public void load() {
      offlinePlayers.clear();
      offlinePlayers.addAll(Arrays.asList(Bukkit.getOfflinePlayers()));
    }

    @Override
    protected Collection<OfflinePlayer> _get() {
      return offlinePlayers;
    }
  }

  public static class OnlineCache extends Cache<Player> {
    private final Set<Player> onlinePlayers = Sets.newConcurrentHashSet();

    public OnlineCache(long invalidateAt) {
      super(invalidateAt);
    }

    @Override
    public void load() {
      onlinePlayers.clear();
      onlinePlayers.addAll(Bukkit.getOnlinePlayers());
    }

    @Override
    protected Collection<Player> _get() {
      onlinePlayers.removeIf(player -> !player.isOnline());
      return onlinePlayers;
    }
  }
}
