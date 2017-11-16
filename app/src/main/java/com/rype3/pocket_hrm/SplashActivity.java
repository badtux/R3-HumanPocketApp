package com.rype3.pocket_hrm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;

public class SplashActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ConnectivityReceiver.ConnectivityReceiverListener{
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.rype3.mendischecking";
    boolean newAccount = false;
    private static final long SYNC_FREQUENCY = 2;  // 1 hour (in seconds)
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            intent = new Intent(SplashActivity.this, MyLocationListner.class);
            intent.putExtra("name", "START SETVICE");
            startService(intent);

            if (syncMethod()) {
                intent = new Intent(this, OauthLogin.class);
            } else {
                Toast.makeText(getApplicationContext(), "SYNC Error", Toast.LENGTH_SHORT).show();
                intent = new Intent(this, OauthLogin.class);
            }
            startActivity(intent);
            finish();
    }

    private boolean syncMethod() {
        boolean setupComplete = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_SETUP_COMPLETE, false);
        Account account = AuthenticatorService.GetAccount();

        AccountManager accountManager = (AccountManager) this.getSystemService(Context.ACCOUNT_SERVICE);

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
        }
        return true;
    }

     /* Class Single Location_object Listner */


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
