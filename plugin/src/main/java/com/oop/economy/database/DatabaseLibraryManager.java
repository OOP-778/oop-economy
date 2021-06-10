package com.oop.economy.database;

import com.oop.datamodule.api.loader.LibraryManager;
import com.oop.datamodule.api.loader.classloader.URLClassLoaderHelper;
import com.oop.datamodule.api.loader.logging.adapters.JDKLogAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URLClassLoader;
import java.nio.file.Path;

public class DatabaseLibraryManager extends LibraryManager {

  private final URLClassLoaderHelper classLoader;

  public DatabaseLibraryManager(JavaPlugin plugin) {
    super(new JDKLogAdapter(plugin.getLogger()), plugin.getDataFolder().toPath());
    classLoader = new URLClassLoaderHelper((URLClassLoader) plugin.getClass().getClassLoader());
  }

  @Override
  protected void addToClasspath(Path path) {
    classLoader.addToClasspath(path);
  }
}
