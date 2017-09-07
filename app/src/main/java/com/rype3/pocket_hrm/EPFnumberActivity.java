package com.rype3.pocket_hrm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EPFnumberActivity extends AppCompatActivity {
    Utils utils;
    private Snackbar snackbar;
    Context context;
    Button one, two, three, four, five, six, seven, eight, nine, zero, clear;
    private EditText mPasswordField;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer_2 = null,mediaPlayer = null;
    private Button next,exit_btn;
    private ImageView back;
    private Intent intent = null;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epf);
        context = this.getApplication();
        utils =  new Utils(context);

        widget();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    utils.setSharedPreference(context, mPasswordField.getText().toString(), Constants.EPF_NUMBER);
                    intent = new Intent(EPFnumberActivity.this, PINnumberActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void widget() {
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

        back = (ImageView)findViewById(R.id.button_back);
        back.setOnClickListener(onClick);

        clear = (Button)findViewById(R.id.button_clear);
        clear.setOnClickListener(onClick);
//
        next = (Button) findViewById(R.id.button_next);
        next.setOnClickListener(onClick);

        exit_btn = (Button) findViewById(R.id.button_back_language);
        exit_btn.setOnClickListener(onClick);


        mPasswordField = (EditText) findViewById(R.id.password_field);
        mPasswordField.setInputType(InputType.TYPE_NULL);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

    }

    public View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                if (v.getTag() != null && "number_button".equals(v.getTag())) {
                    mPasswordField.append(((TextView) v).getText());
                    return;
            }
            switch (v.getId()) {
                case R.id.button_clear: {
                    mPasswordField.setText(null);
                }
                break;
                case R.id.button_back: { // handle backspace button
                        // delete one character
                        Editable editable = mPasswordField.getText();
                        int charCount = editable.length();
                        if (charCount > 0) {
                            editable.delete(charCount - 1, charCount);
                        }
                    }
                break;
                }
            }
        };


    public  boolean validation(){

        String epfNo = mPasswordField.getText().toString();

        if(epfNo.isEmpty()){
            ViewMessage("Please enter your EPF number", 0);
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
