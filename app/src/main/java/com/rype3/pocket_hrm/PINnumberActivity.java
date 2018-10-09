package com.rype3.pocket_hrm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PINnumberActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    Button one, two, three, four, five, six, seven, eight, nine, zero, clear;
    private Button connect, exit_btn;
    private EditText mPasswordField;
    private Intent intent = null;
    private ImageView back;
    private CoordinatorLayout coordinatorLayout;
    private Constants constants;
    Utils utils;
    private Snackbar snackbar;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinnumber);

        context = this.getApplication();
        utils =  new Utils(context);
        constants = new Constants(context);

        widget();

        if (checkConnection()) {
            new ProcressAsyncTask(null,
                    PINnumberActivity.this,
                    utils,
                    Constants.BASE_URL +"/io/api/v1/device/register",
                    null,
                    null,
                    null,
                    "POST",
                    1,
                    PocketHr.Version(),
                    null,PocketHr.GetSharedPreference(Constants.DEVICE_ID),
                    null,
                    null,
                    null,
                    null,
                    null,
                    0,
                    null,
                    null,
                    null).execute();
        }

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validation()) {
                    if (checkConnection()) {
                            new ProcressAsyncTask(null,
                                    PINnumberActivity.this,utils,
                                    Constants.BASE_URL +"/human/api/v1/login",
                                    PocketHr.GetSharedPreference(Constants.EPF_NUMBER),
                                    mPasswordField.getText().toString(),
                                    PocketHr.GetSharedPreference(Constants.EPF_NUMBER),
                                    "POST",
                                    0,
                                    PocketHr.Version(),
                                    null,
                                    PocketHr.GetSharedPreference(Constants.DEVICE_ID),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    0,
                                    null,
                                    null,
                                    null).execute();
                    }
                }
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    utils.setSharedPreference(context,null,Constants.EPF_NUMBER);
                    utils.setSharedPreference(context,null,Constants.PIN);
                    PocketHr.startSpecificActivity(PINnumberActivity.this,context,EPFnumberActivity.class);
            }
        });
    }

    public void widget(){
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

        connect = (Button) findViewById(R.id.button_connect);
        connect.setOnClickListener(onClick);

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
            PocketHr.snackBarMessage(coordinatorLayout,"Please enter your PIN number",Color.RED);
            return false;
        }

        return true;
    }

    public void parseJsonResponse(final String result) {
        if (result != null) {

            Log.e("Result : ", result);//1567234

            try {
                JSONObject jsonObjectResult = new JSONObject(result);

                boolean status = jsonObjectResult.getBoolean("status");

                if (status){
                    String result1 = jsonObjectResult.getString("result");

                    JSONObject jsonObjectResult1 = new JSONObject(result1);
                    String token = jsonObjectResult1.getString("token");
                    String user = jsonObjectResult1.getString("user");
                    JSONObject jsonObjectUser  = new JSONObject(user);

                    String fullName = jsonObjectUser.getString("full_name");

                    utils.setSharedPreference(context,token,Constants.TOKEN);
                    utils.setSharedPreference(context,fullName,Constants.USERNAME);
                    PocketHr.startSpecificActivity(PINnumberActivity.this,context,MainActivity.class);

                }else{

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseJsonRegisterResponse(final String result) {
        if (result != null) {

            Log.e("RegisterResponse : ", result);//1567234

            try {
                JSONObject jsonObjectResult = new JSONObject(result);

                boolean status = jsonObjectResult.getBoolean("status");

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
            PocketHr.snackBarMessage(coordinatorLayout,"You don't have internet connection",Color.RED);
        }
        return false;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            PocketHr.snackBarMessage(coordinatorLayout,"Connection success",Color.GREEN);
        } else {
            PocketHr.snackBarMessage(coordinatorLayout,"You don't have internet connection",Color.RED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
}
