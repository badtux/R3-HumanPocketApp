package com.rype3.pocket_hrm;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.multidex.MultiDex;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    private static MyApplication mInstance;


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        Realm.init(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        final RealmConfiguration configuration = new RealmConfiguration.Builder().name("sample.realm").schemaVersion(1).build();
        Realm.setDefaultConfiguration(configuration);
        Realm.getInstance(configuration);

        SystemClock.sleep(TimeUnit.SECONDS.toMillis(3));
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}
