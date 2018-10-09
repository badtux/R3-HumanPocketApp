package com.rype3.pocket_hrm;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.rype3.pocket_hrm.realm.LocationDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.rype3.pocket_hrm.PocketHr.checkConnection;


public class HistoryActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener,HistoryAdapter.MyClickListener{
    private Toolbar toolbar;
    private Realm myRealm;
    private Utils utils;
    private String number;
    private Intent intent;
    private Context context;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    public ArrayList<Integer> id_list;
    private RealmResults<LocationDetails> locationList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RealmResults<LocationDetails> locationDetailses;
    Switch mySwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar();
        widget();

        myRealm = Realm.getDefaultInstance();

        context = this.getApplicationContext();
        utils = new Utils(context);

        getAttendanceState();

        intent =  getIntent();
        number = intent.getStringExtra("number");

        id_list = new ArrayList<>();
    }

    public void toolbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        toolbar.setTitle(" HISTORY");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void widget(){
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_main, menu);
        MenuItem item = menu.findItem(R.id.myswitch);
        item.setActionView(R.layout.switch_layer);

        mySwitch = (Switch) item.getActionView().findViewById(R.id.switch_sync);
        if (utils.getBoolean(context,Constants.SYNC_STATE)){
            mySwitch.setChecked(true);
            mySwitch.setText("Sync on");
        }else{
            mySwitch.setText("Sync off");
            mySwitch.setChecked(false);
        }
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mySwitch.setText("Sync on");
                    BaseActivity.EnableSyncAutomatically(true);
                    utils.setSharedPreference(context,"1",Constants.SYNC_STATE);
                }else{
                    mySwitch.setText("Sync off");
                    BaseActivity.EnableSyncAutomatically(false);
                    utils.setSharedPreference(context,null,Constants.SYNC_STATE);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            locationDetailses = myRealm.where(LocationDetails.class).equalTo("state",false).findAll();

            for(int i = 0; i < locationDetailses.size(); i++){
                LocationDetails locationDetails = locationDetailses.get(i);
                deleteCache(locationDetails.getId());
            }

            PocketHr.startSpecificActivityWithExtra(
                    HistoryActivity.this,
                    context,
                    MainActivity.class,
                    "number",
                    "",
                    "",
                    "",
                     number,
                    "",
                    "",
                    0);
        }

//        if (id == R.id.action_sync) {
//            if (BaseActivity.EnableSyncAutomatically(true)) {
//                TriggerRefresh("1");
//            }
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void parseJsonResponseHistory(final String result) {
        if (result != null) {
         //   Log.e("TAG@@ " , result);
            try {
                JSONObject jsonObjectResult = new JSONObject(result);
                boolean status = jsonObjectResult.getBoolean("status");
                if (status){
                    utils.setSharedPreference(context, PocketHr.timeStamp(),Constants.last_sync_time);
                    int id = 0;
                    if (utils.getBoolean(context,Constants.TEMP_ID)) {
                        id = Integer.parseInt(utils.getSharedPreference(context, Constants.TEMP_ID));
                    }
                    if (updateAttendance(myRealm,id)) {
                        getAttendanceState();
                        Toast.makeText(context,"Successfully sent",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                AuthenticatorService.GetAccount(),      // Sync account
                null, // Content authority
                b);                                      // Extras
    }

    public void getAttendanceState(){

//        locationDetailses = myRealm.where(LocationDetails.class).findAll();
//
//        for(int i = 0; i < locationDetailses.size(); i++){
//            LocationDetails locationDetails = locationDetailses.get(i);
//
//            Log.e("LOG : " , locationDetails.getMeta());
//            try {
//                JSONObject jsonObject = new JSONObject(locationDetails.getMeta());
//                if (jsonObject.getString("geo_loc").equals("") || jsonObject.getString("d_location").equals("")){
//                 //   deleteCache(locationDetails.getId());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        locationDetailses = myRealm.where(LocationDetails.class).equalTo("type","attendance").equalTo("state",true).findAll();
        locationDetailses.sort("id");

        if (locationDetailses != null) {
            mAdapter = new HistoryAdapter(this,HistoryActivity.this,this, locationDetailses);
            recyclerView.setAdapter(mAdapter);
        }
    }

    public boolean updateAttendance(Realm myRealm, int id){
        LocationDetails updateLocationDetails = myRealm.where(LocationDetails.class).equalTo("id", id).findFirst();
        if (updateLocationDetails != null) {
            myRealm.beginTransaction();
            updateLocationDetails.setState(false);
            myRealm.commitTransaction();
            return true;
        }
        return false;
    }


    public void deleteCache(final int id){
        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<LocationDetails> result = null;
                result = realm.where(LocationDetails.class).equalTo("id",id).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

    @Override
    public void onItemClick(int position, View v,List<LocationDetails> LocatioDetailList) {
                if (checkConnection()) {
                    if (utils.getBoolean(context,Constants.SYNC_STATE)) {
                        mySwitch.setChecked(true);
                        mySwitch.setText("Sync on");
                       // TriggerRefresh();
                        Toast.makeText(context,"Auto sync enabled. Processing..!",Toast.LENGTH_SHORT).show();

                    } else{
                        mySwitch.setText("Sync off");
                        mySwitch.setChecked(false);
                        utils.setSharedPreference(context, String.valueOf(LocatioDetailList.get(position).getId()), Constants.TEMP_ID);

                        switch (LocatioDetailList.get(position).getCheckState()) {
                        case "in":
                            new ProcressAsyncTask(null, this,utils,
                                    Constants.BASE_URL + "/human/api/v1/check-in",

                                    null,
                                    null,
                                    PocketHr.GetSharedPreference(Constants.EPF_NUMBER),//epf
                                    "POST",//HTTP_TYPE
                                    5,//type activity method
                                    PocketHr.Version(),//version
                                    PocketHr.GetSharedPreference(Constants.TOKEN),//token
                                    PocketHr.GetSharedPreference(Constants.DEVICE_ID),//deviceId
                                    utils.getSharedPreference(context, Constants.USER_ID),//uid
                                    PassedDetails(LocatioDetailList.get(position).getMeta()),// checked at time iso
                                    null,
                                    null,
                                    null,
                                    0,
                                    null,
                                    null, // Check string ont use
                                    LocatioDetailList.get(position).getMeta()).execute();
                            break;

                        case "out":
                            new ProcressAsyncTask(null, this,utils,
                                    Constants.BASE_URL + "/human/api/v1/check-out",
                                    null,
                                    null,
                                    PocketHr.GetSharedPreference(Constants.EPF_NUMBER),//epf
                                    "POST",//HTTP_TYPE
                                    5,//type activity method
                                    PocketHr.Version(),//version
                                    PocketHr.GetSharedPreference(Constants.TOKEN),//token
                                    PocketHr.GetSharedPreference(Constants.DEVICE_ID),//deviceId
                                    utils.getSharedPreference(context, Constants.USER_ID),//uid
                                    null,
                                    PassedDetails(LocatioDetailList.get(position).getMeta()),// checked at time iso
                                    null,
                                    null,
                                    0,
                                    null,
                                    null, // Check string ont use
                                    LocatioDetailList.get(position).getMeta()).execute();

                            break;

                    }
                }
            }else{
               Toast.makeText(context,"Connection error..!",Toast.LENGTH_SHORT).show();
            }
    }

    private String PassedDetails(String meta) {
        String d_iso = "";
        try {
            JSONObject jsonObject = new JSONObject(meta);
            d_iso = jsonObject.getString("d_iso");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return d_iso;
    }
}
