package com.rype3.pocket_hrm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;

import io.realm.Realm;

public class SplashActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ConnectivityReceiver.ConnectivityReceiverListener{

    // Constants
    private static final String PREF_SETUP_COMPLETE = "setup_complete";

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.rype3.pocket_hrm";

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    boolean newAccount = false;
    private static final long SYNC_FREQUENCY = 5*60;  // 5 minuits (in seconds)

    Intent intent;
    private Context context;
    Account account;
    private Realm myRealm;
    AccountManager accountManager;
    public ArrayList<Integer> id_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        myRealm = Realm.getDefaultInstance();

//        intent = new Intent(SplashActivity.this, MyLocationListner.class);
//        intent.putExtra("name", "START SETVICE");
//        startService(intent);

        id_list = new ArrayList<>();

        account = AuthenticatorService.GetAccount();
        accountManager = (AccountManager) this.getSystemService(Context.ACCOUNT_SERVICE);

        if (syncMethod()) {
            if (syncanable()){
               PocketHr.startSpecificActivity(SplashActivity.this,context,OauthLogin.class);
            }else{
               PocketHr.setToast(context,"AUTO SYNC ERROR",Toast.LENGTH_SHORT);
            }
        } else {
            PocketHr.setToast(context,"SYNC ERROR",Toast.LENGTH_SHORT);
            PocketHr.startSpecificActivity(SplashActivity.this,context,OauthLogin.class);
        }
    }

    private boolean syncanable(){
//        if (!String.valueOf(PocketHr.Ids(id_list,myRealm)).equals("[]")){
//            BaseActivity.EnableSyncAutomatically(true);
//        }else{
//            BaseActivity.EnableSyncAutomatically(false);
//        }
        return true;
    }

    private boolean syncMethod() {
        boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_SETUP_COMPLETE, false);

        if (accountManager.addAccountExplicitly(account, null, null)) {
            // Inform the system that this account supports sync
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
            // Inform the system that this account is eligible for auto sync when the network is up
            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
            // Recommend a schedule for automatic synchronization. The system may modify this based
            // on other scheduled syncs and network utilization.

            Bundle bundle = new Bundle();
            ContentResolver.addPeriodicSync(account, AUTHORITY, bundle, SYNC_FREQUENCY);
            newAccount = true;
//            if (newAccount || !setupComplete) {
//                pocketHr.TriggerRefresh("2" ,null);
//                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PREF_SETUP_COMPLETE, true).apply();
//            }
        }
        return true;
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

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }
}