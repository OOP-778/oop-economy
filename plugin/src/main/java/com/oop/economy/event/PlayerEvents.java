package com.oop.economy.event;

import com.oop.economy.model.account.EconomyAccounts;
import com.oop.inteliframework.event.Events;
import com.oop.inteliframework.plugin.module.InteliModule;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements InteliModule {

    public PlayerEvents() {
        Events.Simple.hook(PlayerJoinEvent.class, event -> {
          platform()
                  .safeModuleByClass(EconomyAccounts.class)
                  .getOrCreate(event.getPlayer().getUniqueId())
                  .setOnlinePlayer(event.getPlayer());
        });

        Events.Simple.hook(PlayerQuitEvent.class, event -> {
            platform()
                    .safeModuleByClass(EconomyAccounts.class)
                    .getOrCreate(event.getPlayer().getUniqueId())
                    .setOnlinePlayer(null);
        });
    }

}
