package com.marceme.hpifitness;

import android.app.Application;

import com.marceme.hpifitness.util.PrefManager;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Marcel on 9/12/2016.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).schemaVersion(0)
                .deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
        PrefManager.initSharedPref(this);
    }
}
