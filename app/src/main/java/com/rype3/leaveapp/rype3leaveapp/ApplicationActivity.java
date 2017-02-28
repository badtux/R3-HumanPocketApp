package com.rype3.leaveapp.rype3leaveapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ApplicationActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{


    private String[] leaveReasonStringArray ;
    private Button next ,btn_half_day_morning,btn_half_day_evening,btn_full_day ,help,btn_exit;
   // private ImageView from_key,to_key;
    private TextView titleLeaveType, titleLeaveCategory ,leave_reason,UserName;
    private Intent intent = null;
    private RadioButton casual,annual,medical,no_pay;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer = null,mediaPlayer_2 = null;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private RadioGroup radioGroup;
    Utils utils;
    Context context;
    private Long tsLong;
    private String ts;

    private String[] leaveTypeArray = {"","first","second","full_day"};
    private String[] leaveCategoryArray = {"","casual","annual","medical","nopay"};

    private int leaveTypePosition = 0;
    private int leavecategoryPosition = 0;
    ArrayList<String> leaveReasonStringList;

    int tabPosition = 0 ;

    private Button left_btn,right_btn;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        context = this.getApplication();
        utils = new Utils(context);
        widget(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));

        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                leaveReasonStringArray = getResources().getStringArray(R.array.s_reason);
                break;

            case "t":
                leaveReasonStringArray = getResources().getStringArray(R.array.t_reason);
                break;

            case "e":
                leaveReasonStringArray = getResources().getStringArray(R.array.e_reason);
                break;
        }


        if (utils.getBoolean(context,Constants.USERNAME)){
            UserName.setText(utils.getSharedPreference(context,Constants.USERNAME) +"\t\t"+utils.getSharedPreference(context,Constants.EPF_NUMBER));
        }else{
            UserName.setText("USER NAME TO BE ADD");
        }

        tsLong = System.currentTimeMillis() / 1000;
        ts = tsLong.toString();

        leaveReasonStringList = new ArrayList<String>();
        leaveReasonStringList.clear();

        for (int i = 0; i< leaveReasonStringArray.length; i++){
            leaveReasonStringList.add(leaveReasonStringArray[i]);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.radio_annual) {
                    if (playSound(0)) {
                        leavecategoryPosition = 2;
                        annual.setTextColor(Color.parseColor("#76FF03"));
                        casual.setTextColor(Color.parseColor("#FFFFFF"));
                        medical.setTextColor(Color.parseColor("#FFFFFF"));
                        no_pay.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                } else if(checkedId == R.id.radio_casual) {
                    if (playSound(0)) {
                        leavecategoryPosition = 1;
                        annual.setTextColor(Color.parseColor("#FFFFFF"));
                        casual.setTextColor(Color.parseColor("#76FF03"));
                        medical.setTextColor(Color.parseColor("#FFFFFF"));
                        no_pay.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                } else if (checkedId == R.id.radio_medical){
                    if (playSound(0)) {
                        leavecategoryPosition = 3;
                        annual.setTextColor(Color.parseColor("#FFFFFF"));
                        casual.setTextColor(Color.parseColor("#FFFFFF"));
                        medical.setTextColor(Color.parseColor("#76FF03"));
                        no_pay.setTextColor(Color.parseColor("#FFFFFF"));
                    }
                }else if (checkedId == R.id.radio_no_pay){
                    leavecategoryPosition = 4;
                    if (playSound(0)) {
                        annual.setTextColor(Color.parseColor("#FFFFFF"));
                        casual.setTextColor(Color.parseColor("#FFFFFF"));
                        medical.setTextColor(Color.parseColor("#FFFFFF"));
                        no_pay.setTextColor(Color.parseColor("#76FF03"));
                    }
                }
            }

        });

        intent = getIntent();
        if (intent != null) {
            String jsonResult = intent.getStringExtra("json");
            JSONObject jsonObject2 = null;
            try {
                jsonObject2 = new JSONObject(jsonResult);
                int leaveCategoryPosition1 = Integer.parseInt(jsonObject2.getString("leaveCategoryPosition"));
                int leaveTypePosition1 = Integer.parseInt(jsonObject2.getString("leaveTypePosition"));
                final                                                                                                                                                   int reasonPosition1 = Integer.parseInt(jsonObject2.getString("reasonPosition"));

                if (leaveTypePosition1 ==1){
                    leaveTypePosition = leaveTypePosition1;
                    btn_full_day.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_half_day_morning.setTextColor(Color.parseColor("#76FF03"));
                    btn_half_day_evening.setTextColor(Color.parseColor("#FFFFFF"));


                }else if (leaveTypePosition1 ==2){

                    leaveTypePosition = leaveTypePosition1;
                    btn_full_day.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_half_day_evening.setTextColor(Color.parseColor("#76FF03"));
                    btn_half_day_morning.setTextColor(Color.parseColor("#FFFFFF"));


                }else if (leaveTypePosition1 ==3){
                    leaveTypePosition = leaveTypePosition1;
                    btn_half_day_morning.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_half_day_evening.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_full_day.setTextColor(Color.parseColor("#76FF03"));
                }



                if (leaveCategoryPosition1 ==1){

                    leavecategoryPosition = leaveCategoryPosition1;
                    casual.setChecked(true);

                    annual.setTextColor(Color.parseColor("#FFFFFF"));
                    casual.setTextColor(Color.parseColor("#76FF03"));
                    medical.setTextColor(Color.parseColor("#FFFFFF"));
                    no_pay.setTextColor(Color.parseColor("#FFFFFF"));

                }else if (leaveCategoryPosition1 ==2){

                    leavecategoryPosition = leaveCategoryPosition1;
                    annual.setChecked(true);

                    annual.setTextColor(Color.parseColor("#76FF03"));
                    casual.setTextColor(Color.parseColor("#FFFFFF"));
                    medical.setTextColor(Color.parseColor("#FFFFFF"));
                    no_pay.setTextColor(Color.parseColor("#FFFFFF"));

                }else if (leaveCategoryPosition1 ==3){

                    leavecategoryPosition = leaveCategoryPosition1;
                    medical.setChecked(true);

                    annual.setTextColor(Color.parseColor("#FFFFFF"));
                    casual.setTextColor(Color.parseColor("#FFFFFF"));
                    medical.setTextColor(Color.parseColor("#76FF03"));
                    no_pay.setTextColor(Color.parseColor("#FFFFFF"));

                }else if (leaveCategoryPosition1 ==4){

                    leavecategoryPosition = leaveCategoryPosition1;
                    no_pay.setChecked(true);

                    annual.setTextColor(Color.parseColor("#FFFFFF"));
                    casual.setTextColor(Color.parseColor("#FFFFFF"));
                    medical.setTextColor(Color.parseColor("#FFFFFF"));
                    no_pay.setTextColor(Color.parseColor("#76FF03"));
                }

                new Handler().postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                tabLayout.getTabAt(reasonPosition1).select();
                            }
                        }, 100);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i< leaveReasonStringArray.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(leaveReasonStringArray[i]));
        }
        tabLayout.setTabGravity(TabLayout.MODE_SCROLLABLE);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();

                if (tabPosition  == 0){
                    left_btn.setVisibility(View.INVISIBLE);
                }else{
                    left_btn.setVisibility(View.VISIBLE);
                }


                if (tabPosition  == leaveReasonStringArray.length-1){
                    right_btn.setVisibility(View.INVISIBLE);
                }else {
                    right_btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void widget(String languageType){
         titleLeaveType = (TextView) findViewById(R.id.title_leave_type);
         UserName = (TextView) findViewById(R.id.title_userName);
         titleLeaveCategory = (TextView) findViewById(R.id.title_leave_category);
         leave_reason = (TextView) findViewById(R.id.textView_leave_reason);

         btn_full_day = (Button) findViewById(R.id.btn_full_day);
         btn_full_day.setOnClickListener(onclick);

         btn_half_day_morning = (Button) findViewById(R.id.btn_half_day_morning);
         btn_half_day_morning.setOnClickListener(onclick);

         btn_half_day_evening = (Button) findViewById(R.id.btn_half_day_evening);
         btn_half_day_evening.setOnClickListener(onclick);

         help = (Button) findViewById(R.id.button_help);
         help.setOnClickListener(onclick);

         next = (Button) findViewById(R.id.button_next);
         next.setOnClickListener(onclick);

         btn_exit = (Button) findViewById(R.id.button_exit);
         btn_exit.setOnClickListener(onclick);

         casual = (RadioButton) findViewById(R.id.radio_casual);
         annual = (RadioButton) findViewById(R.id.radio_annual);
         medical = (RadioButton) findViewById(R.id.radio_medical);
         no_pay = (RadioButton) findViewById(R.id.radio_no_pay);

         mediaPlayer = MediaPlayer.create(this, R.raw.click);
         mediaPlayer_1 = MediaPlayer.create(this, R.raw.click_2);
         mediaPlayer_2 = MediaPlayer.create(this, R.raw.error);

         coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

         radioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        left_btn = (Button) findViewById(R.id.button_left);
        left_btn.setOnClickListener(onclick);

        right_btn = (Button) findViewById(R.id.button_right);
        right_btn.setOnClickListener(onclick);

        switch (languageType){
            case "s":
                titleLeaveType.setText(getString(R.string.s_leave_type));
                titleLeaveCategory.setText(getString(R.string.s_leave_category));
                btn_full_day.setText(getString(R.string.s_full_day));
                btn_half_day_morning.setText(getString(R.string.s_m_half_day));
                btn_half_day_evening.setText(getString(R.string.s_e_half_day));
                casual.setText(getString(R.string.s_leave_casual) +" (" +utils.getSharedPreference(context,Constants.CASUAL_LEAVE_COUNT)+")");
                annual.setText(getString(R.string.s_leave_annual) +" (" +utils.getSharedPreference(context,Constants.ANUAL_LEAVE_COUNT)+")");
                medical.setText(getString(R.string.s_leave_medical) +" (" +utils.getSharedPreference(context,Constants.MEDICAL_LEAVE_COUNT)+")");
                no_pay.setText(getString(R.string.s_leave_no_pay) +" (" +utils.getSharedPreference(context,Constants.NOPAY_LEAVE_COUNT)+")");
                leave_reason.setText(getString(R.string.s_leave_reason));
                next.setText(getString(R.string.s_next));
                help.setText(getString(R.string.s_help));
                btn_exit.setText(getString(R.string.s_exit));
                break;

            case "e":
                titleLeaveType.setText(getString(R.string.e_leave_type));
                titleLeaveCategory.setText(getString(R.string.e_leave_category));
                btn_full_day.setText(getString(R.string.e_full_day));
                btn_half_day_morning.setText(getString(R.string.e_m_half_day));
                btn_half_day_evening.setText(getString(R.string.e_e_half_day));

                casual.setText(getString(R.string.e_leave_casual) +" (" +utils.getSharedPreference(context,Constants.CASUAL_LEAVE_COUNT)+")");
                annual.setText(getString(R.string.e_leave_annual) +" (" +utils.getSharedPreference(context,Constants.ANUAL_LEAVE_COUNT)+")");
                medical.setText(getString(R.string.e_leave_medical) +" (" +utils.getSharedPreference(context,Constants.MEDICAL_LEAVE_COUNT)+")");
                no_pay.setText(getString(R.string.e_leave_no_pay) +" (" +utils.getSharedPreference(context,Constants.NOPAY_LEAVE_COUNT)+")");

                leave_reason.setText(getString(R.string.e_leave_reason));

                next.setText(getString(R.string.e_next));
                help.setText(getString(R.string.e_help));
                btn_exit.setText(getString(R.string.e_exit));
                break;

            case "t":
                titleLeaveType.setText(getString(R.string.t_leave_type));
                titleLeaveCategory.setText(getString(R.string.t_leave_category));
                btn_full_day.setText(getString(R.string.t_full_day));
                btn_half_day_morning.setText(getString(R.string.t_m_half_day));
                btn_half_day_evening.setText(getString(R.string.t_e_half_day));

                casual.setText(getString(R.string.t_leave_casual) +" (" +utils.getSharedPreference(context,Constants.CASUAL_LEAVE_COUNT)+")");
                annual.setText(getString(R.string.t_leave_annual) +" (" +utils.getSharedPreference(context,Constants.ANUAL_LEAVE_COUNT)+")");
                medical.setText(getString(R.string.t_leave_medical) +"(" +utils.getSharedPreference(context,Constants.MEDICAL_LEAVE_COUNT)+")");
                no_pay.setText(getString(R.string.t_leave_no_pay) +" (" +utils.getSharedPreference(context,Constants.NOPAY_LEAVE_COUNT)+")");

                leave_reason.setText(getString(R.string.t_leave_reason));

                btn_exit.setText(getString(R.string.t_exit));
                next.setText(getString(R.string.t_next));
                help.setText(getString(R.string.t_help));
                break;
        }
    }

    public View.OnClickListener onclick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == next){
                if (validation(utils.getSharedPreference(context,Constants.LANGUAGE_TYPE))) {
                    if (playSound(1)) {

                        if(leaveTypePosition ==3) {
                            intent = new Intent(ApplicationActivity.this, FromDateActivity.class);
                        }else{
                            intent = new Intent(ApplicationActivity.this, HalfDayActivity.class);
                        }
                        intent.putExtra("json", String.valueOf(SaveJson(
                                ts,
                               // utils.getSharedPreference(context, Constants.TOKEN),
                                "token",
                                leaveTypeArray[leaveTypePosition],
                                leaveCategoryArray[leavecategoryPosition],
                                "",
                                "",
                                leaveReasonStringArray[tabPosition])));
                        startActivity(intent);
                        finish();
                    }
                }else{
                    playSound(2);
                }
            }
            if (v == help){
                if (playSound(1)) {
                }
            }

            if (v == btn_exit){
                if (playSound(1)) {
                    intent = new Intent(ApplicationActivity.this, LanguageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            if (v == btn_full_day){
                if (playSound(0)) {
                    leaveTypePosition = 3;
                    btn_half_day_morning.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_half_day_evening.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_full_day.setTextColor(Color.parseColor("#76FF03"));
                }
            }

            if (v == btn_half_day_morning){
                if (playSound(0)) {
                    leaveTypePosition = 1;

                    btn_full_day.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_half_day_morning.setTextColor(Color.parseColor("#76FF03"));
                    btn_half_day_evening.setTextColor(Color.parseColor("#FFFFFF"));

                }
            }

            if (v == btn_half_day_evening){
                if (playSound(0)) {
                    leaveTypePosition = 2;

                    btn_full_day.setTextColor(Color.parseColor("#FFFFFF"));
                    btn_half_day_evening.setTextColor(Color.parseColor("#76FF03"));
                    btn_half_day_morning.setTextColor(Color.parseColor("#FFFFFF"));
                }
            }

            if (v == left_btn){
                new Handler().postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                tabLayout.getTabAt(tabPosition-1).select();
                            }
                        }, 100);
            }

            if (v == right_btn){
                new Handler().postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                tabLayout.getTabAt(tabPosition+1).select();
                            }
                        }, 100);
            }
        }
    };


    public boolean validation(String language){

        if (leaveTypePosition == 0){
            titleLeaveType.setTextColor(getResources().getColor(R.color.red));
            switch (language) {
                case "s":
                    ViewMessage(getString(R.string.s_leave_type), 0);
                    break;

                case "t":
                    ViewMessage(getString(R.string.t_leave_type), 0);
                    break;

                case "e":
                    ViewMessage(getString(R.string.e_leave_type), 0);
                    break;
            }
            return false;
        }else{
            titleLeaveType.setTextColor(getResources().getColor(R.color.white));
        }

        if (leavecategoryPosition == 0){
            titleLeaveCategory.setTextColor(getResources().getColor(R.color.red));
            switch (language) {
                case "s":
                    ViewMessage(getString(R.string.s_leave_category), 0);
                    break;

                case "t":
                    ViewMessage(getString(R.string.t_leave_category), 0);
                    break;

                case "e":
                    ViewMessage(getString(R.string.e_leave_category), 0);
                    break;
            }
            return false;
        }else{
            titleLeaveCategory.setTextColor(getResources().getColor(R.color.white));
        }


        if (tabPosition == 0){
            leave_reason.setTextColor(getResources().getColor(R.color.red));
            switch (language) {
                case "s":
                    ViewMessage(getString(R.string.s_leave_reason), 0);
                    break;

                case "t":
                    ViewMessage(getString(R.string.t_leave_reason), 0);
                    break;

                case "e":
                    ViewMessage(getString(R.string.e_leave_reason), 0);
                    break;
            }
            return false;
        }else{
            leave_reason.setTextColor(getResources().getColor(R.color.white));
        }

        return true;
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            return true;
        } else {
            ViewMessage("You don't have internet connection",0);
        }
        return false;
    }

    public boolean playSound(int position) {
            try {
                switch (position) {
                    case 0:
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = MediaPlayer.create(ApplicationActivity.this, R.raw.click);
                        }
                        mediaPlayer.start();
                        break;

                    case 1:
                        if (mediaPlayer_1.isPlaying()) {
                            mediaPlayer_1.stop();
                            mediaPlayer_1.release();
                            mediaPlayer_1 = MediaPlayer.create(ApplicationActivity.this, R.raw.click_2);
                        }
                        mediaPlayer_1.start();
                        break;

                    case 2:
                        if (mediaPlayer_2.isPlaying()) {
                            mediaPlayer_2.stop();
                            mediaPlayer_2.release();
                            mediaPlayer_2 = MediaPlayer.create(ApplicationActivity.this, R.raw.error);
                        } mediaPlayer_2.start();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            ViewMessage("Connection Success",1);
        } else {
            ViewMessage("You don't have internet connection",0);
        }
    }

    public void ViewMessage(String message, int position){

        switch (position){
            case 0:
                snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)sbView.getLayoutParams();
                params.gravity = Gravity.TOP;
                sbView.setLayoutParams(params);
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setGravity(1);
                textView.setTextColor(Color.RED);
                snackbar.show();
                break;
            case 1:
                snackbar = Snackbar.make(coordinatorLayout,message , Snackbar.LENGTH_SHORT);
                View sbView1 = snackbar.getView();
                CoordinatorLayout.LayoutParams params1=(CoordinatorLayout.LayoutParams)sbView1.getLayoutParams();
                params1.gravity = Gravity.TOP;
                sbView1.setLayoutParams(params1);
                TextView textView1 = (TextView) sbView1.findViewById(android.support.design.R.id.snackbar_text);
                textView1.setTextColor(Color.GREEN);
                snackbar.show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }


    public JSONObject SaveJson(String ts,String token,String leaveType,String leaveCategory,String fromDate,String toDate,String reason){

        JSONObject jsonObject =null;

        try {
            jsonObject = new JSONObject();
            jsonObject.put("timestamp",ts);
            jsonObject.put("token",token);

            jsonObject.put("leaveType",leaveType);
            jsonObject.put("leaveTypePosition", String.valueOf(leaveTypePosition));

            jsonObject.put("leaveCategory",leaveCategory);
            jsonObject.put("leaveCategoryPosition", String.valueOf(leavecategoryPosition));

            jsonObject.put("fromDate",fromDate);
            jsonObject.put("toDate",toDate);

            jsonObject.put("reason",reason);
            jsonObject.put("reasonPosition", String.valueOf(tabPosition));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
