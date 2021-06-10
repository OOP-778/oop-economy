package com.oop.economy.util;

import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.api.Task;
import com.oop.inteliframework.task.api.TaskController;
import com.oop.inteliframework.task.bukkit.BukkitTaskController;
import com.oop.inteliframework.task.bukkit.InteliBukkitTask;
import com.oop.inteliframework.task.type.InteliTask;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Schedulers {
  private static final Supplier<ASYNC_SCHEDULER> ASYNC =
      () ->
          InteliPlatform.getInstance()
              .safeModuleByClass(InteliTaskFactory.class)
              .controllerByClass(ASYNC_SCHEDULER.class)
              .get();

  private static final Supplier<BukkitTaskController> BUKKIT =
      () ->
          InteliPlatform.getInstance()
              .safeModuleByClass(InteliTaskFactory.class)
              .controllerByClass(BukkitTaskController.class)
              .get();

  private static final Supplier<DATABASE_SCHEDULER> DATABASE =
          () ->
                  InteliPlatform.getInstance()
                          .safeModuleByClass(InteliTaskFactory.class)
                          .controllerByClass(DATABASE_SCHEDULER.class)
                          .get();

  public static BukkitTaskRunner bukkit() {
    return new BukkitTaskRunner(BUKKIT.get());
  }

  public static TaskRunner<InteliTask, InteliTaskController> async() {
    return new TaskRunner<>(ASYNC.get());
  }

  public static TaskRunner<InteliTask, InteliTaskController> database() {
    return new TaskRunner<>(DATABASE.get());
  }

  @RequiredArgsConstructor
  public static class TaskRunner<V extends Task<?>, T extends TaskController<T, V>> {
    @Getter private final T controller;

    public void prepareAndRun(Consumer<V> preparator) {
      V v = controller.prepareTask(preparator);
      v.run();
    }

    public void run(Runnable runnable) {
      prepareAndRun(task -> task.body($ -> runnable.run()));
    }

    public void later(Runnable runnable, TimeUnit unit, long delay) {
      prepareAndRun(
          task -> {
            task.body($ -> runnable.run());
            task.delay(unit, delay);
          });
    }

    public void repeat(Runnable runnable, TimeUnit unit, long delay) {
      prepareAndRun(
          task -> {
            task.body($ -> runnable.run());
            task.delay(unit, delay);
            task.repeatable(true);
          });
    }
  }

  public static class BukkitTaskRunner extends TaskRunner<InteliBukkitTask, BukkitTaskController> {
    public BukkitTaskRunner(BukkitTaskController controller) {
      super(controller);
    }

    public void sync(Runnable runnable) {
      prepareAndRun(
          task -> {
            task.sync(true);
            task.body($ -> runnable.run());
          });
    }

    public void ensureMainThread(Runnable runnable) {
      if (Bukkit.isPrimaryThread()) {
        runnable.run();
        return;
      }

      sync(runnable);
    }
  }

  public static class ASYNC_SCHEDULER extends InteliTaskController {
    public ASYNC_SCHEDULER(int threadsCount) {
      super(threadsCount);
    }
  }

  public static class DATABASE_SCHEDULER extends InteliTaskController {
    public DATABASE_SCHEDULER(int threadsCount) {
      super(threadsCount);
    }
  }
}
