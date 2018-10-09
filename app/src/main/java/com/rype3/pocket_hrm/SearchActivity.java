package com.rype3.pocket_hrm;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

import io.realm.Realm;

public class SearchActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ConnectivityReceiver.ConnectivityReceiverListener,
        PlaceAutocompleteAdapter.PlaceAutoCompleteInterface,
        View.OnClickListener{

    private static final String LOG_TAG = "Search Activity";

    protected GoogleApiClient googleApiClient;
    private CoordinatorLayout coordinatorLayout;
    private Button btn_clear;
    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    private Context mContext;
    private Utils utils;
    PlaceAutocompleteAdapter mAdapter;
    private EditText locationEditText;
    AutocompleteFilter typeFilter;
    private Location lastLocation;
    LocationManager locationManager;
    LinearLayout clear_layout;
    private Realm myRealm;
    Intent intent;
    private PocketHr pocketHr;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        widget();

        mContext = this.getApplicationContext();
        utils = new Utils(mContext);

        myRealm = Realm.getDefaultInstance();

        pocketHr = new PocketHr(mContext,utils);

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
            // now get the lat/lon from the locationEditText and do something with it.
            if (lastLocation != null) {
                utils.setSharedPreference(mContext, String.valueOf(lastLocation.getLatitude()), Constants.LAT);
                utils.setSharedPreference(mContext, String.valueOf(lastLocation.getLongitude()), Constants.LONG);

                pocketHr.DataSave(
                        null,
                        myRealm,
                        utils.getSharedPreference(mContext, Constants.CHECKED_STATE),
                        "location",
                        true,
                        utils.getSharedPreference(mContext, Constants.LOCATION));

            } else {
                Log.e("TAG ", " null lat/long");
                utils.setSharedPreference(mContext, String.valueOf(0), Constants.LAT);
                utils.setSharedPreference(mContext, String.valueOf(0), Constants.LONG);
            }
        }

        try {
            mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch, googleApiClient, BOUNDS_MOUNTAIN_VIEW, typeFilter);
            mRecyclerView.setAdapter(mAdapter);
        }catch (Exception e){
            e.printStackTrace();
            PocketHr.setToast(mContext,e.getMessage(),Toast.LENGTH_SHORT);
        }

        locationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!s.toString().equals("") && googleApiClient.isConnected()) {
                    mAdapter.getFilter().filter(s.toString());
                } else if (!googleApiClient.isConnected()) {
                    Log.e("TAG : ", "NOT CONNECTED");
                }else if (googleApiClient.isConnected()){
                    mAdapter = new PlaceAutocompleteAdapter(SearchActivity.this, R.layout.view_placesearch, googleApiClient, BOUNDS_MOUNTAIN_VIEW, typeFilter);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                 if (s.length() > 0){
//                    if (googleApiClient.isConnected()) {
//                        mAdapter = new PlaceAutocompleteAdapter(SearchActivity.this, R.layout.view_placesearch, googleApiClient, BOUNDS_MOUNTAIN_VIEW, typeFilter);
//                        mRecyclerView.setAdapter(mAdapter);
//                    }
//                }
            }
        });

        locationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validation()) {
                        addPlace(locationEditText.getText().toString());
                    }
                }
                return handled;
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected int getMenuResource() {
        return R.menu.search_menu;
    }

    @Override
    protected String ToolBarName() {
        return "Search";
    }

    @Override
    protected int ToolBarIcon() {
        return R.mipmap.ic_launcher_m;
    }

    @Override
    protected String Number() {
        return "";
    }

    @Override
    protected String Place() {
        if (validation()) {
            return locationEditText.getText().toString();
        }
        return "";
    }

    @Override
    public void onClick(View v) {
        if (v == btn_clear || v == clear_layout) {
            locationEditText.setText("");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: " + connectionResult.getErrorCode());

        PocketHr.setToast(getApplicationContext(),"Google Places API connection failed with error code : " + connectionResult.getErrorCode(),Toast.LENGTH_LONG);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, int position) {

        if (mResultList != null) {
            try {
                if (mResultList.size() != 0) {
                    final String placeId = String.valueOf(mResultList.get(position).placeId);
                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                    locationEditText.setText(mResultList.get(position).primery);
                    locationEditText.setSelection(locationEditText.getText().length());
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(null);
                    }

                    Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(@NonNull PlaceBuffer places) {
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        final Place myPlace = places.get(0);
                                        // Log.e("TAG", "Place found: " + myPlace.getName());
                                        utils.setSharedPreference(mContext, String.valueOf(myPlace.getLatLng()), Constants.GEO_LATLONG);
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

    private void widget() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btn_clear = (Button) findViewById(R.id.clear);
        clear_layout = (LinearLayout) findViewById(R.id.linear_layout_clear);
        locationEditText = (EditText) findViewById(R.id.et_location);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_search);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);
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
            locationRequest.setInterval(30*1000);
            locationRequest.setFastestInterval(5*1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); //this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All locationEditText settings are satisfied. The client can initialize locationEditText
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        SearchActivity.this, 1000);
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

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            return true;
        } else {
            PocketHr.snackBarMessage(coordinatorLayout,"You don't have internet connection",Color.RED);
        }
        return false;
    }

    private boolean BuildGoogleService() {
        googleApiClient = new GoogleApiClient.Builder(SearchActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        return true;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            PocketHr.startSpecificActivity(SearchActivity.this,getApplicationContext(),MainActivity.class);
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean validation() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
            startActivity(getIntent());
            return false;
        }
        return true;
    }
}
