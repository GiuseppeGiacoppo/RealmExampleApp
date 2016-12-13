package me.giacoppo.realmexampleapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Peppe on 07/12/2016.
 */

public class RealmExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("database.realm")
                .build();
        Realm.setDefaultConfiguration(config);

    }
}
