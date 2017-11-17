package com.rype3.pocket_hrm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Sign_inActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    //Client id
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
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    Intent intent;
    private Constants constants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        context = this.getApplication();
        utils = new Utils(context);
        constants = new Constants(context);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        web = (WebView) findViewById(R.id.webv);
        web.getSettings().setJavaScriptEnabled(true);
        web.clearHistory();
        web.clearFormData();
        web.removeAllViews();

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

                //Log.e("url : ", url);

                if (url.contains("?code=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    authCode = uri.getQueryParameter("code");
                 //   Log.e("", "CODE : " + authCode);
                    authComplete = true;
                    resultIntent.putExtra("code", authCode);
                    Sign_inActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    setResult(Activity.RESULT_CANCELED, resultIntent);

                    utils.setSharedPreference(context,authCode,Constants.OAUTH_CODE);
                    new TokenGet().execute();

                } else if (url.contains("error=access_denied")) {
                   // Log.e("", "ACCESS_DENIED_HERE");
                    resultIntent.putExtra("code", authCode);
                    authComplete = true;
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
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
           // Log.e("token json : ", json.toString());
            pDialog.dismiss();

            try {
                String access_token = json.getString("access_token");
                String token_type = json.getString("token_type");
                String expires_in = json.getString("expires_in");
             //   String refresh = json.getString("refresh_token");

                utils.setSharedPreference(context,access_token,Constants.TOKEN);

             //   Log.e("Token Access", access_token);
            //    Log.e("Expire", expires_in);

        //        web.clearCache(true);
        //        web.clearFormData();

                if (checkConnection()) {
                    new ProcressAsyncTask(
                            Sign_inActivity.this,
                            constants.urls(2),
                            null,
                            null,
                            null,
                            "POST",
                            2,
                            "1.0",
                            null,
                            utils.getSharedPreference(context,Constants.DEVICE_ID),
                            null,
                            null,
                            null,
                            null).execute();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void parseJsonRegisterResponse(final String result) {
        if (result != null) {
          //  Log.e("RegisterResponse : ", result);//1567234
            try {
                JSONObject jsonObjectResult = new JSONObject(result);
                boolean status = jsonObjectResult.getBoolean("status");
                if (status){
                    registerDeviceForAttendance();
                //
                }else{
                    registerDeviceForAttendance();
                //    intent = new Intent(Sign_inActivity.this,MainActivity.class);
                }
            //    startActivity(intent);
            //    finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerDeviceForAttendance(){
        if (checkConnection()) {
            new ProcressAsyncTask(
                    Sign_inActivity.this,
                    constants.urls(5),
                    null,
                    null,
                    null,
                    "GET",4,"1.0",
                    utils.getSharedPreference(context,Constants.TOKEN),
                    utils.getSharedPreference(context,Constants.DEVICE_ID),null,null,null,null).execute();
        }
    }

    public void parseJsonRegisterForAttendandeResponse(final String result) {
        if (result != null) {
         //   Log.e("Attendance Response : ", result);//1567234
            try {
                JSONObject jsonObjectResult = new JSONObject(result);
                boolean status = jsonObjectResult.getBoolean("status");
                JSONObject jsonResult = new JSONObject(jsonObjectResult.getString("result"));
                String userId = jsonResult.getString("id");
                String userEpfNumber = jsonResult.getString("cf_epf_no");
                utils.setSharedPreference(context,userId,Constants.USER_ID);
                utils.setSharedPreference(context,userEpfNumber,Constants.USER_EPF_NO);
                if (status){
                    intent = new Intent(Sign_inActivity.this,MainActivity.class);
                    intent.putExtra("number","1");
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}