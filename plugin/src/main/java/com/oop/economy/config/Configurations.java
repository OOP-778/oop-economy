package com.oop.economy.config;

import com.oop.economy.command.style.config.CommandStyleConfig;
import com.oop.economy.command.style.config.CommandStyleMessage;
import com.oop.economy.config.database.H2;
import com.oop.economy.util.module.LoadableModule;
import com.oop.economy.util.number.NumberWrapper;
import com.oop.inteliframework.config.file.FileController;
import com.oop.inteliframework.config.file.prerequisite.Paths;
import com.oop.inteliframework.config.property.AssociatedConfig;
import com.oop.inteliframework.config.property.Configurable;
import lombok.Getter;

import java.nio.file.Path;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public class Configurations implements LoadableModule {

  @Getter private final FileController<?> fileController;
  private AssociatedConfig<Config> mainConfig;
  @Getter private AssociatedConfig<CommandStyleConfig> commandStyleConfig;

  public Configurations() {
    registerHandlers();

    Path dataDirectory = platform().starter().dataDirectory();
    this.fileController = new FileController<>(dataDirectory);

    fileController.setFilter(path -> path.toString().contains(".yml"));

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
    new CommandStyleMessage.Handler().register();
  }

  @Override
  public void load() {
    logger().info("Loading Configs...");
    fileController.load();

    this.mainConfig = loadAssociatedConfig(Config.class, "config.yml");
    this.commandStyleConfig = loadAssociatedConfig(CommandStyleConfig.class, "commandStyle.yml");

    logger().info("Loaded {} configuration files!", fileController.files().size());
  }

  protected <T extends Configurable> AssociatedConfig<T> loadAssociatedConfig(
      Class<T> clazz, String fileName) {
    AssociatedConfig<T> associatedConfig;
    try {
      associatedConfig = new AssociatedConfig<>(fileController.getOrCreate(fileName), clazz);
      associatedConfig.load();

      associatedConfig.syncAndSave();
    } catch (Throwable throwable) {
      throw new IllegalStateException(
          format("Failed to load configuration of class {} and in file name {}", clazz, fileName),
          throwable);
    }

    return associatedConfig;
  }

  public Config getMainConfig() {
    return mainConfig.getObject();
  }
}
