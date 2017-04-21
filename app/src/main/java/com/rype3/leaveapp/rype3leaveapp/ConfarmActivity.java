package com.rype3.leaveapp.rype3leaveapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConfarmActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private TextView name,epf_no,leave_type,leave_category,leave_period,time_to,time_from,leave_reson;
    private TextView tittle_name,tittle_epf_no,tittle_leave_type,tittle_leave_category,tittle_leave_reson,tittle_summery;
    private Button confirm,exit, back,help;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    Utils utils;
    private Intent intent;
    private String jsonResult,from_date,to_date;;
    Context context;
    private Constants constants;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer = null,mediaPlayer_2 = null;

    private int leaveTypePosition = 0;
    private int leavecategoryPosition = 0;
    private int leavereasonPosition = 0;

    private String[] leaveReasonStringArray,leaveCategoryStringArray,leaveCategoryStringArrayEnglish,leaveTypeStringArray,leaveTypeStringArrayEnglish;

    private String[] leaveTypeArray = {"","first","second","full_day"};
    private String[] leaveCategoryArray = {"","casual","annual","medical","nopay"};
    private   AlertDialog dialog;
    private ProgrammaticallyExitMessage programmaticallyExitMessage;
    // To keep track of activity's window focus
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;
    private Handler handler;

    private int activityAction = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confarm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = this.getApplication();
        utils = new Utils(context);
        constants = new Constants(context);
        widget(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));

        intent = getIntent();
        jsonResult = intent.getStringExtra("json");
        from_date = intent.getStringExtra("from_date");
        to_date = intent.getStringExtra("to_date");

        leaveTypeStringArrayEnglish = getResources().getStringArray(R.array.e_leaveType_array);
        leaveCategoryStringArrayEnglish = getResources().getStringArray(R.array.e_leaveCat_array);

        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                leaveReasonStringArray = getResources().getStringArray(R.array.s_reason);
                break;

            case "t":
                leaveReasonStringArray = getResources().getStringArray(R.array.t_reason);
                break;

            case "e":
                leaveReasonStringArray = getResources().getStringArray(R.array.e_reason);
                break;
        }


        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                leaveCategoryStringArray = getResources().getStringArray(R.array.s_leaveCat_array);
                break;

            case "t":
                leaveCategoryStringArray = getResources().getStringArray(R.array.t_leaveCat_array);
                break;

            case "e":
                leaveCategoryStringArray = getResources().getStringArray(R.array.e_leaveCat_array);
                break;
        }


        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                leaveTypeStringArray = getResources().getStringArray(R.array.s_leaveType_array);
                break;

            case "t":
                leaveTypeStringArray = getResources().getStringArray(R.array.t_leaveType_array);
                break;

            case "e":
                leaveTypeStringArray = getResources().getStringArray(R.array.e_leaveType_array);
                break;
        }

        try {
            JSONObject jsonObject2 = new JSONObject(jsonResult);

            int leaveReasonPosition1 = Integer.parseInt(jsonObject2.getString("reasonPosition"));
            int leaveCategoryPosition1 = Integer.parseInt(jsonObject2.getString("leaveCategoryPosition"));
            int leaveTypePosition1 = Integer.parseInt(jsonObject2.getString("leaveTypePosition"));

            leavereasonPosition = leaveReasonPosition1;
            leavecategoryPosition = leaveCategoryPosition1;
            leaveTypePosition = leaveTypePosition1;

            name.setText(utils.getSharedPreference(context,Constants.USERNAME));
            epf_no.setText(utils.getSharedPreference(context,Constants.EPF_NUMBER));
            leave_type.setText(leaveTypeStringArray[leaveTypePosition1]);
            leave_category.setText(leaveCategoryStringArray[leaveCategoryPosition1]);
            time_to.setText(to_date);
            time_from.setText(from_date);
            leave_reson.setText(leaveReasonStringArray[leaveReasonPosition1]);

        } catch (JSONException e) {
            e.printStackTrace();
        }

       // exitActivity();
    }

    public void widget(String languageType){
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        name = (TextView) findViewById(R.id.person_name);
        epf_no = (TextView) findViewById(R.id.textView_epf_no);
        leave_type = (TextView) findViewById(R.id.leave_type);
        leave_category = (TextView) findViewById(R.id.leave_category);
        time_to = (TextView) findViewById(R.id.textView_leave_period_to);
        time_from = (TextView) findViewById(R.id.textView_leave_period_from);
        leave_reson = (TextView) findViewById(R.id.textView_leave_reason);

        confirm = (Button) findViewById(R.id.button_confirm);
        confirm.setOnClickListener(onclick);

        help = (Button) findViewById(R.id.button_help);
        help.setOnClickListener(onclick);

        back = (Button) findViewById(R.id.button_edit);
        back.setOnClickListener(onclick);

        exit = (Button) findViewById(R.id.button_exit);
        exit.setOnClickListener(onclick);

        tittle_summery = (TextView) findViewById(R.id.title_userName);
        tittle_name = (TextView) findViewById(R.id.tittle_name);
        tittle_epf_no = (TextView) findViewById(R.id.tittle_epf);
        tittle_leave_type = (TextView) findViewById(R.id.title_leave_type);
        tittle_leave_category = (TextView) findViewById(R.id.tittle_category);
        leave_period = (TextView) findViewById(R.id.tittle_leave_period);
       // tittle_leave_day = (TextView) findViewById(R.id.tittle_leave_day);
        tittle_leave_reson = (TextView) findViewById(R.id.tittle_reason);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click_2);
        mediaPlayer_2 = MediaPlayer.create(this, R.raw.error);


        switch (languageType){
            case "s":
                tittle_summery.setText(getString(R.string.s_summary));
                tittle_name.setText(getString(R.string.s_name));
                tittle_epf_no.setText(getString(R.string.s_epf_number));
                tittle_leave_type.setText(getString(R.string.s_leave_type));
                tittle_leave_category.setText(getString(R.string.s_leave_category));
                leave_period.setText(getString(R.string.s_period));
                tittle_leave_reson.setText(getString(R.string.s_leave_reason));

                confirm.setText(getString(R.string.s_ok));
                help.setText(getString(R.string.s_help));
                back.setText(getString(R.string.s_back));
                exit.setText(getString(R.string.s_exit));
                break;

            case "e":
                tittle_summery.setText(getString(R.string.e_summary));
                tittle_name.setText(getString(R.string.e_name));
                tittle_epf_no.setText(getString(R.string.e_epf_number));
                tittle_leave_type.setText(getString(R.string.e_leave_type));
                tittle_leave_category.setText(getString(R.string.e_leave_category));
                leave_period.setText(getString(R.string.e_period));
                tittle_leave_reson.setText(getString(R.string.e_leave_reason));

                confirm.setText(getString(R.string.e_ok));
                help.setText(getString(R.string.e_help));
                back.setText(getString(R.string.e_back));
                exit.setText(getString(R.string.e_exit));
                break;

            case "t":
                tittle_summery.setText(getString(R.string.t_summary));
                tittle_name.setText(getString(R.string.t_name));
                tittle_epf_no.setText(getString(R.string.t_epf_number));
                tittle_leave_type.setText(getString(R.string.t_leave_type));
                tittle_leave_category.setText(getString(R.string.t_leave_category));
                leave_period.setText(getString(R.string.t_period));

             //   tittle_leave_day.setText(getString(R.string.t_leave_count));
                tittle_leave_reson.setText(getString(R.string.t_leave_reason));

                confirm.setText(getString(R.string.t_ok));
                help.setText(getString(R.string.t_help));
                back.setText(getString(R.string.t_back));
                exit.setText(getString(R.string.e_exit));
                break;
        }

        Settings.System.putInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 20);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =1f;// 100 / 100.0f;
        getWindow().setAttributes(lp);
    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == confirm){
                if (playSound(1)) {
                    activityAction = 1;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResult);

                        Log.e("TAG Reason: ",jsonObject.getString("reason"));

                        new ProcressAsyncTask(
                                ConfarmActivity.this,
                                constants.urls(1),
                                utils.getSharedPreference(context,Constants.EPF_NUMBER),
                                null,
                                utils.getSharedPreference(context,Constants.EPF_NUMBER),
                                "POST",1,"1.0",null,
                                leaveTypeArray[leaveTypePosition],
                                leaveCategoryArray[leavecategoryPosition],
                                from_date,
                                to_date,
                                utils.getSharedPreference(context,Constants.TOKEN),
                                leaveReasonStringArray[leavereasonPosition],utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)).execute();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (view == back){
                if (playSound(1)) {
                    activityAction = 1;
                    intent = new Intent(ConfarmActivity.this, ApplicationActivity.class);
                    intent.putExtra("json" ,jsonResult);
                    startActivity(intent);
                    finish();
                }
            }

            if (view == help){
                if (playSound(1)) {
                    activityAction = 1;
                }
            }

            if (view == exit){
                if (playSound(1)) {
                    activityAction = 1;
                    intent = new Intent(ConfarmActivity.this, LanguageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    };

    public void parseJsonResponse1(final String result) {
        if (result != null) {
            Log.e("Result : " ,result);

            JSONObject jsonObjectResult = null;
            try {
                jsonObjectResult = new JSONObject(result);

                boolean status = jsonObjectResult.getBoolean("status");

                if (status){
                    CustomToast();
                    exitAlertMessageBox(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));

                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing()){
                                dialog.dismiss();
                            }
                            intent = new Intent(ConfarmActivity.this, LanguageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 5000);
                }else{
                    playSound(0);
                    ViewMessage("SERVER ERROR....! ", 0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    public boolean playSound(int position) {
        try {
            switch (position) {
                case 0:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(ConfarmActivity.this, R.raw.click);
                    }
                    mediaPlayer.start();
                    break;

                case 1:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.release();
                        mediaPlayer_1 = MediaPlayer.create(ConfarmActivity.this, R.raw.click_2);
                    }
                    mediaPlayer_1.start();
                    break;

                case 2:
                    if (mediaPlayer_2.isPlaying()) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.release();
                        mediaPlayer_2 = MediaPlayer.create(ConfarmActivity.this, R.raw.error);
                    } mediaPlayer_2.start();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void CustomToast(){

        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_1, (ViewGroup)
                this.findViewById(R.id.like_popup_layout));
        ImageView imageView = (ImageView) layout.findViewById(R.id.like_popup_iv);

        TextView text = (TextView) layout.findViewById(R.id.like_popup_tv);

        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)) {
            case "s":
                text.setText(getString(R.string.s_success));
                break;

            case "t":
                text.setText(getString(R.string.t_success));
                break;

            case "e":
                text.setText(getString(R.string.e_success));
                break;
        }

        Toast toast = new Toast(this.getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        toast.show();
    }


    private void exitAlertMessageBox(String language) {
        String tittle = "";
        String message = "";
        String ok = "";
        String exit = "";

        switch (language){
            case "s":
                tittle = getString(R.string.s_exit);
                message = getString(R.string.s_exit_message);
                ok = getString(R.string.s_ok);
                exit = getString(R.string.s_exit);
                break;

            case "e":
                tittle = getString(R.string.e_exit);
                message = getString(R.string.e_exit_message);
                ok = getString(R.string.e_ok);
                exit = getString(R.string.e_exit);
                break;

            case "t":
                tittle = getString(R.string.t_exit);
                message = getString(R.string.t_exit_message);
                ok = getString(R.string.t_ok);
                exit = getString(R.string.t_exit);
                break;
        }


        dialog = new AlertDialog.Builder(ConfarmActivity.this)
                .setTitle(tittle)
                .setMessage(message)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handler.equals(null);
                        intent = new Intent(ConfarmActivity.this, LanguageActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })

                .setNegativeButton(exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        handler.equals(null);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        return;
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
            exitAlertMessageBox();
        }

        if(keyCode==KeyEvent.KEYCODE_BACK) {
            exitAlertMessageBox();
        }
        return false;
    }

    private void exitAlertMessageBox() {

        AlertDialog dialog = new AlertDialog.Builder(ConfarmActivity.this)
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
        isPaused =false;
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused =true;
        Log.e("onPause() :  " , String.valueOf(activityAction));
        switch (activityAction){
            case 0:
                intent = new Intent(ConfarmActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
                break;
        }
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
                intent = new Intent(ConfarmActivity.this,LanguageActivity.class);
                startActivity(intent);
                finish();
            }

        }, 1000*60*1); // 1000ms = 1s
    }
}
