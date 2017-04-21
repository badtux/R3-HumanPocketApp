package com.rype3.leaveapp.rype3leaveapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EPFnumberActivity extends AppCompatActivity {
    Utils utils;
    private Snackbar snackbar;
    Context context;
    Button one, two, three, four, five, six, seven, eight, nine, zero, back, clear;
    private EditText mPasswordField;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer_2 = null,mediaPlayer = null;
    private Button next,exit_btn;
    private Intent intent = null;
    private CoordinatorLayout coordinatorLayout;

    // To keep track of activity's window focus
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    private int activityAction = 0;

    private ProgrammaticallyExitMessage programmaticallyExitMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epf);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this.getApplication();
        utils =  new Utils(context);

       // programmaticallyExitMessage = new ProgrammaticallyExitMessage();
    //    programmaticallyExitMessage.ProgrammaticallyExitMessage(EPFnumberActivity.this,context,0,utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));

        widget(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (validation(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE))) {
                        if (playSound(1)) {
                        activityAction =1;
                        utils.setSharedPreference(context,mPasswordField.getText().toString(),Constants.EPF_NUMBER);
                        intent = new Intent(EPFnumberActivity.this, PINnumberActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                        playSound(2);
                }
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (playSound(1)) {
                        activityAction =1;
                        utils.setSharedPreference(context,null,Constants.EPF_NUMBER);
                        intent = new Intent(EPFnumberActivity.this, LanguageActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
        });

     //   exitActivity();
    }

    public void widget(String languageType) {
        one = (Button)findViewById(R.id.button1);
        one.setOnClickListener(onClick);

        two = (Button)findViewById(R.id.button2);
        two.setOnClickListener(onClick);

        three = (Button)findViewById(R.id.button3);
        three.setOnClickListener(onClick);

        four = (Button)findViewById(R.id.button4);
        four.setOnClickListener(onClick);

        five = (Button)findViewById(R.id.button5);
        five.setOnClickListener(onClick);

        six = (Button)findViewById(R.id.button6);
        six.setOnClickListener(onClick);

        seven = (Button)findViewById(R.id.button7);
        seven.setOnClickListener(onClick);

        eight = (Button)findViewById(R.id.button8);
        eight.setOnClickListener(onClick);

        nine = (Button)findViewById(R.id.button9);
        nine.setOnClickListener(onClick);

        zero = (Button)findViewById(R.id.button0);
        zero.setOnClickListener(onClick);

        back = (Button)findViewById(R.id.button_back);
        back.setOnClickListener(onClick);

        clear = (Button)findViewById(R.id.button_clear);
        clear.setOnClickListener(onClick);

        next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(onClick);

        exit_btn = (Button) findViewById(R.id.button_back_language);
        exit_btn.setOnClickListener(onClick);


        mPasswordField = (EditText) findViewById(R.id.password_field);
        mPasswordField.setInputType(InputType.TYPE_NULL);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click_2);
        mediaPlayer_2 = MediaPlayer.create(this, R.raw.error);


        switch (languageType){
            case "s":
                next.setText(getString(R.string.s_next));
               // clear.setText(getString(R.string.s_clear));
                back.setText(getString(R.string.s_back));
                back.setTextSize(25);

                exit_btn.setText(getString(R.string.s_exit));

                mPasswordField.setHint(getString(R.string.s_epf_no));

                break;

            case "t":
                next.setText(getString(R.string.t_next));
               // clear.setText(getString(R.string.t_clear));
                back.setText(getString(R.string.t_back));
                exit_btn.setText(getString(R.string.t_exit));
                mPasswordField.setHint(getString(R.string.t_epf_no));
                back.setTextSize(20);
                break;

            case "e":
                next.setText(getString(R.string.e_next));
              //  clear.setText(getString(R.string.e_clear));
                back.setText(getString(R.string.e_back));
                exit_btn.setText(getString(R.string.e_exit));
                mPasswordField.setHint(getString(R.string.e_epf_no));
                back.setTextSize(25);
                break;
        }
        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 20);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =1f;// 100 / 100.0f;
        getWindow().setAttributes(lp);

    }

    public View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (playSound(0)) {
                if (v.getTag() != null && "number_button".equals(v.getTag())) {
                    mPasswordField.append(((TextView) v).getText());
                    return;
                }
            }
            switch (v.getId()) {
                case R.id.button_clear: {
                    if (playSound(0)) {// handle clear button
                    mPasswordField.setText(null);
                }
                }
                break;
                case R.id.button_back: { // handle backspace button
                    if (playSound(0)) {
                        // delete one character
                        Editable editable = mPasswordField.getText();
                        int charCount = editable.length();
                        if (charCount > 0) {
                            editable.delete(charCount - 1, charCount);
                        }
                    }
                }
                break;
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
                        mediaPlayer = MediaPlayer.create(EPFnumberActivity.this, R.raw.click);
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
                        mediaPlayer_1 = MediaPlayer.create(EPFnumberActivity.this, R.raw.click_2);
                    } mediaPlayer_1.start();
                    break;

                case 2:
                    if (mediaPlayer_2.isPlaying()) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                        mediaPlayer_2 = MediaPlayer.create(EPFnumberActivity.this, R.raw.error);
                    } mediaPlayer_2.start();
                    break;
            }
        } catch(Exception e) { e.printStackTrace();
        }
        return true;
    }

    public  boolean validation(String language){

        String epfNo = mPasswordField.getText().toString();

        if(epfNo.isEmpty()){
            switch (language){
                case "s":
                    ViewMessage(getString(R.string.s_epf_no_wrong), 0);
                    mPasswordField.setHintTextColor(Color.RED);
                    break;

                case "t":
                    ViewMessage(getString(R.string.t_epf_no_wrong), 0);
                    mPasswordField.setHintTextColor(Color.RED);
                    break;

                case "e":
                    ViewMessage(getString(R.string.e_epf_no_wrong), 0);
                    mPasswordField.setHintTextColor(Color.RED);
                    break;
            }
            return false;
        }

        return true;
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
            exitAlertMessageBox();
        }

        if(keyCode==KeyEvent.KEYCODE_BACK) {
            exitAlertMessageBox();
        }
        return false;
    }

    private void exitAlertMessageBox() {

        AlertDialog dialog = new AlertDialog.Builder(EPFnumberActivity.this)
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

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        Log.e("onPause() :  " , String.valueOf(activityAction));
        switch (activityAction){
            case 0:
                intent = new Intent(EPFnumberActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    public void exitActivity(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
            intent = new Intent(EPFnumberActivity.this,LanguageActivity.class);
            startActivity(intent);
            finish();
            }

        }, 1000*60*1); // 1000ms = 1s
    }
    }
