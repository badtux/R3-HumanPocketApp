package com.rype3.pocket_hrm;

import android.app.Application;

import io.realm.Realm;

public class MyApplication extends Application {

    private static MyApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
     //   Realm.init();

//        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
//        Realm.setDefaultConfiguration(configuration);
//        Realm.getInstance(configuration);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onTerminate() {
       // Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}
