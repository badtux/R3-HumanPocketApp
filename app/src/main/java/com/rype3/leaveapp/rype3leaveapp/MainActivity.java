package com.rype3.leaveapp.rype3leaveapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private Button next_btn,back_btn ,key_epf,key_pin;
    private Intent intent = null;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer = null;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private EditText editText_epf_no, editTextPin_no;
    private Constants constants;
    Utils utils;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplication();
        utils =  new Utils(context);
        constants = new Constants(context);
        widget(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));
    }


    public void widget(String languageType){
        next_btn = (Button) findViewById(R.id.button_next);
        next_btn.setOnClickListener(onclick);

        back_btn = (Button) findViewById(R.id.button_back);
        back_btn.setOnClickListener(onclick);

        key_pin = (Button) findViewById(R.id.button_pin_key);
        key_pin.setOnClickListener(onclick);

        key_epf = (Button) findViewById(R.id.button_epf);
        key_epf.setOnClickListener(onclick);

        editText_epf_no = (EditText) findViewById(R.id.editText_epf);
        editTextPin_no = (EditText) findViewById(R.id.editText_pin);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click_2);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        switch (languageType){
            case "s":
                next_btn.setText(getString(R.string.s_next));
                back_btn.setText(getString(R.string.s_back));
                next_btn.setTextSize(20);
                back_btn.setTextSize(20);
                back_btn.setGravity(1);
                editTextPin_no.setHint(getString(R.string.s_pin_no));
                editText_epf_no.setHint(getString(R.string.s_epf_no));
                break;

            case "t":
                next_btn.setText(getString(R.string.t_next));
                back_btn.setText(getString(R.string.t_back));
                editTextPin_no.setHint(getString(R.string.t_pin_no));
                editText_epf_no.setHint(getString(R.string.t_epf_no));
                break;

            case "e":
                next_btn.setText(getString(R.string.e_next));
                back_btn.setText(getString(R.string.e_back));
                editTextPin_no.setHint(getString(R.string.e_pin_no));
                editText_epf_no.setHint(getString(R.string.e_epf_no));
                break;
        }
    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == next_btn){
                if (playSound(1)) {


            //        Activity activity, String url , String email, String pin, String epf , String HTTP_TYPE, int type, String version
           //         new ProcressAsyncTask(MainActivity.this, constants.urls(0), "tech@ceynet.asia", "wmmadmin", "tech@cetnet.asia",  "POST",0,"1.0").execute();

                    intent = new Intent(MainActivity.this, ApplicationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            if (view == back_btn){
                if (playSound(1)) {
                    intent = new Intent(MainActivity.this, LanguageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            if (view == key_pin){
                if (playSound(1)) {
                    login_box(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE),0);
                }
            }

            if (view == key_epf){
                if (playSound(1)) {
                    login_box(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE),1);
                }
            }
        }
    };

    public boolean playSound(int position){
        try {
            switch (position){
                case 0:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.click);
                    } mediaPlayer.start();
                    break;

                case 1:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.release();
                        mediaPlayer_1 = MediaPlayer.create(MainActivity.this, R.raw.click_2);
                    } mediaPlayer_1.start();
                    break;
            }
        } catch(Exception e) { e.printStackTrace();
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
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    private void login_box(String language,int box_position) {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.pin_xml);
        Button buttonOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button buttonCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        switch (box_position){
            case 0:
                switch (language){
                    case "s":
                        dialog.setTitle(getString(R.string.s_pin_number));
                        buttonOk.setText(getString(R.string.s_ok));
                        buttonCancel.setText(getString(R.string.s_cancel));
                        buttonOk.setPadding(10,10,10,10);
                        buttonCancel.setPadding(10,10,10,10);
                        break;

                    case "t":
                        dialog.setTitle(getString(R.string.t_pin_number));
                        buttonOk.setText(getString(R.string.t_ok));
                        buttonCancel.setText(getString(R.string.t_cancel));
                        break;

                    case "e":
                        dialog.setTitle(getString(R.string.e_pin_number));
                        buttonOk.setText(getString(R.string.e_ok));
                        buttonCancel.setText(getString(R.string.e_cancel));
                        break;
                }
                break;

            case 1:
                switch (language){
                    case "s":
                        dialog.setTitle(getString(R.string.s_epf_number));
                        buttonOk.setText(getString(R.string.s_ok));
                        buttonCancel.setText(getString(R.string.s_cancel));
                        buttonOk.setPadding(10,10,10,10);
                        buttonCancel.setPadding(10,10,10,10);
                        break;

                    case "t":
                        dialog.setTitle(getString(R.string.t_epf_number));
                        buttonOk.setText(getString(R.string.t_ok));
                        buttonCancel.setText(getString(R.string.t_cancel));
                        break;

                    case "e":
                        dialog.setTitle(getString(R.string.e_epf_number));
                        buttonOk.setText(getString(R.string.e_ok));
                        buttonCancel.setText(getString(R.string.e_cancel));
                        break;
                }
                break;
        }

        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().verticalMargin = -0.5F;

        final EditText channel_1 = (EditText) dialog.findViewById(R.id.et_password);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playSound(1)) {
                    hideKeyBoard();
                    dialog.dismiss();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playSound(1)) {
                    hideKeyBoard();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void hideKeyBoard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
