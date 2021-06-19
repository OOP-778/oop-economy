package com.oop.economy.util;

import com.oop.inteliframework.message.InteliMessageModule;
import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.plugin.InteliPlatform;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class Helper {
  public static <T> List<T> listOf(T... values) {
    return Arrays.asList(values);
  }

  public static <T extends InteliMessage> T useMessage(
      T message, BiConsumer<T, BukkitAudiences> consumer) {
    consumer.accept(
        (T) message.clone(),
        (BukkitAudiences)
            InteliPlatform.getInstance().safeModuleByClass(InteliMessageModule.class).audiences());
    return message;
  }
}
