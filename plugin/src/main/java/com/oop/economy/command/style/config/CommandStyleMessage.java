package com.oop.economy.command.style.config;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.message.chat.InteliChatMessage;
import com.oop.inteliframework.message.config.YamlMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class CommandStyleMessage {

  private final InteliChatMessage message;
  private final Map<String, InteliChatMessage> templates;

  public static CommandStyleMessage of(
      InteliChatMessage message, InteliPair<String, InteliChatMessage>... templatePairs) {
    final Map<String, InteliChatMessage> templates = new HashMap<>();
    for (InteliPair<String, InteliChatMessage> templatePair : templatePairs) {
      templates.put(templatePair.getKey(), templatePair.getValue());
    }

    return new CommandStyleMessage(message, templates);
  }

  public static class Handler implements PropertyHandler<CommandStyleMessage> {
    @Override
    public SerializedProperty toNode(CommandStyleMessage commandStyleMessage) {
      // If no templates, we can put it as in a base node value
      if (commandStyleMessage.templates.isEmpty()) {
        return new SerializedProperty(null, messageToNode(commandStyleMessage.message));
      }

      final BaseParentNode parentNode = new BaseParentNode();
      final BaseParentNode templatesNode = new BaseParentNode();
      parentNode.assignNode("templates", templatesNode);

      for (Map.Entry<String, InteliChatMessage> template :
          commandStyleMessage.templates.entrySet()) {
        templatesNode.assignNode(template.getKey(), messageToNode(template.getValue()));
      }

      parentNode.assignNode("message", messageToNode(commandStyleMessage.message));
      return new SerializedProperty(null, parentNode);
    }

    protected Node messageToNode(InteliChatMessage message) {
      BaseParentNode parentNode = new BaseParentNode();
      YamlMessage.save(message, "temp", parentNode);

      return parentNode.get("temp");
    }

    @Override
    public CommandStyleMessage fromNode(Node node) {
      if (!node.isParent()) {
        return new CommandStyleMessage(YamlMessage.Chat.load(node.asValue()), new HashMap<>());
      }

      ParentNode parentNode = node.asParent();
      if (!parentNode.isPresent("templates")) {
        return new CommandStyleMessage(YamlMessage.Chat.load(parentNode), new HashMap<>());
      }

      InteliChatMessage message = YamlMessage.Chat.load(parentNode.get("message"));
      ParentNode templatesNode = (ParentNode) parentNode.get("templates");

      Map<String, InteliChatMessage> templates = new HashMap<>();

      for (Map.Entry<String, Node> template :
          templatesNode.map(NodeIterator.HIERARCHY).entrySet()) {
        templates.put(template.getKey(), YamlMessage.Chat.load(template.getValue()));
      }

      return new CommandStyleMessage(message, templates);
    }

    @Override
    public Class<CommandStyleMessage> getObjectClass() {
      return CommandStyleMessage.class;
    }
  }
}
