package com.rype3.pocket_hrm;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

public class MyLocationListner extends Service implements ConnectivityReceiver.ConnectivityReceiverListener{
    public static final String BROADCAST_ACTION = "LOCATION_LISTNER";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    Intent intent;
    int counter = 0;
    private Realm myRealm;
    private DataSave dataSave;
    private DataSave getDataSave;

   // private Utils utils;
    private Context context;
    Utils utils;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        context = this.getApplicationContext();
        utils = new Utils(context);

        myRealm = Realm.getDefaultInstance();

        getDataSave = new DataSave();

        dataSave = new DataSave(context,utils);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        String name = intent.getStringExtra("name");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();

        try {
            switch (name){
                case "START SETVICE":
            \        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
                    break;

                case "STOP SETVICE":
                    onDestroy();
                    break;
            }
           // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);

        } catch (SecurityException e) {
          //  dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than exit_btn location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.e("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(listener);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {

            if (isBetterLocation(loc, previousBestLocation)) {

                // loc.getLatitude();
                // loc.getLongitude();

                utils.setSharedPreference(context, String.valueOf(loc.getLatitude()), Constants.LAT);
                utils.setSharedPreference(context, String.valueOf(loc.getLongitude()), Constants.LONG);

    //              Log.e("****Latitude", String.valueOf(loc.getLatitude()));
    //             Log.e("****Longitude", String.valueOf(loc.getLongitude()));
//                 Log.e("****Provider", loc.getProvider());
//

                if (validation()) {
                     long id = System.currentTimeMillis();

//                    Long tsLong = System.currentTimeMillis() / 1000;
//                    String ts = tsLong.toString();
                    dataSave.DataSave(
                            myRealm,
                            id,
                            utils.getSharedPreference(context, Constants.CHECKED_STATE),
                            utils.getSharedPreference(context, Constants.DEVICE_ID),
                            "location",
                            true,
                            utils.getSharedPreference(context, Constants.LOCATION),
                            utils.getSharedPreference(context,Constants.USER_EPF_NO),
                            utils.getSharedPreference(context,Constants.USER_ID),dataSave.Version(context));
//
//                    if (checkConnection()) {
//                        if (validation()) {
//                            String location = "";
//                            String checkState = "";
//
//                            if (utils.getBoolean(context, Constants.LOCATION)) {
//                                location = utils.getSharedPreference(context, Constants.LOCATION);
//                            }
//
//                            if (utils.getBoolean(context, Constants.CHECKED_STATE)) {
//                                checkState = utils.getSharedPreference(context, Constants.CHECKED_STATE);
//                            }
//
//                            new ProcressAsyncTask(
//                                    "http://wmmmendis.rype3.net/io/api/v1/device/track",
//                                    String.valueOf(loc.getLatitude()),
//                                    String.valueOf(loc.getLongitude()),
//                                    utils.getSharedPreference(context, Constants.DEVICE_ID),
//                                    Integer.parseInt(ts),
//                                    checkState,
//                                    location,
//                                    checkConnection(),
//                                    String.valueOf(
//                                            dataSave.meta(
//                                                    utils.getSharedPreference(context, Constants.DEVICE_ID),
//                                                    String.valueOf(dataSave.getBatteryPercentage(context)),
//                                                    utils.getSharedPreference(context, Constants.DEVICE_NAME),
//                                                    utils.getSharedPreference(context, Constants.LOCATION),
//                                                    dataSave.IOStime()
//                                            ))).execute();
//                        }
//                    }else {

//                            if (validation()) {
//                                myRealm.beginTransaction();
//                                Location_object location = myRealm.createObject(Location_object.class);
//                                location.setTimeStamp(Integer.parseInt(ts));
//                                location.setLat(String.valueOf(loc.getLatitude()));
//                                location.setLon(String.valueOf(loc.getLongitude()));
//                                location.setInternetState(checkConnection());
//
//                                if (utils.getBoolean(context, Constants.LOCATION)) {
//                                    location.setLocation(utils.getSharedPreference(context, Constants.LOCATION));
//                                }
//
//                                location.setCheckStatus(utils.getSharedPreference(context, Constants.CHECKED_STATE)); //temerory
//                                location.setDeviceId(utils.getSharedPreference(context, Constants.DEVICE_ID));
//                                location.setSyncState(false);
//                                myRealm.commitTransaction();
//                            }
//                        }
                    }
                }
            }

        boolean validation(){
            String showroom = "";
            String check_State = "";
            if (utils.getBoolean(context,Constants.LOCATION)) {
                 showroom = utils.getSharedPreference(context, Constants.LOCATION);
                if (!showroom.isEmpty()) {
                } else {
                    return false;
                }
            }else{
                return false;
            }

            if (utils.getBoolean(context,Constants.CHECKED_STATE)) {
                check_State = utils.getSharedPreference(context, Constants.CHECKED_STATE);
                if (!check_State.isEmpty()) {
                } else {
                    return false;
                }
            }else{
                return false;
            }
            return true;
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

//        private boolean checkConnection() {
//            boolean isConnected = ConnectivityReceiver.isConnected();
//            if (isConnected) {
//                return true;
//            }
//            return false;
//        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        private boolean checkConnection() {
            boolean isConnected = ConnectivityReceiver.isConnected();
            if (isConnected) {
                return true;
            }
            return false;
        }

        private class ProcressAsyncTask extends AsyncTask<Void, Void, String> {
            private String url,lat,lon,device_id ,checkState,location,meta;
            private int timeStamp;
            private boolean connectionState;
            ProcressAsyncTask(String url, String lat, String lon, String device_id, int timeStamp, String checkState, String location, boolean connectionState, String meta) {
                super();

                this.url = url;
                this.lat = lat;
                this.lon = lon;
                this.device_id = device_id;
                this.timeStamp = timeStamp;
                this.checkState = checkState;
                this.location = location;
                this.connectionState = connectionState;
                this.meta = meta;

                //    Log.e("Proccess timeStamp : ", String.valueOf(this.timeStamp));
            }

            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    if (loadJSON(this.url, this.lat, this.lon, this.device_id, this.timeStamp, this.checkState,this.location, this.connectionState,this.meta) != null) {
                        return loadJSON(this.url, this.lat, this.lon, this.device_id, this.timeStamp, this.checkState, this.location, this.connectionState,this.meta).toString();
                    } else {
                        return null;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String result) {

                if (result != null) {
                    try {
                        //   Log.e("onPostExecute Id : ", String.valueOf(this.timeStamp));
                            Log.e("Result Live : ", result);
//                        Location_object updateLocationObject = myRealm.where(Location_object.class)
//                                .equalTo("timeStamp", this.timeStamp)
//                                .notEqualTo("checkStatus", "")
//                                .findFirst();
//                        if (updateLocationObject != null) {
//                            myRealm.beginTransaction();
//                            updateLocationObject.setSyncState(true);
//                            myRealm.commitTransaction();
//                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        JSONObject loadJSON(String url, String lat, String lon, String device_id, int timeStamp, String checkState, String location, boolean connectionState, String meta) {
            // Creating JSON Parser instance
            JSONParser jParser = new JSONParser();

            // getting JSON string from URL
            JSONObject json = jParser.getJSONFromUrl(url ,lat,lon,device_id,timeStamp,checkState,location,connectionState,meta);

            return json;
        }

        private class JSONParser {

            private InputStream is = null;
            private JSONObject jObj = null;
            private String json = "";

            // constructor3
            JSONParser() {

            }

            JSONObject getJSONFromUrl(String url, String lat, String lon, String device_id, int timeStamp, String checkState, String location, boolean connectionState, String meta) {
                JSONObject jsonObject = null;
                // Create a new HttpClient and Post Header

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);

                    List<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();

                    if (device_id != null) {
                        nameValuePairs.add(new BasicNameValuePair("device_id", device_id));
                    }

                    if (lat != null) {
                        nameValuePairs.add(new BasicNameValuePair("lat", lat));
                    }

                    if (lon != null) {
                        nameValuePairs.add(new BasicNameValuePair("lon", lon));
                    }

                    if (checkState != null) {
                        nameValuePairs.add(new BasicNameValuePair("check_state", checkState));
                    }

                    if (location != null) {
                        nameValuePairs.add(new BasicNameValuePair("location", location));
                    }

                    if (timeStamp != 0) {
                        nameValuePairs.add(new BasicNameValuePair("ts", String.valueOf(timeStamp)));
                    }

                    if (meta != null) {
                        nameValuePairs.add(new BasicNameValuePair("meta", meta));
                    }
                        nameValuePairs.add(new BasicNameValuePair("connectionState", String.valueOf(connectionState)));


                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();

                    StringBuilder stringBuilder = new StringBuilder();

                    //   Log.e("TAG", " statusCode " + String.valueOf(statusCode));

                    if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        InputStream inputStream = entity.getContent();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            jsonObject = new JSONObject(stringBuilder.toString());
                        }
                        inputStream.close();
                    }
                } catch (ClientProtocolException ignored) {

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return jsonObject;
            }
        }

    }
//    public JSONObject meta(){
//        JSONObject jsonObject = null;
//        String lat = "";
//        String lon = "";
//
//        jsonObject = new JSONObject();
//        try {
//            jsonObject.put("did",utils.getSharedPreference(context,Constants.DEVICE_ID));
//            jsonObject.put("bat",utils.getSharedPreference(context,Constants.BATTERY_LEVEL));
//            jsonObject.put("d_name",utils.getSharedPreference(context,Constants.DEVICE_NAME));
//
//            JSONObject jsonObjectLocation = new JSONObject();
//
//            if (utils.getBoolean(context,Constants.LAT)){
//                lat = utils.getSharedPreference(context,Constants.LAT);
//            }
//
//            if (utils.getBoolean(context,Constants.LONG)){
//                lon = utils.getSharedPreference(context,Constants.LONG);
//            }
//            jsonObjectLocation.put("lat" , lat);
//            jsonObjectLocation.put("long" , lon);
//
//            jsonObject.put("location", jsonObjectLocation);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return jsonObject;
//    }
}