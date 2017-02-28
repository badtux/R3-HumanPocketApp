package com.rype3.leaveapp.rype3leaveapp;

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

public class LanguageActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private Intent intent = null;
    private Button sinhala,english,tamil,btn_next;
    private  MediaPlayer mediaPlayer_1 = null,mediaPlayer = null,mediaPlayer_2 = null;
    private Snackbar snackbar = null;
    private CoordinatorLayout coordinatorLayout;
    Utils utils;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        widget();

        context = this.getApplication();
        utils =  new Utils(context);
        utils.setSharedPreference(context,null,Constants.LANGUAGE_TYPE);

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
            if (mediaPlayer != null ){

                if (view == sinhala){
                    if (playSound(0)){
                        sinhala.setTextColor(Color.parseColor("#76FF03"));
                        tamil.setTextColor(Color.parseColor("#FFFFFF"));
                        english.setTextColor(Color.parseColor("#FFFFFF"));
                        utils.setSharedPreference(context,"s",Constants.LANGUAGE_TYPE);
                        btn_next.setText(getString(R.string.s_next));
                        btn_next.setTextSize(35);
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
                        btn_next.setTextSize(30);
                    }
                }

                if (view == btn_next){
                    if (utils.getBoolean(context,Constants.LANGUAGE_TYPE)){
                        if (playSound(1)) {
                        //    btn_next.setTextColor(Color.parseColor("#76FF03"));
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

                        if (mediaPlayer_2 != null){
                            mediaPlayer_2.stop();
                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = MediaPlayer.create(LanguageActivity.this, R.raw.click);
                        } mediaPlayer.start();
                        break;

                    case 1:
                        if (mediaPlayer_2 != null){
                            mediaPlayer_2.stop();
                        }
                        if (mediaPlayer_1.isPlaying()) {
                            mediaPlayer_2.stop();
                            mediaPlayer_1.stop();
                            mediaPlayer_1.release();
                            mediaPlayer_1 = MediaPlayer.create(LanguageActivity.this, R.raw.click_2);
                        } mediaPlayer_1.start();
                        break;

                    case 2:
                        if (mediaPlayer_2.isPlaying()) {
                            mediaPlayer_2.stop();
                            mediaPlayer_2.release();
                            mediaPlayer_2 = MediaPlayer.create(LanguageActivity.this, R.raw.error);
                        } mediaPlayer_2.start();
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
}
