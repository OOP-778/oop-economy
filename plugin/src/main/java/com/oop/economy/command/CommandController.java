package com.oop.economy.command;

import com.oop.economy.command.eco.EcoCommand;
import com.oop.economy.command.player.PayCommand;
import com.oop.economy.command.style.CommandConfigurableStyle;
import com.oop.inteliframework.command.bukkit.BukkitCommandRegistry;

public class CommandController extends BukkitCommandRegistry {
    public CommandController() {
        setStyle(new CommandConfigurableStyle());
        register(new EcoCommand());
        register(new BalanceCommand());
        register(new PayCommand());
    }
}
