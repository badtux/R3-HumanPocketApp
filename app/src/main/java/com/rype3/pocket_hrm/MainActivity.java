package com.rype3.pocket_hrm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private TextView textView_count;
    private AutoCompleteTextView location;
    private Button  btn_in,
                    btn_out;

    long   MillisecondTime,
           StartTime,
           TimeBuff,
           timestamp,
           UpdateTime = 0L;

    Handler handler;

    int hour,
        Seconds,
        Minutes,
        MilliSeconds;

    private Realm myRealm;
    private Context context;
    private Utils utils;
    LocationManager locationManager;
    protected GoogleApiClient googleApiClient;
    private CoordinatorLayout coordinatorLayout;
    private Constants constants;
    boolean newAccount = false;
    private ImageView image_in ,image_out;
    private RelativeLayout relativeLayout_checkout, relativeLayout_checkin;
    private static final long SYNC_FREQUENCY = 2;  // 1 hour (in seconds)

    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private DataSave dataSave;
  //  private DataSave getDataSave;
    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.rype3.mendischecking";

    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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

        myRealm = Realm.getDefaultInstance();

       // getDataSave = new DataSave();
        dataSave = new DataSave(context,utils);

        if (checkConnection()){
            BuildGoogleService();
        }

        location.setThreshold(3);

        location.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        location.setAdapter(mPlaceArrayAdapter);

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
            }else{
                utils.setSharedPreference(context, "",Constants.GEO_LATLONG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            btn_in.setBackground(getResources().getDrawable(R.drawable.button_background_2));
            utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Intent intent = new Intent(MainActivity.this, MyLocationListner.class);
        intent.putExtra("name", "LOCATION_LISTNER");
        startService(intent);

        syncMethod();

        if(checkConnection()){
            TriggerRefresh();
        }
//        Intent i= new Intent(context, NetWatcher.class);
//        i.putExtra("KEY1", "Value to be used by the service");
//        context.startService(i);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.e(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.e(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
           final Place place = places.get(0);

            Log.e(LOG_TAG, "Place latlong : " + place.getLatLng());
            utils.setSharedPreference(context, String.valueOf(place.getLatLng()),Constants.GEO_LATLONG);
//            CharSequence attributions = places.getAttributions();
//            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
//            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
//            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
//            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
//            mWebTextView.setText(place.getWebsiteUri() + "");
//            if (attributions != null) {
//                mAttTextView.setText(Html.fromHtml(attributions.toString()));
//            }
        }
    };

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
                            relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2_click));
                            relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3));
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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

                        long id = System.currentTimeMillis() / 1000;

                        dataSave.DataSave(myRealm,id,"in", utils.getSharedPreference(context, Constants.DEVICE_ID),"attendance",true,location.getText().toString());
                       // markAttendance(0,location.getText().toString());

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next, getApplicationContext().getTheme()));
                    } else {
                        image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next));
                    }

                    blinkImage(image_out ,1);

                    thankyouAlertMessageBox(utils.getSharedPreference(context, Constants.LAST_TIME));
                }
            }
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
        } else {
            ViewMessage("You don't have internet connection",0);
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
                    relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
                    relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3_click));

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
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
        }, 5000);
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
                           //     markAttendance(1,location.getText().toString());

                            long id_ = System.currentTimeMillis() / 1000;
                            dataSave.DataSave(myRealm,id_,"in", utils.getSharedPreference(context, Constants.DEVICE_ID),"attendance",true,location.getText().toString());

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

            Log.e("TAG" , GooglePlayServicesUtil.getErrorDialog(status, this, 0).toString());
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

        mPlaceArrayAdapter.setGoogleApiClient(googleApiClient);
        Log.e(LOG_TAG, "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {

        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this, "Google Places API connection failed with error code:"
                + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();

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

   // public boolean markAttendance(long id,String checkState,String deviceId,String type,boolean state,String location,int battery){
    public boolean markAttendance(int position ,String location){

          if (checkConnection()) {

             switch (position) {
                 case 0:
                     utils.setSharedPreference(context,String.valueOf(dataSave.getBatteryPercentage(context))+"%",Constants.BATTERY_LEVEL);
                    new ProcressAsyncTask(
                         MainActivity.this,
                         constants.urls(3),
                         null,
                         null,
                         null,
                         "POST",
                         3,
                         "1.0",
                         null,
                         null,
                         utils.getSharedPreference(context, Constants.USER_ID),
                            dataSave.IOStime(),
                         null,
                         String.valueOf(
                                 dataSave.meta(
                                         utils.getSharedPreference(context, Constants.DEVICE_ID),
                                         String.valueOf(dataSave.getBatteryPercentage(context))+"%",
                                         utils.getSharedPreference(context,Constants.DEVICE_NAME),
                                         location,dataSave.IOStime()))).execute();
                     break;

                 case 1:
                     utils.setSharedPreference(context,String.valueOf(dataSave.getBatteryPercentage(context))+"%",Constants.BATTERY_LEVEL);
                     new ProcressAsyncTask(
                             MainActivity.this,
                             constants.urls(4),
                             null,
                             null,
                             null,
                             "POST",
                             3,
                             "1.0",
                             null,
                             null,
                             utils.getSharedPreference(context, Constants.USER_ID),
                             null,
                             dataSave.IOStime(),
                             String.valueOf(
                                     dataSave.meta(
                                             utils.getSharedPreference(context, Constants.DEVICE_ID),
                                             String.valueOf(dataSave.getBatteryPercentage(context))+"%",
                                             utils.getSharedPreference(context,Constants.DEVICE_NAME),
                                             location,dataSave.IOStime()))).execute();
                     break;
             }
        }
        return true;
    }

    private boolean BuildGoogleService(){
        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        return true;
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

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);

        if(checkConnection()){
            TriggerRefresh();
        }
    }
}
