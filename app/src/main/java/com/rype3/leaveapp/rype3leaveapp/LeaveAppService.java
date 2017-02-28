package com.rype3.leaveapp.rype3leaveapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class LeaveAppService extends Service {

    @Nullable
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("LOG" ,"OnCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("LOG" ,"onDestroy");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
       // return Service.START_NOT_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
        Intent intents = new Intent(getBaseContext(),LanguageActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
    //    Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d("TAG", "onStart");
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
