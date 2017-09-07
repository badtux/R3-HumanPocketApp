package com.rype3.pocket_hrm;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticatorService extends Service {
    private static final String TAG = "Authenticator Service";
    private static final String ACCOUNT_TYPE = "com.rype3.mendischecking";
    public static final String ACCOUNT_NAME = "Mendis";

    // Instance field that stores the authenticator object
    private Authenticator mAuthenticator;

    public static Account GetAccount() {

        final String accountName = ACCOUNT_NAME;
        return new Account(accountName, ACCOUNT_TYPE);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "Service created");
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */

    @Override
    public void onDestroy() {
        Log.e(TAG, "Service destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
