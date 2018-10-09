package com.rype3.pocket_hrm;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.rype3.pocket_hrm.realm.LocationDetails;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import io.realm.Realm;

import static com.rype3.pocket_hrm.PocketHr.timeStamp;

class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "Sync Adapter";

    ContentResolver mContentResolver;
    private Realm myRealm;
    private LocationDetails getLocation;
    private Utils utils;
    public PocketHr pocketHr;
    public ArrayList<Integer> id_list;
    JSONArray jsonArray;
    Context mContext;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();

        Log.e("SyncAdapter : ","OK");

        myRealm = Realm.getDefaultInstance();
        utils = new Utils(context);

        id_list = new ArrayList<>();

        mContext = context;

        pocketHr = new PocketHr(context, utils);
        ProcressAsyncTask procressAsyncTask = new ProcressAsyncTask(getContext());
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.e("TAG :","onPerformSync()");

        Handler h1 = new Handler(Looper.getMainLooper());
        h1.post(new Runnable() {
            public void run() {

                if (!String.valueOf(PocketHr.Ids(id_list,myRealm)).equals("[]")){
                    jsonArray = new JSONArray(id_list);
                    new ProcressTask2().execute(jsonArray.length());
                }else{
                    BaseActivity.EnableSyncAutomatically(false);
                    Log.e("SYNC : ", "OFF");
                    utils.setSharedPreference(getContext(),timeStamp(),Constants.last_sync_time);
                    utils.setSharedPreference(getContext(),null,Constants.SYNC_STATE);
                }
            }
               });
        }


    private class ProcressTask2 extends AsyncTask<Integer, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int count = 0; count < integers[0]; count++) {

                try {
                    Thread.sleep(10000); //10s
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

            getLocation = myRealm.where(LocationDetails.class).equalTo("id", Integer.parseInt(jsonArray.get(index).toString())).findFirst();

            if (getLocation != null) {
                String did  = "";
                String d_location = "";
                String d_iso = "";
                String d_name = "";
                String location = "";
                JSONObject jsonObject = new JSONObject(getLocation.getMeta());
                if (jsonObject != null) {

                    if (jsonObject.has("did")){
                        did = jsonObject.getString("did");
                    }else{
                        did  = "";
                    }

                    if (jsonObject.has("d_location")){
                        d_location = jsonObject.getString("d_location");
                    }else{
                        d_location = "";
                    }

                    if (jsonObject.has("d_iso")){
                        d_iso = jsonObject.getString("d_iso");
                    }else{
                        d_iso = "";
                    }

                    if (jsonObject.has("d_name")){
                        d_name = jsonObject.getString("d_name");
                    }else{
                        d_name = "";
                    }

                    if (jsonObject.has("location")){
                        location = jsonObject.getString("location");
                    }else{
                        location = "";
                    }

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
                                    url =   Constants.BASE_URL+"/human/api/v1/check-in";
                                    in = d_iso;
                                    break;

                                case "out":
                                    url = Constants.BASE_URL+"/human/api/v1/check-out";
                                    out = d_iso;
                                    break;
                            }

                            break;

                        case "location":
                            url = Constants.BASE_URL+"/io/api/v1/device/track";
                            break;
                    }

               //     Log.e("URL : " , url);

                    new ProcressAsyncTask(myRealm,
                    null,
                     utils,
                     url,
                    null,
                    null,
                    PocketHr.GetSharedPreference(Constants.EPF_NUMBER),
                    "POST",
                    8,
                    PocketHr.Version(),
                    PocketHr.GetSharedPreference(Constants.TOKEN),
                    PocketHr.GetSharedPreference(Constants.DEVICE_ID),
                    PocketHr.GetSharedPreference(Constants.USER_ID),
                    in,
                    out,
                    latitude,
                    longtitude,
                    getLocation.getId(),
                    getLocation.getCheckState(),
                    d_location,
                    getLocation.getMeta()).execute();

//                    Log.e("TAG : ",
//                            "Url : " + url +
//                                    "\nId : " + getLocation.getId() +
//                                    "\nType : " + getLocation.getType() +
//                                    "\nChecked state : " + getLocation.getCheckState() + "\nMeta" + getLocation.getMeta());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
