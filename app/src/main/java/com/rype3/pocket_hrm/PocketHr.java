package com.rype3.pocket_hrm;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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
import com.rype3.pocket_hrm.Sqldb.DatabaseHandler;
import com.rype3.pocket_hrm.realm.LocationDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

class PocketHr implements ConnectivityReceiver.ConnectivityReceiverListener,
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    public static Context context;
    public static Utils utils;

    PocketHr(){
    }

    PocketHr(Context context, Utils utils){
        this.context = context;
        this.utils = utils;
    }

    void DataSave(DatabaseHandler databaseHandler,Realm realm, String checkState, String type, boolean state, String location){

        realm.beginTransaction();

        LocationDetails locationDetails  = realm.createObject(LocationDetails.class);
        locationDetails.setId((int)  currentTimeMilisecond());
        locationDetails.setCheckState(checkState);
        locationDetails.setMeta(String.valueOf(meta(GetSharedPreference(Constants.USER_ID), String.valueOf(getBatteryPercentage(context)), getDeviceName(),location,IOStime(),GetSharedPreference(Constants.USER_EPF_NO), GetSharedPreference(Constants.USER_ID),Version())));
        locationDetails.setType(type);
        locationDetails.setState(state);

   //     Log.e("LOG : ", String.valueOf(meta(deviceId, String.valueOf(getBatteryPercentage(context)), getDeviceName(),location,IOStime(),epf_no,user_id,app_v)));

        realm.commitTransaction();

//        databaseHandler.addContact(new com.rype3.pocket_hrm.Sqldb.LocationDetails((int) id,location,"","",checkState,deviceId,
//                String.valueOf(meta(deviceId, String.valueOf(getBatteryPercentage(context)), getDeviceName(),location,IOStime(),epf_no,user_id,app_v)),type));

// Reading all contacts
//        Log.d("Reading: ", "Reading all contacts..");
//        List<com.rype3.pocket_hrm.Sqldb.LocationDetails> locationDetail = databaseHandler.getAllContacts();
//
//        for (com.rype3.pocket_hrm.Sqldb.LocationDetails cn : locationDetail) {
//            String log = "Id: " + cn.get_id() + " ,Location_name: " + cn.getLocation_name() + " ,Meta: " + cn.getMeta();
//            // Writing Contacts to log
//            Log.d("Name: ", log);
//        }
    }

    public JSONObject meta(String did, String battery, String deviceName, String location, String ISOtime,String epf_no,String user_id,String app_v){
        JSONObject jsonObject;
        String lat = "";
        String lon = "";

        jsonObject = new JSONObject();
        try {
            jsonObject.put("did",did);
            jsonObject.put("bat",battery);
            jsonObject.put("d_name",deviceName);
            jsonObject.put("d_location",location);
            jsonObject.put("d_iso",ISOtime);
            jsonObject.put("epf",epf_no);
            jsonObject.put("user_id",user_id);
            jsonObject.put("app_v",app_v);

            JSONObject jsonObjectLocation = new JSONObject();

            if (utils.getBoolean(context,Constants.GEO_LATLONG)){
                jsonObject.put("geo_loc",utils.getSharedPreference(context,Constants.GEO_LATLONG));
            }

            if (utils.getBoolean(context,Constants.LAT)){
                String Lat = utils.getSharedPreference(context,Constants.LAT);
                jsonObjectLocation.put("lat" , Lat);
            }else{
                jsonObjectLocation.put("lat" , lat);
            }

            if (utils.getBoolean(context,Constants.LONG)){
                String Lon = utils.getSharedPreference(context,Constants.LONG);
                jsonObjectLocation.put("long" , Lon);
            }

            jsonObject.put("location", jsonObjectLocation);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String IOStime(){
        String ISOtime = "";
        DateFormat df;
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.UK);
        ISOtime = df.format(new Date());
        return ISOtime;
    }

    private String getDeviceName() {
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

    public int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }

    // blink icon
    public void blinkIcon(ImageView image,int type){
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

    public static String Version() {
        String versionCode = "";
        try {
            versionCode = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "v "+versionCode;
    }

    void alertMessage(final Activity activity, String title, String subtitle, String message, String positiveButtnText, String negativeButtonText){
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        activity.finish();
                    }
                }).setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }


    static void snackBarMessage(CoordinatorLayout coordinatorLayout, String message, int colour){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(colour);
        snackbar.show();
    }

    //Start Activity
    public static void startSpecificActivity(Activity activity,Context context, Class<?> nextClass){
        Intent intent  = new Intent(context,nextClass);
        activity.startActivity(intent);
        activity.finish();
    }

    //Start Activity
    public static void startSpecificActivityWithExtra(Activity activity,Context context, Class<?> nextClass ,String key_1 ,String key_2,String key_3,String key_4,String value_1,String value_2,String value_3,int value_4){
        Intent intent  = new Intent(context,nextClass);
        intent.putExtra(key_1,value_1);
        intent.putExtra(key_2,value_2);
        intent.putExtra(key_3,value_3);
        intent.putExtra(key_4,value_4);
        activity.startActivity(intent);
        activity.finish();
    }

    //Time formate view hh:mm:ss
    String getTimeSpentString(long diff) {
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


    public static ArrayList<Integer> Ids(ArrayList<Integer> id_list,Realm realm){
        id_list.clear();

        RealmResults<LocationDetails> locationList = realm.where(LocationDetails.class).equalTo("state", true).findAll();

        if (locationList != null) {
            for (int i = 0; i < locationList.size(); i++) {
                id_list.add(locationList.get(i).getId());
            }
        } else {
            id_list.add(null);
        }
        return id_list;
    }

    public void TriggerRefresh(String num, ArrayList<Integer> id_list) {
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

    //hide keyboard
    public  static void hideKeyBoard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //Check connectivity
    public static boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }


    public synchronized void buildGoogleApiClient(final Activity activity, GoogleApiClient googleApiClient) {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
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
                                        activity, 1000);
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
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static String getLocation(Context context){
        JSONObject jsonObject = new JSONObject();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // now get the lat/lon from the locationEditText and do something with it.
            if (lastLocation != null) {
                Toast.makeText(context,"Lat : " + lastLocation.getLatitude()+"\nLong : "+lastLocation.getLongitude(),Toast.LENGTH_SHORT ).show();
                Log.e("TAG ", " Lat : " + lastLocation.getLatitude());
            }
        }
        return String.valueOf(lastLocation.getLatitude());
    }

    public static void Visibility(RelativeLayout relativeLayout, TextView textView, Button button ,int visibility){

        if (relativeLayout != null){
            relativeLayout.setVisibility(visibility);
        }

        if (textView != null){
            textView.setVisibility(visibility);
        }

        if (button != null){
            button.setVisibility(visibility);
        }
    }


    public static void setBackgroundDrawable(Context context,RelativeLayout relativeLayout, TextView textView, Button button,int drawable){
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            if (relativeLayout != null) {
                relativeLayout.setBackgroundDrawable(context.getResources().getDrawable(drawable));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void setImageDrawable(Context context, ImageView imageView , Button button, int icon){
        if (imageView != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageDrawable(context.getResources().getDrawable(icon, context.getTheme()));
            }else{
                imageView.setImageDrawable(context.getResources().getDrawable(icon));
            }
        }

        if (button != null){
            button.setBackground(context.getResources().getDrawable(icon));
        }
    }

    public static void setToast(Context context,String message ,int length){
        Toast.makeText(context,message ,length).show();
    }

    //Start Sevice
    public static void startService(Activity activity,Context context, Class<?> nextClass,String type){
        Intent intent  = new Intent(context,nextClass);
        intent.putExtra("name" ,type);
        activity.startActivity(intent);
    }

    public static long currentTimeMilisecond(){
       return System.currentTimeMillis();
    }

    public static String GetSharedPreference(String constants){
       return utils.getSharedPreference(context,constants) ;
    }

    public static String timeStamp(){
        Long tsLong = System.currentTimeMillis()/1000L;
        return tsLong.toString();
    }


    public static String getDate(long timeStamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        return formatter.format(new Date(timeStamp*1000));
    }

}
