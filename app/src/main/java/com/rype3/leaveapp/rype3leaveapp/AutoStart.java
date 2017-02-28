package com.rype3.leaveapp.rype3leaveapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,LeaveAppService.class);
        context.startService(intent1);
        Log.i("Autostart", "started");
    }
}
