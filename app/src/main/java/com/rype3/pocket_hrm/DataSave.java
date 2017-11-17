package com.rype3.pocket_hrm;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.rype3.pocket_hrm.realm.LocationDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

import static android.R.attr.version;

class DataSave{

    Context context;
    Utils utils;

    DataSave(){
    }

    DataSave(Context context, Utils utils){

        this.context = context;
        this.utils = utils;
    }

    void DataSave(Realm realm, long id, String checkState, String deviceId, String type, boolean state, String location,String epf_no,String user_id,String app_v){

        realm.beginTransaction();

        LocationDetails locationDetails  = realm.createObject(LocationDetails.class);
        locationDetails.setId((int) id);
        locationDetails.setCheckState(checkState);
        locationDetails.setMeta(String.valueOf(meta(deviceId, String.valueOf(getBatteryPercentage(context)), getDeviceName(),location,IOStime(),epf_no,user_id,app_v)));
        locationDetails.setType(type);
        locationDetails.setState(state);

   //     Log.e("LOG : ", String.valueOf(meta(deviceId, String.valueOf(getBatteryPercentage(context)), getDeviceName(),location,IOStime(),epf_no,user_id,app_v)));

        realm.commitTransaction();

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

    public String Version(Context context) {
        String versionCode = "";
        try {
            versionCode = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        return "v "+versionCode;
    }
}
