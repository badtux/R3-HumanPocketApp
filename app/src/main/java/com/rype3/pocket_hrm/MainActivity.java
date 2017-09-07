package com.rype3.pocket_hrm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private TextView textView_count;
    private AutoCompleteTextView location;
    private Button  btn_in,
                    btn_out;
                   // btn_exit,
                  //  btn_help;
    long MillisecondTime, StartTime, TimeBuff, timestamp, UpdateTime = 0L;
    Handler handler;
    int     hour,
            Seconds,
            Minutes,
            MilliSeconds;
    private Context context;
    private Utils utils;
    LocationManager locationManager;
    protected GoogleApiClient googleApiClient;
    private CoordinatorLayout coordinatorLayout;
    private Constants constants;
    boolean newAccount = false;
    private ImageView image_in ,image_out;
    private RelativeLayout relativeLayout_checkout,relativeLayout_checkin;
    private static final long SYNC_FREQUENCY = 2;  // 1 hour (in seconds)

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.rype3.mendischecking";

    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        widget();
        toolbar();

        buildGoogleApiClient();

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        context = this.getApplicationContext();
        utils = new Utils(context);
        constants = new Constants(context);

        timestamp = System.currentTimeMillis() / 1000;

        handler = new Handler();

        Realm myRealm = Realm.getInstance(this);

        myRealm.beginTransaction();
        myRealm.where(Location_object.class).equalTo("syncState", true).findAll().clear();
        myRealm.commitTransaction();

        utils.setSharedPreference(context, getDeviceName(), Constants.DEVICE_NAME);

        try {
            if (utils.getBoolean(context, Constants.EXIT_STATAUS)) {
                boolean exit_state = Boolean.parseBoolean(utils.getSharedPreference(context, Constants.EXIT_STATAUS));

                if (exit_state) {

                    final int sdk = Build.VERSION.SDK_INT;
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2_click));
                        relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3));
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            relativeLayout_checkin.setBackground(getResources().getDrawable(R.drawable.button_background_2_click));
                            relativeLayout_checkout.setBackground(getResources().getDrawable(R.drawable.button_background_3));
                        }
                    }

                    blinkImage(image_out ,0);

                    StartTime = Long.parseLong(utils.getSharedPreference(context, Constants.START_TIMESTAMP));
                    handler.postDelayed(runnable, 0);

                    location.setText(utils.getSharedPreference(context, Constants.LOCATION));
                    btn_in.setClickable(false);
                } else {
                    utils.setSharedPreference(context, "", Constants.CHECKED_STATE);
                    utils.setSharedPreference(context, null, Constants.LOCATION);
                    utils.setSharedPreference(context, "0L", Constants.START_TIMESTAMP);

                    btn_in.setClickable(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            btn_in.setBackground(getResources().getDrawable(R.drawable.button_background_2));
            utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);
        }

        syncMethod();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Intent intent = new Intent(MainActivity.this, MyLocationListner.class);
        intent.putExtra("name", "LOCATION_LISTNER");
        startService(intent);
    }


    public void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setSubtitle("");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_m);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void widget(){
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        textView_count = (TextView) findViewById(R.id.textView_count);
        image_in = (ImageView) findViewById(R.id.iv_icon);
        image_out = (ImageView) findViewById(R.id.iv_icon_1);

        relativeLayout_checkout = (RelativeLayout) findViewById(R.id.relative_out);
        relativeLayout_checkin = (RelativeLayout) findViewById(R.id.relative_in);

        btn_in = (Button) findViewById(R.id.button_in);
        btn_in.setOnClickListener(onclick);

        btn_out = (Button) findViewById(R.id.button_out);
        btn_out.setOnClickListener(onclick);

//        btn_exit = (Button) findViewById(R.id.button_exit);
//        btn_exit.setOnClickListener(onclick);
//
//        btn_help = (Button) findViewById(R.id.button_help);
//        btn_help.setOnClickListener(onclick);

        location = (AutoCompleteTextView) findViewById(R.id.autoComplete_location);
    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == btn_in) {
                if (validation()) {
                    if (reset()) {
                        hideKeyBoard();

                        final int sdk = Build.VERSION.SDK_INT;
                        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                           // btn_in.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2_click));
                            relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2_click));
                            relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3));
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                          //  btn_in.setBackground(getResources().getDrawable(R.drawable.button_background_2_click));
                            relativeLayout_checkin.setBackground(getResources().getDrawable(R.drawable.button_background_2_click));
                            relativeLayout_checkout.setBackground(getResources().getDrawable(R.drawable.button_background_3));
                            }
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_check, getApplicationContext().getTheme()));
                        } else {
                            image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                        }

                        blinkImage(image_out ,0);

                        markAttendance(0, IOStime());
                        utils.setSharedPreference(context, location.getText().toString(), Constants.LOCATION);
                        utils.setSharedPreference(context, "in", Constants.CHECKED_STATE);
                        utils.setSharedPreference(context, "true", Constants.EXIT_STATAUS);
                        StartTime = SystemClock.uptimeMillis();
                        handler.postDelayed(runnable, 0);
                        utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);

                        btn_in.setClickable(false);
                    }
                }
            }

            if (v == btn_out) {
                if (!location.getText().toString().isEmpty()) {

//                    final int sdk = Build.VERSION.SDK_INT;
//                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
//                        btn_out.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3));
//                        btn_in.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
//                    } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            btn_out.setBackground(getResources().getDrawable(R.drawable.button_background_3));
//                            btn_in.setBackground(getResources().getDrawable(R.drawable.button_background_2));
//                        }
//                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next, getApplicationContext().getTheme()));
                    } else {
                        image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next));
                    }

                    blinkImage(image_out ,1);

                    thankyouAlertMessageBox(utils.getSharedPreference(context, Constants.LAST_TIME));
                }
            }
//            if (v == btn_exit){
//                if (!location.getText().toString().isEmpty()) {
//                    exitAlertMessageBox(1);
//                }
//            }
//
//            if (v == btn_help){
//                if(checkConnection()){
//                    TriggerRefresh();
//                }
//            }
        }
    };

    private void blinkImage(ImageView image ,int type){
        final Animation animation = new AlphaAnimation(1, 0);

        switch (type) {

            case 0:
                animation.setDuration(1000);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.REVERSE);
                image.startAnimation(animation);
                break;

            case 1:
                animation.setDuration(1000);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(Animation.INFINITE);
                animation.setRepeatMode(Animation.REVERSE);
                image.startAnimation(animation);
                animation.cancel();
                break;
        }
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            return true;
        }
        return false;
    }

    private boolean syncMethod() {
        boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_SETUP_COMPLETE, false);
        Account account = AuthenticatorService.GetAccount();

        AccountManager accountManager = (AccountManager) this.getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.

            Bundle bundle = new Bundle();
            bundle.putString("time", String.valueOf(StartTime));

            ContentResolver.addPeriodicSync(account, AUTHORITY, bundle, SYNC_FREQUENCY);
            newAccount = true;
        }

        if (newAccount || !setupComplete) {
            TriggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREF_SETUP_COMPLETE, true).apply();
        }
        return true;
    }

    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        b.putString("number","1");

        ContentResolver.requestSync(
                AuthenticatorService.GetAccount(),      // Sync account
                null, // Content authority
                b);                                      // Extras
    }

    private void resetData(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final int sdk = Build.VERSION.SDK_INT;
                if(sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    //btn_out.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3) );
                  //  btn_in.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2) );

                    relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
                    relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3_click));

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                       // btn_out.setBackground(getResources().getDrawable(R.drawable.button_background_3));
                       // btn_in.setBackground(getResources().getDrawable(R.drawable.button_background_2));

                        relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
                        relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3_click));
                    }
                }

                utils.setSharedPreference(context, "", Constants.CHECKED_STATE);
                utils.setSharedPreference(context,null,Constants.LOCATION);
                utils.setSharedPreference(context,"",Constants.START_TIMESTAMP);
                location.setText("");
                textView_count.setText("00:00:00");
                btn_in.setClickable(true);

                blinkImage(image_out ,1);
            }
        }, 10000);
    }

    public boolean reset() {
        MillisecondTime = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        Seconds = 0;
        Minutes = 0;
        MilliSeconds = 0;

        return true;
    }

    public String IOStime(){
        String ISOtime;
        DateFormat df;
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.UK);
        ISOtime = df.format(new Date());
        return ISOtime;
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            hour = Seconds / (60 * 60);

            Minutes = (Seconds / 60) % 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            textView_count.setText(hour + ":" + String.format("%02d",Minutes) + ":" + String.format("%02d", Seconds));

            utils.setSharedPreference(context, (hour + ":" + String.format("%02d",Minutes) + ":" + String.format("%02d", Seconds)), Constants.LAST_TIME);

            handler.postDelayed(this, 0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (utils.getBoolean(context, Constants.CHECKED_STATE)) {
                if (utils.getSharedPreference(context, Constants.CHECKED_STATE).equals("in")) {
                    exitAlertMessageBox(0);
                } else {
                    onBackPressed();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitAlertMessageBox(int type) {

        switch (type){
            case 0:
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exit?")
                        .setMessage("It's recommended to keep the APP running in the background. Do you really want to exit?")
                        .setPositiveButton("Yes! Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                onBackPressed();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create();
                dialog.show();
                break;

            case 1:
                final android.app.AlertDialog dialog_1 = new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Exit?")
                        .setMessage("It's recommended to keep the APP running in the background. Do you really want to exit?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                onBackPressed();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create();
                dialog_1.show();
                break;
        }

    }

    private void thankyouAlertMessageBox(String time) {

                String msg = "You have spent " + time + " Hrs at this location. Do you want to record the exit?";
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Please Confirm")
                        .setMessage(msg)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                markAttendance(1, IOStime());
                                utils.setSharedPreference(context, "out", Constants.CHECKED_STATE);
                                utils.setSharedPreference(context, "false", Constants.EXIT_STATAUS);
                                resetData();
                                TimeBuff += MillisecondTime;
                                handler.removeCallbacks(runnable);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                onBackPressed();
                            }
                        }).create();
                dialog.show();

    }

    public void parseJsonResponse(final String result) {
        if (result != null) {
            Log.e("Result : ", result);
            try {
                JSONObject jsonObjectResult = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
    /* Class Single Location_object Listner  */

    public boolean validation() {
        String String_location = location.getText().toString();

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
            startActivity(getIntent());
            return false;
        }

        if (String_location.isEmpty()){
            ViewMessage("Location is required", 0);
            return false;
        }
        return true;
    }

    private void hideKeyBoard(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected synchronized void buildGoogleApiClient() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        MainActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected){
           TriggerRefresh();
        }
    }

    public void ViewMessage(String message, int position) {

        switch (position) {
            case 0:
                Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
                params.gravity = Gravity.TOP;
                sbView.setLayoutParams(params);
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
                break;

            case 1:
                snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView1 = snackbar.getView();
                CoordinatorLayout.LayoutParams params1 = (CoordinatorLayout.LayoutParams) sbView1.getLayoutParams();
                params1.gravity = Gravity.TOP;
                sbView1.setLayoutParams(params1);
                TextView textView1 = (TextView) sbView1.findViewById(android.support.design.R.id.snackbar_text);
                textView1.setTextColor(Color.GREEN);
                snackbar.show();
                break;
        }
    }

    public boolean markAttendance(int position,String time){
          if (checkConnection()) {

             switch (position) {
                 case 0:
                     utils.setSharedPreference(context,String.valueOf(getBatteryPercentage(context))+"%",Constants.BATTERY_LEVEL);
                 new ProcressAsyncTask(
                         MainActivity.this,
                         constants.urls(3),
                         null,
                         null,
                         null,
                         "POST", 3, "1.0", null, null, utils.getSharedPreference(context, Constants.USER_ID), time,null,String.valueOf(meta(utils.getSharedPreference(context, Constants.DEVICE_ID),String.valueOf(getBatteryPercentage(context))+"%",utils.getSharedPreference(context,Constants.DEVICE_NAME)))).execute();
                     break;

                 case 1:
                     utils.setSharedPreference(context,String.valueOf(getBatteryPercentage(context))+"%",Constants.BATTERY_LEVEL);
                     new ProcressAsyncTask(
                             MainActivity.this,
                             constants.urls(4),
                             null,
                             null,
                             null,
                             "POST", 3, "1.0", null, null, utils.getSharedPreference(context, Constants.USER_ID), null,time,String.valueOf(meta(utils.getSharedPreference(context, Constants.DEVICE_ID),String.valueOf(getBatteryPercentage(context))+"%",utils.getSharedPreference(context,Constants.DEVICE_NAME)))).execute();
                     break;
             }
        }
        return true;
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

    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkinout_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_in_out) {
            Intent intent = new Intent(getBaseContext(), LeaveActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public JSONObject meta(String did,String battery,String deviceName){
        JSONObject jsonObject = null;
        String lat = "";
        String lon = "";

        jsonObject = new JSONObject();
        try {
            jsonObject.put("did",did);
            jsonObject.put("bat",battery);
            jsonObject.put("d_name",deviceName);

            JSONObject jsonObjectLocation = new JSONObject();

            if (utils.getBoolean(context,Constants.LAT)){
                lat = utils.getSharedPreference(context,Constants.LAT);
            }

            if (utils.getBoolean(context,Constants.LONG)){
                lon = utils.getSharedPreference(context,Constants.LONG);
            }
            jsonObjectLocation.put("lat" , lat);
            jsonObjectLocation.put("long" , lon);

            jsonObject.put("location", jsonObjectLocation);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    @Override
    protected void onResume () {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);

        if(checkConnection()){
            TriggerRefresh();
        }
    }
}
