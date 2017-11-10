package com.rype3.pocket_hrm;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.rype3.pocket_hrm.realm.LocationDetails;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;


public class HistoryActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private Toolbar toolbar;
    private Realm myRealm;
    private Utils utils;
    private String number;
    private Intent intent;
    private Context context;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RealmResults<LocationDetails> locationDetailses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar();
        widget();

        myRealm = Realm.getDefaultInstance();

        context = this.getApplicationContext();
        utils = new Utils(context);

        locationDetailses = myRealm.where(LocationDetails.class).equalTo("type","attendance").equalTo("state",true).findAll();
        locationDetailses.sort("id");

        if (locationDetailses != null) {
            mAdapter = new HistoryAdapter(HistoryActivity.this,this, locationDetailses);
            recyclerView.setAdapter(mAdapter);
        }

        intent =  getIntent();
        number = intent.getStringExtra("number");

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(HistoryActivity.this,MainActivity.class);
            intent.putExtra("number",number);
            startActivity(intent);
            finish();
        }

        if (id == R.id.action_sync) {
            TriggerRefresh("1");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void parseJsonResponseHistory(final String result) {
        if (result != null) {
            Log.e("Result : ", result);
            try {
                JSONObject jsonObjectResult = new JSONObject(result);
                boolean status = jsonObjectResult.getBoolean("status");
                if (status){
                    int id = 0;

                    if (utils.getBoolean(context,Constants.TEMP_ID)) {
                        id = Integer.parseInt(utils.getSharedPreference(context, Constants.TEMP_ID));
                    }
                    LocationDetails updateLocationDetails = myRealm.where(LocationDetails.class).equalTo("id", id).findFirst();
                    if (updateLocationDetails != null) {
                        myRealm.beginTransaction();
                        updateLocationDetails.setState(false);
                        myRealm.commitTransaction();
                    }

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        if (mAdapter != null) {
            ((HistoryAdapter) mAdapter).setOnItemClickListener(new HistoryAdapter.MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.e("LOG_TAG", " Clicked on Item " + position);
                }
            });
        }
        MyApplication.getInstance().setConnectivityListener(this);
    }

    public static void TriggerRefresh(String num) {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        b.putString("number",num );

        ContentResolver.requestSync(
                AuthenticatorService.GetAccount(),      // Sync account
                null, // Content authority
                b);                                      // Extras
    }
}
