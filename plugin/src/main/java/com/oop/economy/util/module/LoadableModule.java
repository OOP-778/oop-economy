package com.oop.economy.util.module;

import com.oop.inteliframework.plugin.module.InteliModule;

public interface LoadableModule extends InteliModule {
  void load();

  default void callLoad() {

  }
}
