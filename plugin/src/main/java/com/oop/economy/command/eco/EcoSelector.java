package com.oop.economy.command.eco;

import com.oop.economy.command.Arguments;
import com.oop.inteliframework.command.element.command.Command;

public class EcoSelector {
  public static void makeOnto(Command command) {
    command.addChild(Arguments.offlineTargetsArg(ModifierCommand::register));
  }
}
