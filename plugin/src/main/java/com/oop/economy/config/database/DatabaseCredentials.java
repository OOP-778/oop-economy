package com.oop.economy.config.database;

import com.oop.datamodule.h2.H2Credential;
import com.oop.datamodule.universal.UniversalStorage;
import com.oop.datamodule.universal.model.UniversalBodyModel;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;

@Getter
public abstract class DatabaseCredentials {
  public abstract boolean testConnection();

  public abstract <T extends UniversalBodyModel> void provideForStorage(
      UniversalStorage<T> storage);

  public abstract void close();

  public static class Handler implements PropertyHandler<DatabaseCredentials>, InteliModule {
    @Override
    public SerializedProperty toNode(DatabaseCredentials databaseCredentials) {
      if (databaseCredentials instanceof H2) {
        BaseParentNode databaseNode = new BaseParentNode();
        databaseNode.set("type", "h2");
        databaseNode.set("db-name", ((H2) databaseCredentials).getCredential().database());
        return new SerializedProperty(null, databaseNode);
      }

      throw new IllegalStateException("Unimplemented credential type");
    }

    @Override
    public DatabaseCredentials fromNode(Node node) {
      ParentNode databaseNode = node.asParent();
      String type = databaseNode.get("type").asValue().getAs(String.class);
      if (type.equalsIgnoreCase("h2")) {
        return new H2(
            new H2Credential()
                .folder(platform().starter().dataDirectory().toAbsolutePath().toFile())
                .database(databaseNode.get("db-name").asValue().getAs(String.class)));
      }

      throw new IllegalStateException("Unimplemented credential type");
    }

    @Override
    public Class<DatabaseCredentials> getObjectClass() {
      return DatabaseCredentials.class;
    }
  }
}
