package com.oop.economy.command.eco;

import com.oop.economy.command.Arguments;
import com.oop.inteliframework.command.element.command.Command;

public class EcoCommand extends Command {
  public EcoCommand() {
    labeled("eco");
    addChild(Arguments.offlineTargetsArg(ModifierCommand::register));
  }
}
