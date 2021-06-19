package com.oop.economy.command.style.config;

import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.annotations.Comment;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.property.custom.ObjectProperty;
import com.oop.inteliframework.message.chat.InteliChatMessage;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Comment("This config is made to configure the style of commands")
@Getter
public class CommandStyleConfig implements Configurable {

  @Named("wrong-arguments-error")
  private final ObjectProperty<CommandStyleMessage> wrongArgumentsError =
      ObjectProperty.from(
          CommandStyleMessage.of(
              new InteliChatMessage(
                  "&cInvalid Arguments for command: %command_path%. Unknown argument for %argument%")));

  @Named("too-arguments-error")
  private final ObjectProperty<CommandStyleMessage> tooMuchArgumentsError =
      ObjectProperty.from(
          CommandStyleMessage.of(
              new InteliChatMessage(
                  "&cToo much arguments for command: %command_path%. Unknown arguments %argument%")));

  @Named("invalid-argument-error")
  private final ObjectProperty<CommandStyleMessage> invalidArgumentError =
      ObjectProperty.from(
          CommandStyleMessage.of(
              new InteliChatMessage(
                  "&cError while parsing argument for command: %command_path% for argument `%argument%` message: %error_message%")));

  @Named("wrong-usage-single")
  private final ObjectProperty<CommandStyleMessage> wrongUsageSingle =
      ObjectProperty.from(
          new CommandStyleMessage(
              new InteliChatMessage(
                  "&cWrong command usage of command %command_path%. Command usage: /%command_usage%"),
              makeDefaultArgTemplates()));


  private static Map<String, InteliChatMessage> makeDefaultArgTemplates() {
    Map<String, InteliChatMessage> templates = new HashMap<>();
    templates.put("optional-argument", new InteliChatMessage("&8[&a%label%&8]"));
    templates.put("required-argument", new InteliChatMessage("&8<&a%label%&8>"));
    return templates;
  }

  private static Map<String, InteliChatMessage> makeDefaultListedTemplates() {
    Map<String, InteliChatMessage> templates = makeDefaultArgTemplates();
    return templates;
  }
}
