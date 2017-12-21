package com.rype3.pocket_hrm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class BackServices extends Service {
    @Nullable

    public int counter=0;
    public BackServices(Context applicationContext) {
        super();
        Log.e("HERE", "here I am!");
    }

    public BackServices() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, ConnectivityReceiver.class));
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
        sendBroadcast(broadcastIntent);
        stoptimertask();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
