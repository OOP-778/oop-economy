package com.oop.economy;

import com.oop.datamodule.api.StorageInitializer;
import com.oop.datamodule.api.storage.Storage;
import com.oop.datamodule.h2.H2Dependencies;
import com.oop.datamodule.mysql.MySqlDependencies;
import com.oop.datamodule.universal.UniversalStorage;
import com.oop.economy.command.CommandController;
import com.oop.economy.config.Configurations;
import com.oop.economy.config.database.DatabaseCredentials;
import com.oop.economy.database.DatabaseController;
import com.oop.economy.database.DatabaseLibraryManager;
import com.oop.economy.event.PlayerEvents;
import com.oop.economy.language.LanguageController;
import com.oop.economy.model.account.EconomyAccounts;
import com.oop.economy.util.Schedulers;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.dependency.common.CommonLibraryManager;
import com.oop.inteliframework.dependency.logging.adapters.JDKLogAdapter;
import com.oop.inteliframework.event.InteliEventModule;
import com.oop.inteliframework.event.bukkit.BukkitEventSystem;
import com.oop.inteliframework.message.InteliMessageModule;
import com.oop.inteliframework.plugin.PlatformStarter;
import com.oop.inteliframework.task.InteliTaskFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URLClassLoader;
import java.nio.file.Path;

public class OOPEconomy extends JavaPlugin implements PlatformStarter<OOPEconomy> {

  @Override
  public Path dataDirectory() {
    return getDataFolder().toPath();
  }

  @Override
  public String name() {
    return "oop-economy";
  }

  @Override
  public void onEnable() {
    try {
      // Initialize the platform
      initPlatform();

      // Initialize configurations
      initConfigurations();

      // Initialize database stuff
      initDatabase();

      // Initialize events
      initEvents();

      registerModule(new CommandController());
    } catch (Throwable throwable) {
      Bukkit.getPluginManager().disablePlugin(this);
      new IllegalStateException("Error while loading plugin", throwable).printStackTrace();
    }
  }

  private void initEvents() {
    new PlayerEvents();
  }

  public void initDatabase() {
    try {
      // Initialize Storage Helper
      StorageInitializer.initialize(
              runnable -> Schedulers.database().run(runnable),
              runnable -> Schedulers.bukkit().ensureMainThread(runnable),
              new DatabaseLibraryManager(this),
              null,
              error -> logger().error(error.getMessage()),
              new H2Dependencies()
      );

      DatabaseController databaseController = new DatabaseController();
      registerModule(databaseController);

      registerModule(new EconomyAccounts(databaseController));

      // Test db connection
      DatabaseCredentials databaseCredentials =
          safeModuleByClass(Configurations.class).getMainConfig().getDatabase().get();

      if (!databaseCredentials.testConnection()) {
        throw new IllegalStateException(
            "Failed to establish connection to the database, are the options correct?");
      }

      for (Storage storage : databaseController.getStorageList()) {
        databaseCredentials.provideForStorage(((UniversalStorage) storage));
      }

    } catch (Throwable throwable) {
      throw new IllegalStateException("Failed to initialize database", throwable);
    }
  }

  @Override
  public void onDisable() {
    platform().onDisable();
    StorageInitializer.getInstance().onDisable();
  }

  protected void initConfigurations() {
    try {
      Configurations configurations = new Configurations();
      registerModule(configurations);

      configurations.load();
    } catch (Throwable throwable) {
      throw new IllegalStateException("Failed to initialize configurations!", throwable);
    }

    try {
      LanguageController languageController = new LanguageController();
      languageController.load();

      registerModule(languageController);
    } catch (Throwable throwable) {
      throw new IllegalStateException("Failed to initialize Language", throwable);
    }
  }

  protected void initPlatform() {
    // Start the platform
    startPlatform();

    // Register main modules
    registerModule(
        new InteliEventModule(),
        new InteliTaskFactory(),
        new InteliPropertyModule(),
        new CommonLibraryManager(
            new JDKLogAdapter(getLogger()),
            (URLClassLoader) getClassLoader(),
            dataDirectory().toFile()));

    safeModuleByClass(CommonLibraryManager.class).load();

    registerModule(new InteliMessageModule(BukkitAudiences.create(this)));
    safeModuleByClass(InteliEventModule.class).registerSystem(new BukkitEventSystem());
  }
}
