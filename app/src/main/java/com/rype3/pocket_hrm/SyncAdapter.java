package com.rype3.pocket_hrm;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

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
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter implements ConnectivityReceiver.ConnectivityReceiverListener{
    public static final String TAG = "Sync Adapter";
    /**
     * URL to fetch content from during a sync.
     *
     * <p>This points to the Android Developers Blog. (Side note: We highly recommend reading the
     * Android Developer Blog to stay up to date on the latest Android platform developments!)
     *
     *
     * Network connection timeout, in milliseconds.
     *
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds
    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    //UploadToServer uploadToServerPatientDetails;
    /**
     * Set up the sync adapter
     */
    private Realm myRealm;
    private Utils utils;
    private RealmResults<Location_object> locationList;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

        Log.e("SyncAdapter : ","OK");

        myRealm = Realm.getInstance(context);
        utils = new Utils(context);

    }

    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        String time = bundle.getString("time");
        if (time != null) {
          Log.e("time : ", time);
         }

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable(){

            @Override
            public void run() {

                locationList = myRealm.where(Location_object.class)
                        .notEqualTo("checkStatus", "")
                        .findAll();
                new ProcressTask().execute(locationList.size());
            }
        });
    }

    public void updateLocalFeedData(final String stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {

        Log.e("updateLocalFeedData : ","OK");
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    private class ProcressTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int count = 0; count < integers[0]; count++) {

                try {
                    Thread.sleep(3000);
                    publishProgress(count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "Task Completed.";
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            SyncItinerary (values[0]);
        }
    }

    public void SyncItinerary (int index){
        Log.e("URL : " , "http://wmmmendis.rype3.net/io/api/v1/device/track");
              new ProcressAsyncTask(
                      "http://wmmmendis.rype3.net/io/api/v1/device/track",
                      locationList.get(index).getLat(),
                      locationList.get(index).getLon(),
                      locationList.get(index).getDeviceId(),
                      locationList.get(index).getTimeStamp(),
                      locationList.get(index).getCheckStatus(),locationList.get(index).getLocation(),locationList.get(index).isInternetState()).execute();
    }

    private class ProcressAsyncTask extends AsyncTask<Void, Void, String> {
        private String url,lat,lon,device_id ,checkState,location ;
        private int timeStamp;
        private boolean connectionState;
        public ProcressAsyncTask(String url , String lat, String lon , String device_id, int timeStamp , String checkState , String location, boolean connectionState) {
            super();

            this.url = url;
            this.lat = lat;
            this.lon = lon;
            this.device_id = device_id;
            this.timeStamp = timeStamp;
            this.checkState = checkState;
            this.location = location;
            this.connectionState = connectionState;

            //    Log.e("Proccess timeStamp : ", String.valueOf(this.timeStamp));
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (loadJSON(this.url, this.lat, this.lon, this.device_id, this.timeStamp, this.checkState, this.location, this.connectionState) != null) {
                    return loadJSON(this.url, this.lat, this.lon, this.device_id, this.timeStamp, this.checkState, this.location, this.connectionState).toString();
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
                        Log.e("Result offline :  ", result);

                    Location_object updateLocationObject = myRealm.where(Location_object.class)
                            .equalTo("timeStamp", this.timeStamp)
                            .notEqualTo("checkStatus", "")
                            .findFirst();
                    if (updateLocationObject != null) {
                        myRealm.beginTransaction();
                        updateLocationObject.setSyncState(true);
                        myRealm.commitTransaction();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public JSONObject loadJSON(String url , String lat, String lon , String device_id, int timeStamp , String checkState, String location, boolean connectionState) {
        // Creating JSON Parser instance
       JSONParser jParser = new JSONParser();

        // getting JSON string from URL
        JSONObject json = jParser.getJSONFromUrl(url ,lat,lon,device_id,timeStamp,checkState,location,connectionState);

        return json;
    }

    private class JSONParser {

        private InputStream is = null;
        private JSONObject jObj = null;
        private String json = "";

        // constructor3
        public JSONParser() {

        }

        JSONObject getJSONFromUrl(String url, String lat, String lon, String device_id, int timeStamp, String checkState, String location, boolean connectionState) {
            JSONObject jsonObject = null;
            // Create a new HttpClient and Post Header

            //    Log.e("Check State : ", checkState);

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
            } catch (ClientProtocolException e) {

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}

