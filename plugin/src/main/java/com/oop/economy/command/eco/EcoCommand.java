package com.oop.economy.command.eco;

import com.oop.inteliframework.command.element.command.Command;

public class EcoCommand extends Command {
  public EcoCommand() {
    labeled("eco");
    EcoSelector.makeOnto(this);
  }
}
