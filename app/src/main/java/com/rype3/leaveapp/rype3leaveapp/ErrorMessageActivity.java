package com.rype3.leaveapp.rype3leaveapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ErrorMessageActivity extends AppCompatActivity {

    TextView title_1,title_2,warning;
    Button button;
    Utils utils;
    private MediaPlayer mediaPlayer_1 = null;
    private Intent intent = null;
    Context context;

    // To keep track of activity's window focus
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    private int activityAction = 0;

  //  private ProgrammaticallyExitMessage programmaticallyExitMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = this.getApplication();
        utils =  new Utils(context);

   //     programmaticallyExitMessage = new ProgrammaticallyExitMessage();
   //     programmaticallyExitMessage.ProgrammaticallyExitMessage(ErrorMessageActivity.this,context,0,utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));

        title_1 = (TextView) findViewById(R.id.title_1);
        title_2 = (TextView) findViewById(R.id.title_3);
        warning = (TextView) findViewById(R.id.tv_warnning);
        button = (Button) findViewById(R.id.btn_next);

        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 20);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =1f;// 100 / 100.0f;
        getWindow().setAttributes(lp);

        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click);

        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                title_1.setText(getString(R.string.s_wrong_1));
                title_2.setText(getString(R.string.s_press));
                warning.setText(getString(R.string.s_warning));
                button.setText(getString(R.string.s_button));
                break;

            case "t":
                title_1.setText(getString(R.string.t_wrong_1));
                title_2.setText(getString(R.string.t_press));
                warning.setText(getString(R.string.t_warning));
                button.setText(getString(R.string.t_button));
                break;

            case "e":
                title_1.setText(getString(R.string.e_wrong_1));
                title_2.setText(getString(R.string.e_press));
                warning.setText(getString(R.string.e_warning));
                button.setText(getString(R.string.e_button));
                break;
        }
        button.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (playSound(1)) {
                activityAction =1;
            //    programmaticallyExitMessage.ProgrammaticallyExitMessage(ErrorMessageActivity.this,context,1,utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));
                intent = new Intent(ErrorMessageActivity.this, EPFnumberActivity.class);
                startActivity(intent);
                finish();
            }
        }
        });

     //   exitActivity();
    }

    public boolean playSound(int position){
        try {
            switch (position){
                case 1:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.release();
                        mediaPlayer_1 = MediaPlayer.create(ErrorMessageActivity.this, R.raw.click_2);
                    } mediaPlayer_1.start();
                    break;
            }
        } catch(Exception e) { e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME) {
            exitAlertMessageBox();
        }

        if(keyCode==KeyEvent.KEYCODE_BACK) {
            exitAlertMessageBox();
        }
        return false;
    }

    private void exitAlertMessageBox() {

        AlertDialog dialog = new AlertDialog.Builder(ErrorMessageActivity.this)
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
        Log.e("onPause() :  " , String.valueOf(activityAction));
        switch (activityAction){
            case 0:
                intent = new Intent(ErrorMessageActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
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

    public void exitActivity(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                intent = new Intent(ErrorMessageActivity.this,LanguageActivity.class);
                startActivity(intent);
                finish();
            }

        }, 1000*60*1); // 1000ms = 1s
    }

}
