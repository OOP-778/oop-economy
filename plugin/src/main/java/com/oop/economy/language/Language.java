package com.oop.economy.language;

import com.oop.inteliframework.message.InteliMessageModule;
import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.message.chat.InteliChatMessage;
import com.oop.inteliframework.message.chat.element.ChatLineElement;
import com.oop.inteliframework.plugin.InteliPlatform;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public enum Language {
  COMMAND_MODIFIER_GIVE_SENDER("&a&lECONOMY » &fYou've given &a%target% $%amount_formatted%"),
  COMMAND_MODIFIER_GIVE_RECEIVER(
      "&a&lECONOMY » &fYou've received &a$%amount_formatted% &ffrom &a%sender%"),
  COMMAND_MODIFIER_SET_SENDER("&a&lECONOMY » &fYou've set &a%target%'s &fbalance to &a%amount_formatted%$"),
  COMMAND_MODIFIER_SET_RECEIVER(
          "&a&lECONOMY » &fYour balance was set to &a$%amount_formatted% &ffrom &a%sender%"),
  COMMAND_BALANCE_YOUR("&a&lECONOMY » &fYou have &a$%money_formatted%"),
  COMMAND_BALANCE_OTHER("&a&lECONOMY » &a%player% &fhas &a$%money_formatted%")
  ;
  protected final String[] comments;
  protected InteliMessage<?> message;

  Language(String message, String... comments) {
    this.message = new InteliChatMessage(new ChatLineElement(message));
    this.comments = comments;
  }

  Language(InteliMessage<?> message, String... comments) {
    this.message = message;
    this.comments = comments;
  }

  public InteliMessage<?> get() {
    return message.clone();
  }

  public InteliMessage<?> use(Consumer<InteliMessage<?>> consumer) {
    InteliMessage<?> inteliMessage = get();
    consumer.accept(inteliMessage);

    return inteliMessage;
  }

  public InteliMessage<?> use(BiConsumer<InteliMessage<?>, BukkitAudiences> consumer) {
    InteliMessage<?> inteliMessage = get();
    consumer.accept(
        inteliMessage,
        (BukkitAudiences)
            InteliPlatform.getInstance().safeModuleByClass(InteliMessageModule.class).audiences());

    return inteliMessage;
  }
}
