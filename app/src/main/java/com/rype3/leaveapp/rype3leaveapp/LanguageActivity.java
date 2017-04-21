package com.rype3.leaveapp.rype3leaveapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class LanguageActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,SensorEventListener {

    private Intent intent = null;
    private Button sinhala,english,tamil,btn_next;
    private  MediaPlayer mediaPlayer_1 = null,mediaPlayer = null,mediaPlayer_2 = null;
    private Snackbar snackbar = null;
    private CoordinatorLayout coordinatorLayout;
    Utils utils;
    Context context;

    // To keep track of activity's window focus
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    private int activityAction = 0;

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private static final int SENSOR_SENSITIVITY = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        widget();

        context = this.getApplication();
        utils =  new Utils(context);
        utils.setSharedPreference(context,null,Constants.LANGUAGE_TYPE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Settings.System.putInt(LanguageActivity.this.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 20);

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness =0.1f;// 100 / 100.0f;
                getWindow().setAttributes(lp);
            }

        }, 10000);

        exitActivity();
    }

    public void widget(){
        sinhala = (Button) findViewById(R.id.btn_sinhala);
        sinhala.setText(getString(R.string.sinhala));
        sinhala.setOnClickListener(onClick);

        english = (Button) findViewById(R.id.btn_english);
        english.setText(getString(R.string.english));
        english.setOnClickListener(onClick);

        tamil = (Button) findViewById(R.id.btn_tamil);
        tamil.setText(getString(R.string.tamil));
        tamil.setOnClickListener(onClick);

        btn_next = (Button) findViewById(R.id.button_next);
        btn_next.setOnClickListener(onClick);

//        btn_exit = (Button) findViewById(R.id.button_exit);
//        btn_exit.setOnClickListener(onClick);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click_2);
        mediaPlayer_2 = MediaPlayer.create(this, R.raw.error);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
    }

    public View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (exitActivity()){

                if (view == sinhala){
                    if (playSound(0)){
                        sinhala.setTextColor(Color.parseColor("#76FF03"));
                        tamil.setTextColor(Color.parseColor("#FFFFFF"));
                        english.setTextColor(Color.parseColor("#FFFFFF"));
                        utils.setSharedPreference(context,"s",Constants.LANGUAGE_TYPE);
                        btn_next.setText(getString(R.string.s_next));
                    }
                }

                if (view == english){
                    if (playSound(0)){
                        sinhala.setTextColor(Color.parseColor("#FFFFFF"));
                        tamil.setTextColor(Color.parseColor("#FFFFFF"));
                        english.setTextColor(Color.parseColor("#76FF03"));
                        utils.setSharedPreference(context,"e",Constants.LANGUAGE_TYPE);
                        btn_next.setText(getString(R.string.e_next));
                    }
                }

                if (view == tamil){
                    if (playSound(0)){
                        sinhala.setTextColor(Color.parseColor("#FFFFFF"));
                        tamil.setTextColor(Color.parseColor("#76FF03"));
                        english.setTextColor(Color.parseColor("#FFFFFF"));
                        utils.setSharedPreference(context,"t",Constants.LANGUAGE_TYPE);
                        btn_next.setText(getString(R.string.t_next));

                    }
                }

                if (view == btn_next){
                    if (utils.getBoolean(context,Constants.LANGUAGE_TYPE)){
                        if (playSound(1)) {
                        //    btn_next.setTextColor(Color.parseColor("#76FF03"));
                            activityAction =1;
                            intent = new Intent(LanguageActivity.this, EPFnumberActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        if (playSound(2)){
                            String text = getString(R.string.s_language) +" / " + getString(R.string.e_language) + " / " + getString(R.string.t_language) ;
                            ViewMessage(text,0);
                        }
                    }
                }
            }
        }
    };

    public boolean playSound(int position){
        try {
            switch (position){
                case 0:
                    if (mediaPlayer_2.isPlaying()){
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer = MediaPlayer.create(LanguageActivity.this, R.raw.click);
                    } mediaPlayer.start();
                    break;

                case 1:
                    if (mediaPlayer_2.isPlaying()){
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                    }

                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.reset();
                        mediaPlayer_1 = MediaPlayer.create(LanguageActivity.this, R.raw.click_2);
                    } mediaPlayer_1.start();
                    break;

                case 2:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.reset();
                    }

                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }

                    if (mediaPlayer_2.isPlaying()) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                        mediaPlayer_2 = MediaPlayer.create(LanguageActivity.this, R.raw.error);
                    } mediaPlayer_2.start();
                    break;
            }

            Settings.System.putInt(this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, 20);

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness =1f;// 100 / 100.0f;
            getWindow().setAttributes(lp);

        } catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            return true;
        } else {
            ViewMessage("You don't have internet connection",0);
        }
        return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            ViewMessage("Connection Success",1);
        } else {
            ViewMessage("You don't have internet connection",0);
        }
    }

    public void ViewMessage(String message, int position){

        switch (position){
            case 0:
                snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)sbView.getLayoutParams();
                params.gravity = Gravity.TOP;
                sbView.setLayoutParams(params);
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setGravity(1);
                textView.setTextColor(Color.RED);
                snackbar.show();
                break;
            case 1:
                snackbar = Snackbar.make(coordinatorLayout,message , Snackbar.LENGTH_SHORT);
                View sbView1 = snackbar.getView();
                CoordinatorLayout.LayoutParams params1=(CoordinatorLayout.LayoutParams)sbView1.getLayoutParams();
                params1.gravity = Gravity.TOP;
                sbView1.setLayoutParams(params1);
                TextView textView1 = (TextView) sbView1.findViewById(android.support.design.R.id.snackbar_text);
                textView1.setTextColor(Color.GREEN);
                snackbar.show();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME) {
          //  exitAlertMessageBox();

            Log.e("Home Button","Clicked");
            return true;
        }

        if(keyCode==KeyEvent.KEYCODE_BACK) {
            exitAlertMessageBox();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitAlertMessageBox() {

        AlertDialog dialog = new AlertDialog.Builder(LanguageActivity.this)
                .setTitle("ERROR")
                .setMessage("PRESS OK")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();

        isPaused = true;
        switch (activityAction){
            case 0:
            intent = new Intent(LanguageActivity.this, LanguageActivity.class);
            startActivity(intent);
            finish();
                break;
        }

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
        MyApplication.getInstance().setConnectivityListener(this);

        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 20);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =0.1f;// 100 / 100.0f;
        getWindow().setAttributes(lp);

        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {

                Settings.System.putInt(this.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 20);

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness =1f;// 100 / 100.0f;
                getWindow().setAttributes(lp);

                //near
//                Toast.makeText(getApplicationContext(), "near", Toast.LENGTH_SHORT).show();
//                Log.e("TAG" , "near");
            } else {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Settings.System.putInt(LanguageActivity.this.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, 20);

                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.screenBrightness =0.1f; // 100 / 100.0f;
                        getWindow().setAttributes(lp);
                    }
                }, 10000); //10 seconds
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        currentFocus = hasFocus;

        if (!hasFocus) {
            // Method that handles loss of window focus
            collapseNow();
        }
    }

    public void collapseNow() {

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    // Use reflection to trigger a method from 'StatusBarManager'

                    Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Method collapseStatusBar = null;

                    try {

                        // Prior to API 17, the method to call is 'collapse()'
                        // API 17 onwards, the method to call is `collapsePanels()`

                        if (Build.VERSION.SDK_INT > 16) {
                            collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager .getMethod("collapse");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    collapseStatusBar.setAccessible(true);

                    try {
                        collapseStatusBar.invoke(statusBarService);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    // Check if the window focus has been returned
                    // If it hasn't been returned, post this Runnable again
                    // Currently, the delay is 100 ms. You can change this
                    // value to suit your needs.
                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 100L);
                    }
                }
            }, 300L);
        }
    }

    public boolean exitActivity(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                sinhala.setTextColor(Color.parseColor("#FFFFFF"));
                tamil.setTextColor(Color.parseColor("#FFFFFF"));
                english.setTextColor(Color.parseColor("#FFFFFF"));
                btn_next.setText(getString(R.string.e_next));

                Settings.System.putInt(LanguageActivity.this.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 20);

                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness =0.1f;// 100 / 100.0f;
                getWindow().setAttributes(lp);
            }

        }, 1000*60*1); // 1000ms = 1s
        return true;
    }
}
