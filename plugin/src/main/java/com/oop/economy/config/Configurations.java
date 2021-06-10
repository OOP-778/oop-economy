package com.oop.economy.config;

import com.oop.economy.config.database.H2;
import com.oop.economy.util.module.LoadableModule;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.config.file.FileController;
import com.oop.inteliframework.config.file.prerequisite.Paths;
import com.oop.inteliframework.config.property.AssociatedConfig;
import lombok.Getter;

import java.nio.file.Path;

public class Configurations implements LoadableModule {

  @Getter private final FileController<?> fileController;
  private AssociatedConfig<Config> mainConfig;

  public Configurations() {
    registerHandlers();

    Path dataDirectory = platform().starter().dataDirectory();
    this.fileController = new FileController<>(dataDirectory);

    fileController.prerequisites(
        fcp ->
            fcp.loadFromResources(
                resources -> {
                  resources.option(Paths.CopyOption.COPY_IF_NOT_EXIST);
                  resources.filter(
                      file ->
                          !file.contains("modules")
                              && file.contains("yml")
                              && !file.equalsIgnoreCase("plugin.yml"));
                }));
  }

  public void registerHandlers() {
    new H2.Handler().register();
    new NumberWrapper.Handler().register();
  }

  @Override
  public void load() {
    logger().info("Loading Configs...");
    fileController.load();
    try {
      mainConfig = new AssociatedConfig<>(fileController.getOrCreate("config.yml"), Config.class);
      mainConfig.load();

      mainConfig.syncAndSave();
    } catch (Throwable throwable) {
      throw new IllegalStateException("Failed to load main configuration file, details", throwable);
    }
    logger().info("Loaded {} configuration files!", fileController.files().size());
  }

  public Config getMainConfig() {
    return mainConfig.getObject();
  }
}
