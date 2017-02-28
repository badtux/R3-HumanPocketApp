package com.rype3.leaveapp.rype3leaveapp;

import android.app.Activity;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Sign_inActivity extends AppCompatActivity {

    private static String CLIENT_ID = "589bf05f277bf8424f3cc6ea";
    //Use your own client id
    private static String CLIENT_SECRET = "0y7F9mIlYXsOLi0mhbTb";
    //Use your own client secret
    private static String REDIRECT_URI = "http://localhost";
    private static String GRANT_TYPE = "authorization_code";
    private static String TOKEN_URL = "http://wmmmendis.rype3.net/human/account/oauth/access_token";
    private static String OAUTH_URL = "http://wmmmendis.rype3.net/human/account/oauth/authorize";
    private static String OAUTH_SCOPE = "";
    //Change the Scope as you need
    WebView web;

    Utils utils;
    Context context;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        context = this.getApplication();
        utils = new Utils(context);

        web = (WebView) findViewById(R.id.webv);
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID + "&scope=" + OAUTH_SCOPE);

        web.setWebViewClient(new WebViewClient() {

            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            String authCode;

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.e("url : ", url);

                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                    Log.e("", "CODE : " + authCode);
                    authComplete = true;
                    resultIntent.putExtra("code", authCode);
                    Sign_inActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    setResult(Activity.RESULT_CANCELED, resultIntent);

                    utils.setSharedPreference(context,authCode,Constants.OAUTH_CODE);
                    new TokenGet().execute();

                } else if (url.contains("error=access_denied")) {
                    Log.e("", "ACCESS_DENIED_HERE");
                    resultIntent.putExtra("code", authCode);
                    authComplete = true;
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class TokenGet extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        String Code;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Sign_inActivity.this);
            pDialog.setMessage("Contacting with Rype3 ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... args) {
            GetAccessToken jParser = new GetAccessToken();
            JSONObject json = jParser.gettoken(TOKEN_URL,utils.getSharedPreference(context,Constants.OAUTH_CODE),CLIENT_ID,CLIENT_SECRET,REDIRECT_URI,GRANT_TYPE);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            Log.e("Token Json : ", json.toString());
            pDialog.dismiss();
            if (json != null){

                try {
                    String access_token = json.getString("access_token");
                    String token_type = json.getString("token_type");
                    String expires_in = json.getString("expires_in");
                 //   String refresh = json.getString("refresh_token");

                    utils.setSharedPreference(context,access_token,Constants.OAUTH_TOKEN);

                    Log.e("Token Access", access_token);
                    Log.e("Expire", expires_in);

                    intent = new Intent(Sign_inActivity.this,LanguageActivity.class);
                    startActivity(intent);
                    finish();

                    web.clearCache(true);
                    web.clearFormData();


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }
    }
}