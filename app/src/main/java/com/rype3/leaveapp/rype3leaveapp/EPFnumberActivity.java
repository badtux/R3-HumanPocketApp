package com.rype3.leaveapp.rype3leaveapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epf);
        context = this.getApplication();
        utils =  new Utils(context);

        widget(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (validation(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE))) {
                        if (playSound(1)) {
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
                        utils.setSharedPreference(context,null,Constants.EPF_NUMBER);
                        intent = new Intent(EPFnumberActivity.this, LanguageActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }
        });
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
                back.setTextSize(35);

                exit_btn.setText(getString(R.string.s_exit));

                mPasswordField.setHint(getString(R.string.s_epf_no));


                break;

            case "t":
                next.setText(getString(R.string.t_next));
               // clear.setText(getString(R.string.t_clear));
                back.setText(getString(R.string.t_back));
                exit_btn.setText(getString(R.string.t_exit));
                mPasswordField.setHint(getString(R.string.t_epf_no));
                back.setTextSize(28);
                break;

            case "e":
                next.setText(getString(R.string.e_next));
              //  clear.setText(getString(R.string.e_clear));
                back.setText(getString(R.string.e_back));
                exit_btn.setText(getString(R.string.e_exit));
                mPasswordField.setHint(getString(R.string.e_epf_no));
                back.setTextSize(30);
                break;
        }

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
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(EPFnumberActivity.this, R.raw.click);
                    } mediaPlayer.start();
                    break;

                case 1:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.release();
                        mediaPlayer_1 = MediaPlayer.create(EPFnumberActivity.this, R.raw.click_2);
                    } mediaPlayer_1.start();
                    break;

                case 2:
                    if (mediaPlayer_2.isPlaying()) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.release();
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
    }
