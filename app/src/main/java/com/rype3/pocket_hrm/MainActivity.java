package com.rype3.pocket_hrm;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rype3.pocket_hrm.realm.LocationDetails;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ConnectivityReceiver.ConnectivityReceiverListener,
        PlaceAutocompleteAdapter.PlaceAutoCompleteInterface,
        View.OnClickListener {
    private TextView textView_count, version;
    private EditText location;
    private Button btn_in, btn_out;

    long onDutyInMiliSeconds,
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
    private ImageView image_in, image_out;
    private Button btn_clear;
    private RelativeLayout relativeLayout_checkout, relativeLayout_checkin;

    private static final String LOG_TAG = "MainActivity";

    private DataSave dataSave;
    private Intent intent;
    private String number;
    //  private DataSave getDataSave;
    // Constants
    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    PlaceAutocompleteAdapter mAdapter;
    AutocompleteFilter typeFilter;
    private RealmResults<LocationDetails> locationList;
    public ArrayList<Integer> id_list;
    private  Location lastLocation;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(6.0083849,80.3094838), new LatLng(9.8014351,80.2087608));

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        widget();

        buildGoogleApiClient();

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }

        if (checkConnection()) {
            BuildGoogleService();
        }

        typeFilter = new AutocompleteFilter.Builder()
                .setCountry("LK")
                .build();

        context = this.getApplicationContext();

        utils = new Utils(context);

        timestamp = System.currentTimeMillis() / 1000;

        handler = new Handler();

        myRealm = Realm.getDefaultInstance();

        dataSave = new DataSave(context, utils);

        version.setText(dataSave.Version(context));

        id_list = new ArrayList<>();

        intent = getIntent();
        number = intent.getStringExtra("number");

        if (utils.getBoolean(context, Constants.EXIT_STATAUS)) {
            boolean exit_state = Boolean.parseBoolean(utils.getSharedPreference(context, Constants.EXIT_STATAUS));
            AppState(exit_state);
        } else {
            utils.setSharedPreference(context, "", Constants.GEO_LATLONG);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // now get the lat/lon from the location and do something with it.
            if (lastLocation != null) {
                utils.setSharedPreference(context, String.valueOf(lastLocation.getLatitude()), Constants.LAT);
                utils.setSharedPreference(context, String.valueOf(lastLocation.getLongitude()), Constants.LONG);

//                String provider = locationManager.getBestProvider(new Criteria(), true);
//
//                Location locations = locationManager.getLastKnownLocation(provider);
//                List<String> providerList = locationManager.getAllProviders();
//                if (null != locations && null != providerList && providerList.size() > 0) {
//                    double longitude = locations.getLongitude();
//                    double latitude = locations.getLatitude();
//                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//                    try {
//                        List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
//
//                        if (null != listAddresses && listAddresses.size() > 0) {
//                            String _Location = listAddresses.get(0).getAddressLine(0);
//                           // Log.e("TAG location : ", _Location);
//
//                            location.setHint("Near by : "+_Location);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                } else {
                    Log.e("TAG ", " null lat/long");
                    utils.setSharedPreference(context, String.valueOf(0), Constants.LAT);
                    utils.setSharedPreference(context, String.valueOf(0), Constants.LONG);
                }
            }


        try {
            mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch, googleApiClient, BOUNDS_MOUNTAIN_VIEW, typeFilter);
            mRecyclerView.setAdapter(mAdapter);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    //  if (!exitState()) {

                    btn_clear.setVisibility(View.VISIBLE);
                    relativeLayout_checkin.setVisibility(View.GONE);
                    relativeLayout_checkout.setVisibility(View.GONE);
                    textView_count.setVisibility(View.GONE);

                    //  }
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                    }

//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            relativeLayout_checkin.setVisibility(View.VISIBLE);
//                            relativeLayout_checkout.setVisibility(View.VISIBLE);
//                            textView_count.setVisibility(View.VISIBLE);
//                            mRecyclerView.setAdapter(null);
//                        }
//                    }, 10 *1000);
                } else if (count == 0) {
                    if (mAdapter != null) {
                        relativeLayout_checkin.setVisibility(View.VISIBLE);
                        relativeLayout_checkout.setVisibility(View.VISIBLE);
                        textView_count.setVisibility(View.VISIBLE);
                        mRecyclerView.setAdapter(null);
                    } else {
//                        if (!exitState()) {
//
//                            relativeLayout_checkin.setVisibility(View.VISIBLE);
//                            relativeLayout_checkout.setVisibility(View.VISIBLE);
//                            textView_count.setVisibility(View.VISIBLE);
//                            btn_clear.setVisibility(View.GONE);
//                        }
                    }
                }
                if (!s.toString().equals("") && googleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!googleApiClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e("", "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                  /* Write your logic here that will be executed when user taps next button */

                    relativeLayout_checkin.setVisibility(View.VISIBLE);
                    relativeLayout_checkout.setVisibility(View.VISIBLE);
                    textView_count.setVisibility(View.VISIBLE);
                    btn_clear.setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMenuResource() {
        return R.menu.checkinout_main;
    }

    @Override
    protected String ToolBarName() {
        return "";
    }

    @Override
    protected int ToolBarIcon() {
        return R.mipmap.ic_launcher_m;
    }

    @Override
    protected String Number() {
        return number;
    }

    private void widget() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        textView_count = (TextView) findViewById(R.id.textView_count);
        version = (TextView) findViewById(R.id.app_version);
        image_in = (ImageView) findViewById(R.id.iv_icon);
        image_out = (ImageView) findViewById(R.id.iv_icon_1);
        btn_clear = (Button) findViewById(R.id.clear);

        relativeLayout_checkout = (RelativeLayout) findViewById(R.id.relative_out);
        relativeLayout_checkin = (RelativeLayout) findViewById(R.id.relative_in);

        btn_in = (Button) findViewById(R.id.button_in);
        btn_in.setOnClickListener(onclick);

        btn_out = (Button) findViewById(R.id.button_out);
        btn_out.setOnClickListener(onclick);

        location = (EditText) findViewById(R.id.et_location);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(llm);

    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == btn_in) {
                if (validation()) {
                    hideKeyBoard();
                    if (reset()) {
//                        intent = new Intent(MainActivity.this, MyLocationListner.class);
//                        intent.putExtra("name", "START SETVICE");
//                        startService(intent);
                        btn_clear.setClickable(false);
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

                        dataSave.blinkIcon(image_out, 0);

                        long id = System.currentTimeMillis();

                        dataSave.DataSave(
                                myRealm,
                                id,
                                "in",
                                utils.getSharedPreference(context, Constants.DEVICE_ID),
                                "attendance",
                                true,
                                location.getText().toString(),
                                utils.getSharedPreference(context, Constants.USER_EPF_NO),
                                utils.getSharedPreference(context, Constants.USER_ID),
                                dataSave.Version(context));

                        utils.setSharedPreference(context, location.getText().toString(), Constants.LOCATION);
                        utils.setSharedPreference(context, "in", Constants.CHECKED_STATE);
                        utils.setSharedPreference(context, "true", Constants.EXIT_STATAUS);
                        StartTime = System.currentTimeMillis();
                        handler.postDelayed(runnable, 0);
                        utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);

                        btn_in.setClickable(false);
                        TriggerRefresh("1", id_list());
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

                    dataSave.blinkIcon(image_out, 1);

                    thankyouAlertMessageBox(utils.getSharedPreference(context, Constants.LAST_TIME));
                }
            }

            if (v == btn_clear){
                location.setText("");
            }
        }
    };

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            return true;
        } else {
            ViewMessage("You don't have internet connection", 0);
        }
        return false;
    }

    public static void TriggerRefresh(String num, ArrayList<Integer> id_list) {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        b.putString("number", num);
        b.putString("id_list", String.valueOf(id_list));

        ContentResolver.requestSync(
                AuthenticatorService.GetAccount(),      // Sync account
                null, // Content authority
                b);                                      // Extras
    }

    private void resetData() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
                    relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3_click));

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
                        relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3_click));
                    }
                }

                utils.setSharedPreference(context, "", Constants.CHECKED_STATE);
                utils.setSharedPreference(context, null, Constants.LOCATION);
                utils.setSharedPreference(context, "", Constants.START_TIMESTAMP);
                location.setText("");
                textView_count.setText("00:00:00");
                btn_in.setClickable(true);
                btn_clear.setClickable(true);
                dataSave.blinkIcon(image_out, 1);

                TriggerRefresh("1", id_list());
            }
        }, 5000);
    }

    public boolean reset() {
        onDutyInMiliSeconds = 0L;
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

            onDutyInMiliSeconds = System.currentTimeMillis() - (StartTime);

            String time_spent = getTimeSpentString(onDutyInMiliSeconds);

            textView_count.setText(time_spent);

            utils.setSharedPreference(context, time_spent, Constants.LAST_TIME);

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

        switch (type) {
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


                        long id_ = System.currentTimeMillis();
                        dataSave.DataSave(
                                myRealm,
                                id_,
                                "out",
                                utils.getSharedPreference(context, Constants.DEVICE_ID),
                                "attendance",
                                true,
                                location.getText().toString(),
                                utils.getSharedPreference(context, Constants.USER_EPF_NO),
                                utils.getSharedPreference(context, Constants.USER_ID), dataSave.Version(context));

                        utils.setSharedPreference(context, "out", Constants.CHECKED_STATE);
                        utils.setSharedPreference(context, "false", Constants.EXIT_STATAUS);
                        TriggerRefresh("1", id_list());
                        resetData();
                        //   TimeBuff += MillisecondTime;
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

            Log.e("TAG", GooglePlayServicesUtil.getErrorDialog(status, this, 0).toString());
            return false;
        }
    }
    /* Class Single Location_object Listner */

    public boolean validation() {
        String String_location = location.getText().toString();

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
            startActivity(getIntent());
            return false;
        }

        if (String_location.isEmpty()) {
            ViewMessage("Location is required", 0);
            return false;
        }
        return true;
    }

    private void hideKeyBoard() {
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
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); //this is the key ingredient
            // **************************

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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            TriggerRefresh(number, id_list());
        } else {
            TriggerRefresh(null, null);
        }
    }

    public void ViewMessage(String message, int position) {

        switch (position) {
            case 0:
                Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) sbView.getLayoutParams();
                params.gravity = Gravity.BOTTOM;
                sbView.setLayoutParams(params);
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.RED);
                snackbar.show();
                break;

            case 1:
                snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView1 = snackbar.getView();
                CoordinatorLayout.LayoutParams params1 = (CoordinatorLayout.LayoutParams) sbView1.getLayoutParams();
                params1.gravity = Gravity.BOTTOM;
                sbView1.setLayoutParams(params1);
                TextView textView1 = (TextView) sbView1.findViewById(android.support.design.R.id.snackbar_text);
                textView1.setTextColor(Color.GREEN);
                snackbar.show();
                break;
        }
    }

    private boolean BuildGoogleService() {
        googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
        if (checkConnection()) {
            TriggerRefresh(number, id_list());
        } else {
            TriggerRefresh(null, null);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_clear) {
            location.setText("");
            if (mAdapter != null) {
                mAdapter.clearList();
            }
        }
    }

    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, final int position) {

        if (mResultList != null) {
            try {
                if (mResultList.size() != 0) {
                    final String placeId = String.valueOf(mResultList.get(position).placeId);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                    location.setText(mResultList.get(position).primery);
//                    utils.setSharedPreference(context, String.valueOf(mResultList.get(position).placeId), Constants.GEO_LATLONG);
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(null);
                        if (!exitState()) {
                            relativeLayout_checkin.setVisibility(View.VISIBLE);
                            relativeLayout_checkout.setVisibility(View.VISIBLE);
                            textView_count.setVisibility(View.VISIBLE);
                        }
                    }

//                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
//                    placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
//                        @Override
//                        public void onResult(@NonNull PlaceBuffer places) {
//
//                            if (!places.getStatus().isSuccess()) {
//                                 // Request did not complete successfully
//                                Log.e("TAG", "Place query did not complete. Error: " + places.getStatus().toString());
//                                places.release();
//                                return;
//                            }else {
//
//                            }
//                        }
//                    });

//                    private ResultCallback<PlaceBuffer> fromPlaceResultCallback = new ResultCallback<PlaceBuffer>() {
//                        @Override
//                        public void onResult(@NonNull PlaceBuffer places) {
//                            if (!places.getStatus().isSuccess()) {
//                                places.release();
//                                return;
//                            }
//                            // Get the Place object from the buffer.
//                            if(places.getCount() > 0) {
//                                // Got place details
//                                Place place = places.get(0);
//                                // Do your stuff
//                            } else {
//                                // No place details
//                                Toast.makeText(MainActivity.this, "Place details not found.", Toast.LENGTH_LONG).show();
//                            }
//                            places.release();
//                        }
//
//                } else {
//                    Log.e("TAG", "NOT At que");

                    Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        final Place myPlace = places.get(0);
                                       // Log.e("TAG", "Place found: " + myPlace.getName());
                                        utils.setSharedPreference(context, String.valueOf(myPlace.getLatLng()), Constants.GEO_LATLONG);
                                    } else {
                                        Log.e("TAG", "Place not found");
                                    }
                                    places.release();
                                }
                            });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void AppState(boolean state) {
        try {
            if (state) {
                if (exitState()) {
                    relativeLayout_checkin.setVisibility(View.VISIBLE);
                    relativeLayout_checkout.setVisibility(View.VISIBLE);
                    textView_count.setVisibility(View.VISIBLE);
                }
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

                dataSave.blinkIcon(image_out, 0);

                StartTime = Long.parseLong(utils.getSharedPreference(context, Constants.START_TIMESTAMP));
                handler.postDelayed(runnable, 0);

                location.setText(utils.getSharedPreference(context, Constants.LOCATION));
                btn_in.setClickable(false);
                btn_clear.setClickable(false);
            } else {
                utils.setSharedPreference(context, "", Constants.CHECKED_STATE);
                utils.setSharedPreference(context, null, Constants.LOCATION);
                utils.setSharedPreference(context, "0L", Constants.START_TIMESTAMP);

                btn_in.setClickable(true);
                btn_clear.setClickable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            btn_in.setBackground(getResources().getDrawable(R.drawable.button_background_2));
            utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);
        }
    }

    private String getTimeSpentString(long diff) {
        String time = "";
        try {
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);


            time = String.format("%02d", diffHours) + ":" + String.format("%02d", diffMinutes) + ":" + String.format("%02d", diffSeconds);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    private ArrayList<Integer> id_list(){
        id_list.clear();

        locationList = myRealm.where(LocationDetails.class).equalTo("state", true).findAll();

        if (locationList != null) {
            for (int i = 0; i < locationList.size(); i++) {
                id_list.add(locationList.get(i).getId());
            }
        } else {
            id_list.add(null);
        }
        return id_list;
    }
}
