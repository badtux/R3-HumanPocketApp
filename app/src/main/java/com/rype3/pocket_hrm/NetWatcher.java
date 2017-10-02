package com.rype3.pocket_hrm;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class NetWatcher extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("TAG" , "Start");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
