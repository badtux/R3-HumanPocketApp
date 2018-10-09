package com.rype3.pocket_hrm;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.rype3.pocket_hrm.Sqldb.DatabaseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import io.realm.Realm;
import static com.rype3.pocket_hrm.PocketHr.Version;
import static com.rype3.pocket_hrm.PocketHr.getDate;
import static com.rype3.pocket_hrm.PocketHr.hideKeyBoard;

public class MainActivity extends BaseActivity implements ConnectivityReceiver.ConnectivityReceiverListener{
    private TextView textView_count;
    private TextView version,last_update_on;
    private EditText locationEditText;

    private Button btn_in;
    private Button btn_out;
    long onDutyInMiliSeconds;
    long StartTime;
    long TimeBuff;
    long UpdateTime = 0L;

    Handler handler;

    int hour;
    int Seconds;
    int Minutes;
    int MilliSeconds;

    private Realm myRealm;
    private Context context;
    private Utils utils;
    protected GoogleApiClient googleApiClient;
    private CoordinatorLayout coordinatorLayout;
    private ImageView image_in;
    private ImageView image_out;
    private RelativeLayout relativeLayout_checkout;
    private RelativeLayout relativeLayout_checkin;
    private DatabaseHandler databaseHandler;

    private PocketHr pocketHr;
    private Intent intent;
    private String number;
    private String placeName;

    // private PocketHr getDataSave;

    // Constants


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        widget();

        context = this.getApplicationContext();
        utils = new Utils(context);

        handler = new Handler();

        myRealm = Realm.getDefaultInstance();

        databaseHandler = new DatabaseHandler(this);

        pocketHr = new PocketHr(context, utils);
        pocketHr.buildGoogleApiClient(MainActivity.this,googleApiClient);
        version.setText(Version());

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

        if (utils.getBoolean(context,Constants.last_sync_time)){
            last_update_on.setText("Last sync : " +getDate(Long.parseLong(utils.getSharedPreference(context, Constants.last_sync_time))));
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
        last_update_on = (TextView) findViewById(R.id.last_update_on);
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
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onClick(View v) {

            if (v == btn_in) {
                if (validation()) {
                    hideKeyBoard(MainActivity.this);
                    if (reset()) {
                      //  BaseActivity.EnableSyncAutomatically(true);

                        PocketHr.setBackgroundDrawable(context,relativeLayout_checkin,null,null,R.drawable.button_background_2_click);
                        PocketHr.setBackgroundDrawable(context,relativeLayout_checkout,null,null,R.drawable.button_background_3);

                       // Log.e("LOCATION : " , location(MainActivity.this));

                        PocketHr.setImageDrawable(context,image_in,null,R.drawable.ic_check);

                        pocketHr.blinkIcon(image_out, 0);

                        pocketHr.DataSave(
                                databaseHandler,
                                myRealm,
                                "in",
                                "attendance",
                                true,
                                locationEditText.getText().toString());

                        utils.setSharedPreference(context, locationEditText.getText().toString(), Constants.LOCATION);
                        utils.setSharedPreference(context, "in", Constants.CHECKED_STATE);
                        utils.setSharedPreference(context, "true", Constants.EXIT_STATAUS);
                        StartTime = System.currentTimeMillis();
                        handler.postDelayed(runnable, 0);
                        utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);

                        btn_in.setClickable(false);

//                      PocketHr.startService(MainActivity.this, context,MyLocationListner.class,"START SETVICE");
                    }
                }
            }

            if (v == btn_out) {
                if (!locationEditText.getText().toString().isEmpty()) {
                    PocketHr.setImageDrawable(context,image_in,null,R.drawable.ic_navigate_next);
                    pocketHr.blinkIcon(image_out, 1);
                    thankyouAlertMessageBox(utils.getSharedPreference(context, Constants.LAST_TIME));
                }
            }
//            if (v == btn_clear){
//                locationEditText.setText("");
//            }
            if (v == locationEditText){
                PocketHr.startSpecificActivity(MainActivity.this,context,SearchActivity.class);
            }
        }
    };

    private void resetData() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PocketHr.setBackgroundDrawable(context,relativeLayout_checkin,null,null,R.drawable.button_background_2);
                PocketHr.setBackgroundDrawable(context,relativeLayout_checkout,null,null,R.drawable.button_background_3_click);

                utils.setSharedPreference(context, "", Constants.CHECKED_STATE);
                utils.setSharedPreference(context, null, Constants.LOCATION);
                utils.setSharedPreference(context, "", Constants.START_TIMESTAMP);
                utils.setSharedPreference(context, "00:00:00", Constants.LAST_TIME);

                locationEditText.setText("");
                textView_count.setText("00:00:00");
                btn_in.setClickable(true);
                locationEditText.setClickable(true);
                pocketHr.blinkIcon(image_out, 1);
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

            String time_spent = pocketHr.getTimeSpentString(onDutyInMiliSeconds);
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
                    pocketHr.alertMessage(
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
                            pocketHr.DataSave(
                                    databaseHandler,
                                    myRealm,
                                    "out",
                                    "attendance",
                                    true,
                                    locationEditText.getText().toString());

                        utils.setSharedPreference(context, "out", Constants.CHECKED_STATE);
                        utils.setSharedPreference(context, "false", Constants.EXIT_STATAUS);

                     // PocketHr.startService(MainActivity.this, context,MyLocationListner.class,"STOP SETVICE");

                        resetData();
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
            PocketHr.snackBarMessage(coordinatorLayout,"Location is required",Color.RED);
            return false;
        }
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void AppState(boolean state) {
        try {
            if (state) {
                if (exitState()) {
                    PocketHr.Visibility(relativeLayout_checkin,null,null ,View.VISIBLE);
                    PocketHr.Visibility(relativeLayout_checkout,null,null ,View.VISIBLE);
                    PocketHr.Visibility(null,textView_count,null ,View.VISIBLE);
                }

                PocketHr.setBackgroundDrawable(context,relativeLayout_checkin,null,null,R.drawable.button_background_2_click);
                PocketHr.setBackgroundDrawable(context,relativeLayout_checkout,null,null,R.drawable.button_background_3);

                pocketHr.blinkIcon(image_out, 0);

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
            PocketHr.setImageDrawable(context,null,btn_in,R.drawable.button_background_2);
            utils.setSharedPreference(context, String.valueOf(StartTime), Constants.START_TIMESTAMP);
        }
    }

    public static String location(Activity activity){
        JSONObject jsonObject = null;
        if ( ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
             ActivityCompat.checkSelfPermission(activity,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = new Criteria();
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                jsonObject = new JSONObject();

                try {
                    jsonObject.put("lat", lat);
                    jsonObject.put("lng", lng);
                    return jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }
}
