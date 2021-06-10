package com.oop.economy.config;

import com.oop.datamodule.h2.H2Credential;
import com.oop.economy.config.database.DatabaseCredentials;
import com.oop.economy.config.database.H2;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.annotations.Comment;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.property.CollectionProperty;
import com.oop.inteliframework.config.property.property.custom.ObjectProperty;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;

import java.util.LinkedList;

@Comment("Economy plugin made by OOP :>")
@Getter
public class Config implements Configurable, InteliModule {

  @Named("number-formatter-suffixes")
  private final CollectionProperty<String, LinkedList<String>> numberFormatterSuffixes =
      CollectionProperty.from(
          new LinkedList<>(),
          String.class,
          "",
          "k",
          "m",
          "b",
          "T",
          "Q",
          "Qt",
          "S",
          "ST",
          "O",
          "N",
          "D",
          "UD",
          "DD",
          "Z");

  @Named("starting-balance")
  @Comment("What starting balance new users will have?")
  private final ObjectProperty<NumberWrapper> startingBalance =
      ObjectProperty.from(NumberWrapper.of(10));

  @Named("database")
  private final ObjectProperty<DatabaseCredentials> database =
      ObjectProperty.from(
          new H2(
              new H2Credential()
                  .folder(platform().starter().dataDirectory().toAbsolutePath().toFile())
                  .database("data")));
}
