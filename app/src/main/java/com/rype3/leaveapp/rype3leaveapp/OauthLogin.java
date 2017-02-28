package com.rype3.leaveapp.rype3leaveapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class OauthLogin extends AppCompatActivity {

    Button auth;
    Intent intent;
    Utils utils;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_login);

        context = this.getApplication();
        utils = new Utils(context);


        if (utils.getBoolean(context,Constants.OAUTH_TOKEN)){
            intent = new Intent(OauthLogin.this,LanguageActivity.class);
            startActivity(intent);
            finish();
        }

        auth = (Button)findViewById(R.id.auth);
        auth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                intent = new Intent(OauthLogin.this,Sign_inActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
