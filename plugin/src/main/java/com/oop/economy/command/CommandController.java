package com.oop.economy.command;

import com.oop.economy.command.eco.EcoCommand;
import com.oop.inteliframework.command.bukkit.BukkitCommandRegistry;

public class CommandController extends BukkitCommandRegistry {
    public CommandController() {
        register(new EcoCommand());
        register(new BalanceCommand());
    }
}
