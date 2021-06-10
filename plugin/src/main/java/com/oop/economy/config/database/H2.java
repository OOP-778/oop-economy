package com.oop.economy.config.database;

import com.oop.datamodule.h2.H2Credential;
import com.oop.datamodule.h2.H2Database;
import com.oop.datamodule.universal.StorageProviders;
import com.oop.datamodule.universal.UniversalStorage;
import com.oop.datamodule.universal.model.UniversalBodyModel;
import lombok.Getter;

@Getter
public class H2 extends DatabaseCredentials {
    private final H2Credential credential;
    private H2Database database;

    public H2(H2Credential credential) {
        this.credential = credential;
    }

    @Override
    public boolean testConnection() {
        return credential.test();
    }

    @Override
    public <T extends UniversalBodyModel> void provideForStorage(UniversalStorage<T> storage) {
        if (database == null) {
            database = credential.build();
        }

        storage.currentImplementation(StorageProviders.H2.provide(storage.getLinker(), database));
    }

    @Override
    public void close() {
        database.shutdown();
    }
}
