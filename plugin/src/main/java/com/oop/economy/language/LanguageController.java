package com.oop.economy.language;

import com.oop.economy.config.Configurations;
import com.oop.economy.util.module.LoadableModule;
import com.oop.economy.util.module.NameableModule;
import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import com.oop.inteliframework.message.api.InteliMessage;
import com.oop.inteliframework.message.config.YamlMessage;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageController implements NameableModule, LoadableModule {
  @Override
  public void load() {
    Configurations configurations = platform().safeModuleByClass(Configurations.class);
    File file = configurations.getFileController().getOrCreate("language.yml");

    PlainConfig nodes = new PlainConfig(file);
    nodes.load();

    final Map<String, Language> languageKeys = new HashMap<>();
    for (Language language : Language.values()) {
      languageKeys.put(
          StringUtils.replace(language.name().toLowerCase(Locale.ROOT), "_", "."), language);
    }

    // Remove not existent language keys
    for (String key : nodes.map(NodeIterator.HIERARCHY).keySet()) {
      Language language = languageKeys.get(key);
      if (language == null) {
        nodes.remove(key);
      }
    }

    // Load the language from config
    for (Map.Entry<String, Language> languageKey : languageKeys.entrySet()) {
      Node node = nodes.getOrDefault(languageKey.getKey(), null);
      if (node == null) {
        YamlMessage.save(languageKey.getValue().message, languageKey.getKey(), nodes);
        setComments(nodes, languageKey);
        continue;
      }

      InteliMessage load = YamlMessage.load(languageKey.getKey(), nodes);
      if (load != null) {
        setComments(nodes, languageKey);
        languageKey.getValue().message = load;
      }
    }

    nodes.handler().save(nodes, file);
  }

  private void setComments(PlainConfig nodes, Map.Entry<String, Language> languageKey) {
    Node node = nodes.get(languageKey.getKey());
    if (node != null && languageKey.getValue().comments.length != 0) {
      node.comments().clear();
      node.comments().addAll(Arrays.asList(languageKey.getValue().comments));
    }
  }

  @Override
  public String name() {
    return "Language";
  }
}
