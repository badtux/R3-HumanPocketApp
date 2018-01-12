package com.rype3.pocket_hrm;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.rype3.pocket_hrm.Sqldb.DatabaseHandler;
import com.rype3.pocket_hrm.realm.LocationDetails;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends BaseActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener{
    private TextView textView_count, version;
    private EditText locationEditText;

    private Button btn_in, btn_out;
    long onDutyInMiliSeconds,
            StartTime,
            TimeBuff,
            timestamp,
            UpdateTime = 0L;

    Handler handler;

    int hour,
            Seconds,
            Minutes,
            MilliSeconds;

    private Realm myRealm;
    private Context context;
    private Utils utils;
    protected GoogleApiClient googleApiClient;
    private CoordinatorLayout coordinatorLayout;
    private ImageView image_in, image_out;
    private RelativeLayout relativeLayout_checkout, relativeLayout_checkin;
    private DatabaseHandler databaseHandler;

    private DataSave dataSave;
    private Intent intent;
    private String number,placeName;
    private BackServices backServices;
    // private DataSave getDataSave;
    // Constants
    public ArrayList<Integer> id_list;

    private static final long SYNC_FREQUENCY = 2;  // 1 hour (in seconds)
    public static final String AUTHORITY = "com.rype3.pocket_hrm";


    public static final long SECONDS_PER_MINUTE = 60L;
  //  public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL = SECONDS_PER_MINUTE;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        widget();

        context = this.getApplicationContext();
        utils = new Utils(context);
        timestamp = System.currentTimeMillis() / 1000;

        handler = new Handler();

        myRealm = Realm.getDefaultInstance();

        databaseHandler = new DatabaseHandler(this);

        dataSave = new DataSave(context, utils);
        dataSave. buildGoogleApiClient(MainActivity.this ,googleApiClient);

        version.setText(dataSave.Version(context));

        id_list = new ArrayList<>();

        intent = getIntent();
        number = intent.getStringExtra("number");
        placeName = intent.getStringExtra("place");

        if (utils.getBoolean(context, Constants.EXIT_STATAUS)) {
            boolean exit_state = Boolean.parseBoolean(utils.getSharedPreference(context, Constants.EXIT_STATAUS));
            AppState(exit_state);
        } else {
            utils.setSharedPreference(context, "", Constants.GEO_LATLONG);
        }

        if (placeName != null) {
            locationEditText.setText(placeName);
        }
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMenuResource() {
        return R.menu.checkinout_main;
    }

    @Override
    protected String ToolBarName() {
        return "";
    }

    @Override
    protected int ToolBarIcon() {
        return R.mipmap.ic_launcher_m;
    }

    @Override
    protected String Number() {
        return number;
    }

    @Override
    protected String Place() {
        return "";
    }

    private void widget() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        textView_count = (TextView) findViewById(R.id.textView_count);
        version = (TextView) findViewById(R.id.app_version);
        image_in = (ImageView) findViewById(R.id.iv_icon);
        image_out = (ImageView) findViewById(R.id.iv_icon_1);

        relativeLayout_checkout = (RelativeLayout) findViewById(R.id.relative_out);
        relativeLayout_checkin = (RelativeLayout) findViewById(R.id.relative_in);

        btn_in = (Button) findViewById(R.id.button_in);
        btn_in.setOnClickListener(onclick);

        btn_out = (Button) findViewById(R.id.button_out);
        btn_out.setOnClickListener(onclick);

        locationEditText = (EditText) findViewById(R.id.et_location);
        locationEditText.setOnClickListener(onclick);
        locationEditText.setInputType(InputType.TYPE_NULL);

    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == btn_in) {
                if (validation()) {
                    dataSave.hideKeyBoard(MainActivity.this);
                    if (reset()) {

                        final int sdk = Build.VERSION.SDK_INT;
                        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                            relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2_click));
                            relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3));
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                relativeLayout_checkin.setBackground(getResources().getDrawable(R.drawable.button_background_2_click));
                                relativeLayout_checkout.setBackground(getResources().getDrawable(R.drawable.button_background_3));
                            }
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_check, getApplicationContext().getTheme()));
                        } else {
                            image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                        }

                        dataSave.blinkIcon(image_out, 0);

                        long id = System.currentTimeMillis();

                        dataSave.DataSave(
                                databaseHandler,
                                myRealm,
                                id,
                                "in",
                                utils.getSharedPreference(context, Constants.DEVICE_ID),
                                "attendance",
                                true,
                                locationEditText.getText().toString(),
                                utils.getSharedPreference(context, Constants.USER_EPF_NO),
                                utils.getSharedPreference(context, Constants.USER_ID),
                                dataSave.Version(context));


                        utils.setSharedPreference(context, locationEditText.getText().toString(), Constants.LOCATION);
                        utils.setSharedPreference(context, "in", Constants.CHECKED_STATE);
                        utils.setSharedPreference(context, "true", Constants.EXIT_STATAUS);
                        StartTime = System.currentTimeMillis();
                        handler.postDelayed(runnable, 0);
                        utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);

                        btn_in.setClickable(false);
                       // dataSave.TriggerRefresh("1", dataSave.Ids(id_list,myRealm));
                    }
                }
            }

            if (v == btn_out) {
                if (!locationEditText.getText().toString().isEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next, getApplicationContext().getTheme()));
                    } else {
                        image_in.setImageDrawable(getResources().getDrawable(R.drawable.ic_navigate_next));
                    }
                    dataSave.blinkIcon(image_out, 1);
                    thankyouAlertMessageBox(utils.getSharedPreference(context, Constants.LAST_TIME));
                }
            }

//            if (v == btn_clear){
//                locationEditText.setText("");
//            }

            if (v == locationEditText){
                dataSave.startSpecificActivity(MainActivity.this,context,SearchActivity.class);
            }
        }
    };

    private void resetData() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                final int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
                    relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3_click));

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2));
                        relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3_click));
                    }
                }

                utils.setSharedPreference(context, "", Constants.CHECKED_STATE);
                utils.setSharedPreference(context, null, Constants.LOCATION);
                utils.setSharedPreference(context, "", Constants.START_TIMESTAMP);
                utils.setSharedPreference(context, "00:00:00", Constants.LAST_TIME);
                locationEditText.setText("");
                textView_count.setText("00:00:00");
                btn_in.setClickable(true);
                locationEditText.setClickable(true);
                dataSave.blinkIcon(image_out, 1);

             //   dataSave.TriggerRefresh("1", dataSave.Ids(id_list,myRealm));
            }
        }, 5000);
    }

    public boolean reset() {
        onDutyInMiliSeconds = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        Seconds = 0;
        Minutes = 0;
        MilliSeconds = 0;

        return true;
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            onDutyInMiliSeconds = System.currentTimeMillis() - (StartTime);

            String time_spent = dataSave.getTimeSpentString(onDutyInMiliSeconds);

            textView_count.setText(time_spent);

            utils.setSharedPreference(context, time_spent, Constants.LAST_TIME);

            handler.postDelayed(this, 0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (utils.getBoolean(context, Constants.CHECKED_STATE)) {
                if (utils.getSharedPreference(context, Constants.CHECKED_STATE).equals("in")) {
                    dataSave.alertMessage(
                            MainActivity.this,
                            "Exit?","",
                            "It's recommended to keep the APP running in the background. Do you really want to exit?",
                            "Yes! Exit",
                            "Cancel");
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void thankyouAlertMessageBox(String time) {

        String msg = "You have spent " + time + " Hrs at this location. Do you want to record the exit?";
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("Please Confirm")
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        long id_ = System.currentTimeMillis();
                        dataSave.DataSave(
                                databaseHandler,
                                myRealm,
                                id_,
                                "out",
                                utils.getSharedPreference(context, Constants.DEVICE_ID),
                                "attendance",
                                true,
                                locationEditText.getText().toString(),
                                utils.getSharedPreference(context, Constants.USER_EPF_NO),
                                utils.getSharedPreference(context, Constants.USER_ID), dataSave.Version(context));

                        utils.setSharedPreference(context, "out", Constants.CHECKED_STATE);
                        utils.setSharedPreference(context, "false", Constants.EXIT_STATAUS);
                 //    dataSave.TriggerRefresh("1", dataSave.Ids(id_list,myRealm));
                        resetData();
                 //     TimeBuff += MillisecondTime;
                        handler.removeCallbacks(runnable);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                }).create();
        dialog.show();
    }

    public void parseJsonResponse(final String result) {
        if (result != null) {
            Log.e("Result : ", result);
            try {
                JSONObject jsonObjectResult = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validation() {
        String String_location = locationEditText.getText().toString();
        if (String_location.isEmpty()) {
            dataSave.snackBarMessage(coordinatorLayout,"Location is required",Color.RED);
            return false;
        }
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
           // dataSave.TriggerRefresh(number, dataSave.Ids(id_list,myRealm));
        } else {
         //   dataSave.TriggerRefresh(null, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
//        if (dataSave.checkConnection()) {
//            dataSave.TriggerRefresh(number, dataSave.Ids(id_list,myRealm));
//        } else {
//            dataSave.TriggerRefresh(null, null);
//            dataSave.snackBarMessage(coordinatorLayout,"You don't have internet connection",Color.RED);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void AppState(boolean state) {
        try {
            if (state) {
                if (exitState()) {
                    relativeLayout_checkin.setVisibility(View.VISIBLE);
                    relativeLayout_checkout.setVisibility(View.VISIBLE);
                    textView_count.setVisibility(View.VISIBLE);
                }
                final int sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    relativeLayout_checkin.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_2_click));
                    relativeLayout_checkout.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_background_3));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        relativeLayout_checkin.setBackground(getResources().getDrawable(R.drawable.button_background_2_click));
                        relativeLayout_checkout.setBackground(getResources().getDrawable(R.drawable.button_background_3));
                    }
                }

                dataSave.blinkIcon(image_out, 0);

                StartTime = Long.parseLong(utils.getSharedPreference(context, Constants.START_TIMESTAMP));
                handler.postDelayed(runnable, 0);

                locationEditText.setText(utils.getSharedPreference(context, Constants.LOCATION));
                btn_in.setClickable(false);

            } else {
                utils.setSharedPreference(context, "", Constants.CHECKED_STATE);
                utils.setSharedPreference(context, null, Constants.LOCATION);
                utils.setSharedPreference(context, "0L", Constants.START_TIMESTAMP);

                btn_in.setClickable(true);
                locationEditText.setClickable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            btn_in.setBackground(getResources().getDrawable(R.drawable.button_background_2));
            utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);
        }
    }
}
