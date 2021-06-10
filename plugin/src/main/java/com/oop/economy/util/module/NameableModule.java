package com.oop.economy.util.module;

import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.module.InteliModule;

import java.util.Optional;

public interface NameableModule extends InteliModule {
  static Optional<NameableModule> getModuleByName(String name) {
    return InteliPlatform.getInstance()
        .allModules(
            module ->
                module instanceof NameableModule
                    && ((NameableModule) module).name().contentEquals(name))
        .map(inteliModule -> (NameableModule) inteliModule)
        .findFirst();
  }

  String name();
}
