package com.rype3.pocket_hrm;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class OauthLogin extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private CoordinatorLayout coordinatorLayout;
    Button user_name,epf_no;
    Intent intent;
    Utils utils;
    Context context;
    private String url;

    //Client id
    private static String CLIENT_ID = "589bf05f277bf8424f3cc6ea";

    //Use your own client secret
    private static String REDIRECT_URI = "http://localhost";
    private static String OAUTH_URL = "http://wmmmendis.rype3.net/human/account/oauth/authorize";
    private static String OAUTH_SCOPE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_login);

        context = this.getApplication();
        utils = new Utils(context);

        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        utils.setSharedPreference(context,android_id,Constants.DEVICE_ID);


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(OauthLogin.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // We do not have this permission. Let's ask the user
                ActivityCompat.requestPermissions(OauthLogin.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 124);
            }

            if (ContextCompat.checkSelfPermission(OauthLogin.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // We do not have this permission. Let's ask the user
                ActivityCompat.requestPermissions(OauthLogin.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 125);
            }
        }else{
            // telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            // Log.e("IMEI NUMBER : ", telephonyManager.getDeviceId() +"\nDevice name : "+telephonyManager.getPhoneType());
            // utils.setSharedPreference(context, getDeviceName(), Constants.DEVICE_ID);
        }

        if (utils.getBoolean(context,Constants.TOKEN)){
            intent = new Intent(OauthLogin.this,MainActivity.class);
            intent.putExtra("number","1");
            startActivity(intent);
            finish();
        }

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        user_name = (Button)findViewById(R.id.button_user);
        user_name.setOnClickListener(onclick);

        epf_no = (Button)findViewById(R.id.button_epf);
        epf_no.setOnClickListener(onclick);
    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (checkConnection()) {
                if (v == user_name) {
                  //  intent = new Intent(OauthLogin.this, Sign_inActivity.class);


                    if(checkConnection()){
                        new HTTPAsyncTask(OAUTH_URL + "?redirect_uri=" + REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID + "&scope=" + OAUTH_SCOPE ).execute();
                    }
                }

                if (v == epf_no) {
                    intent = new Intent(OauthLogin.this, EPFnumberActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

//            case 123: {
////                if (grantResults.length > 0
////                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////
////                    telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
////                    Log.e("IMEI NUMBER : ", telephonyManager.getDeviceId() +"\nDevice name : "+telephonyManager.getPhoneType());
////                    utils.setSharedPreference(context, telephonyManager.getDeviceId(), Constants.DEVICE_ID);
////                    return;
////                }
//
//                utils.setSharedPreference(context,getDeviceName(), Constants.DEVICE_ID);
//            }
//            break;

            case 124:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            break;

            case 125:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            break;
        }
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
          //  ViewMessage("Connection success", 1);
            return true;
        }
        ViewMessage("You don't have internet connection", 0);
        return false;
    }

    public void ViewMessage(String message, int position) {

        switch (position) {
            case 0:
                Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
                sbView.setLayoutParams(params);
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
                break;

            case 1:
                snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView1 = snackbar.getView();
                CoordinatorLayout.LayoutParams params1 = (CoordinatorLayout.LayoutParams) sbView1.getLayoutParams();
                sbView1.setLayoutParams(params1);
                TextView textView1 = (TextView) sbView1.findViewById(android.support.design.R.id.snackbar_text);
                textView1.setTextColor(Color.GREEN);
                snackbar.show();
                break;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private class HTTPAsyncTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ProgressDialog dialog;
        private final static String TAG = "HTTPAsyncTask";

        HTTPAsyncTask(String url) {
            super();
            this.url = url;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(OauthLogin.this);
            dialog.setTitle("Please wait...");
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return loadJSON(
                        this.url).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            dialog.dismiss();
            Log.e("RESULT : " , String.valueOf(result));

            if (result.equals("200")){
                intent = new Intent(OauthLogin.this, Sign_inActivity.class);
                startActivity(intent);
                finish();
            }else{
                ViewMessage("Service unavailable. Try again in few minutes.", 0);
            }
        }

        private Integer loadJSON(String url) {
            // Creating JSON Parser instance
            JSONParser jParser = new JSONParser();
            // getting JSON string from URL
            return jParser.getJSONFromUrl(url);
        }

        private class JSONParser {
            int statusCode;
            // constructor
            JSONParser() {
            }

            Integer getJSONFromUrl(String url) {
              //  Log.e("URL ", url);
                // Making HTTP POST request
                        try {
                            // defaultHttpClient
                            DefaultHttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost(url);

                            try {
                                List<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();
                                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            HttpResponse httpResponse = httpClient.execute(httpPost);
                            HttpEntity httpEntity = httpResponse.getEntity();

                            StatusLine statusLine = httpResponse.getStatusLine();
                            statusCode = statusLine.getStatusCode();

                           // Log.e(TAG, " Status code : " + String.valueOf(statusCode));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        try {
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
//                            StringBuilder sb = new StringBuilder();
//                            String line = null;
//                            while ((line = reader.readLine()) != null) {
//                                sb.append(line).append("\n");
//                            }
//                            is.close();
//                            json = sb.toString();
//                        } catch (Exception e) {
//                            Log.e("Buffer Error", "Error converting result " + e.toString());
//                        }
//
//                        // try parse the string to a JSON object
//                        try {
//                            jObj = new JSONObject(json);
//                        } catch (JSONException e) {
//                            Log.e("JSON Parser", "Error parsing data " + e.toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                return statusCode;
            }
        }
    }
}
