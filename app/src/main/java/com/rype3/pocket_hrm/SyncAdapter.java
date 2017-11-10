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

import com.rype3.pocket_hrm.realm.LocationDetails;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter{
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
    private LocationDetails getLocation;
    private Utils utils;
    private DataSave dataSave;
    JSONArray jsonArray;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

        Log.e("SyncAdapter : ","OK");

        myRealm = Realm.getDefaultInstance();
        utils = new Utils(context);

        dataSave = new DataSave(context, utils);
    }
//
//    public SyncAdapter(
//            Context context,
//            boolean autoInitialize,
//            boolean allowParallelSyncs) {
//        super(context, autoInitialize, allowParallelSyncs);
//        /*
//         * If your app uses a content resolver, get an instance of it
//         * from the incoming Context
//         */
//        mContentResolver = context.getContentResolver();
//    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        String number = bundle.getString("number");
        final String id_list = bundle.getString("id_list");
        if (number != null) {
            Log.e("number : ", number);

            if (number.equals("1")) {
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {

                    @Override
                    public void run() {

                        if (id_list != null){

                            try {
                              jsonArray = new JSONArray(id_list);

                            //    Log.e("Id list : ", jsonArray.toString());

                                new ProcressTask2().execute(jsonArray.length());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

//

                       }
                    }
                });
            }
        }
    }

    public void updateLocalFeedData(final String stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {

        Log.e("updateLocalFeedData : ","OK");
    }


    private class ProcressTask2 extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int count = 0; count < integers[0]; count++) {

                try {
                    Thread.sleep(5000); //5s
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

    private void SyncItinerary(int index) {

        try {
            Log.e("Id list : ", jsonArray.get(index).toString());

            getLocation = myRealm.where(LocationDetails.class).equalTo("id", Integer.parseInt(jsonArray.get(index).toString())).findFirst();

            if (getLocation != null) {
                JSONObject jsonObject = new JSONObject(getLocation.getMeta());
                if (jsonObject != null) {
                    String did = jsonObject.getString("did");
                    String d_location = jsonObject.getString("d_location");
                    String d_iso = jsonObject.getString("d_iso");
                    String d_name = jsonObject.getString("d_name");
                    String location = jsonObject.getString("location");

                    JSONObject locatio_json = new JSONObject(location);

                    String latitude = locatio_json.getString("lat");
                    String longtitude = locatio_json.getString("long");

                    String in = null;
                    String out = null;
                    String url = "";
                    switch (getLocation.getType()) {


                        case "attendance":

                            switch (getLocation.getCheckState()) {

                                case "in":
                                    url = "http://wmmmendis.rype3.net/human/api/v1/check-in";
                                    in = d_iso;
                                    break;

                                case "out":
                                    url = "http://wmmmendis.rype3.net/human/api/v1/check-out";
                                    out = d_iso;
                                    break;
                            }

                            break;

                        case "location":
                            url = "http://wmmmendis.rype3.net/io/api/v1/device/track";
                            break;
                    }
                    new ProcressAsyncTask1(
                            url,
                            latitude,
                            longtitude,
                            did,
                            d_name,
                            getLocation.getId(),
                            getLocation.getCheckState(),
                            d_location,
                            getLocation.getMeta(),
                            utils.getSharedPreference(getContext(), Constants.USER_ID), //  uid
                            in, // checkIn
                            out). //checkOut
                            execute();

//                    Log.e("TAG : ",
//                            "Url : " + url +
//                            "\nId : " + getLocation.getId() +
//                            "\nType : " + getLocation.getType() +
//                            "\nChecked state : " + getLocation.getCheckState() + "\nMeta" + getLocation.getMeta());
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ProcressAsyncTask1 extends AsyncTask<Void, Void, String> {
        private String url,lat,lon,device_id ,checkState,location ,meta,uid,checkedAt , checkedOutAt,device_name;
        private int timeStamp;

        ProcressAsyncTask1(
                String url,
                String lat,
                String lon,
                String device_id,
                String device_name,
                int timeStamp,
                String checkState,
                String location,
                String meta,
                String uid,
                String checkedAt ,
                String checkedOutAt) {
            super();

            this.url = url;
            this.lat = lat;
            this.lon = lon;
            this.device_id = device_id;
            this.device_name = device_name;
            this.timeStamp = timeStamp;
            this.checkState = checkState;
            this.location = location;
            this.meta = meta;
            this.uid = uid;
            this.checkedAt = checkedAt;
            this.checkedOutAt = checkedOutAt;

        //  Log.e("Proccess timeStamp : ", String.valueOf(this.timeStamp));
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (loadJSON(this.url, this.lat, this.lon, this.device_id, this.device_name,this.timeStamp, this.checkState, this.location, this.meta,this.uid,this.checkedAt,this.checkedOutAt) != null) {
                    return loadJSON(this.url, this.lat, this.lon, this.device_id,this.device_name, this.timeStamp, this.checkState, this.location, this.meta,this.uid,this.checkedAt,this.checkedOutAt).toString();
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
              //  Log.e("result : ", result);
              //  Log.e("Result offline :  ", String.valueOf(this.timeStamp));

                    LocationDetails updateLocationDetails = myRealm.where(LocationDetails.class).equalTo("id", this.timeStamp).findFirst();
                    if (updateLocationDetails != null) {
                        myRealm.beginTransaction();
                        updateLocationDetails.setState(false);
                        myRealm.commitTransaction();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Integer loadJSON(String url, String lat, String lon, String device_id, String device_name,int timeStamp, String checkState, String location, String meta,String uid,String checkedAt ,String checkedOutAt) {
        // Creating JSON Parser instance
       JSONParser jParser = new JSONParser();

        // getting JSON string from URL
        int json = jParser.getJSONFromUrl(url ,lat,lon,device_id,device_name,timeStamp,checkState,location,meta , uid ,checkedAt,checkedOutAt);

        return json;
    }

    private class JSONParser {

        // constructor
        JSONParser() {
        }

        int getJSONFromUrl(String url, String lat, String lon, String device_id, String device_name, int timeStamp, String checkState, String location, String meta, String uid , String checkedAt , String checkedOutAt) {
            JSONObject jsonObject = null;
            //      Create a new HttpClient and Post Header
            //      Log.e("Check State : ", checkState);

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                List<BasicNameValuePair> nameValuePairs = new LinkedList<BasicNameValuePair>();

                if (device_id != null) {
                    nameValuePairs.add(new BasicNameValuePair("device_id", device_id));
                    nameValuePairs.add(new BasicNameValuePair("imei", device_id));// attendance

                }

                if (device_name != null){
                    nameValuePairs.add(new BasicNameValuePair("device_name", device_name));// attendance
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

                if (meta != null) {//tracker meta, Attendance
                    nameValuePairs.add(new BasicNameValuePair("meta", meta));

                  //  Log.e("LOG meta ", meta);
                }

                if (uid != null) {
                    nameValuePairs.add(new BasicNameValuePair("uid", uid)); //need to add user id
                }

                if (checkedAt != null) {
                    nameValuePairs.add(new BasicNameValuePair("checked_at", checkedAt));
                  //  Log.e("checked_at LOG ", checkedAt);
                }

                if (checkedOutAt != null) {
                    nameValuePairs.add(new BasicNameValuePair("checkout_at", checkedOutAt));
                 //   Log.e("checkout_at LOG ", checkedOutAt);
                }


//                if ( metaAttendance != null){// in/out meta
//                    nameValuePairs.add(new BasicNameValuePair("meta", metaAttendance));
//
//                    Log.e("LOG metaAttendance ", metaAttendance);
//                }
                nameValuePairs.add(new BasicNameValuePair("description", ""));
                nameValuePairs.add(new BasicNameValuePair("client_version", dataSave.Version(getContext())));
                nameValuePairs.add(new BasicNameValuePair("u_id", utils.getSharedPreference(getContext(),Constants.USER_EPF_NO)));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                Log.e("statusCode : ", String.valueOf(statusCode));

                StringBuilder stringBuilder = new StringBuilder();

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
            } catch (ClientProtocolException | UnknownHostException ignored) {

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return timeStamp;

        }
    }
}

