
package com.rype3.leaveapp.rype3leaveapp;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Application;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.provider.Settings;
        import android.support.design.widget.TabLayout;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.lang.reflect.InvocationTargetException;
        import java.lang.reflect.Method;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.GregorianCalendar;

public class ToDateActivity extends Activity {
    private Button btn_back, btn_next,btn_exit ;
    private TextView tv_date;
    private TextView tv_month;
    private TextView tv_weekday;
    private TextView tv_to;
    private TextView tv_year;
    Utils utils;
    Context context;
    private Intent intent = null;
    private String jsonResult ,from_date,to_date;
    private String[] MonthStringArray ;
    private String[] WeekStringArray ;
    private TabLayout tabLayout,tabLayoutDay;
    private String[] monthArray;
    int monthPosition = 0,datePosition = 0,getYearPosition = 0,year,month,day,toDay;
    long todayTimestamp ,selectedTimestamp;
    ArrayList datesArray;
    private Button left_btn_month, right_btn_month, left_btn_date, right_btn_date;
    Calendar mycal;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer = null,mediaPlayer_2 = null;
    // To keep track of activity's window focus
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    private int activityAction = 0;

    private ProgrammaticallyExitMessage programmaticallyExitMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_date_activity);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = this.getApplication();
        utils =  new Utils(context);

    //    programmaticallyExitMessage = new ProgrammaticallyExitMessage();
    //    programmaticallyExitMessage.ProgrammaticallyExitMessage(ToDateActivity.this,context,0,utils.getSharedPreference(context,Constants.LANGUAGE_TYPE));

        Date();
        widget();

        datesArray = new ArrayList();

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click_2);
        mediaPlayer_2 = MediaPlayer.create(this, R.raw.error);

        intent = getIntent();
        jsonResult = intent.getStringExtra("json");
        from_date = intent.getStringExtra("from_date");

        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                monthArray = getResources().getStringArray(R.array.s_month);
                break;

            case "t":
                monthArray = getResources().getStringArray(R.array.t_month);
                break;

            case "e":
                monthArray = getResources().getStringArray(R.array.e_month);
                break;
        }

        for (int i = 0; i< monthArray.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(monthArray[i]));
        }
        tabLayout.setTabGravity(TabLayout.MODE_SCROLLABLE);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                monthPosition = tab.getPosition();
                playSound(0);
            //    Log.e("Month Position : ", String.valueOf(monthPosition));
            //    Log.e("Get year : ", String.valueOf(monthPosition/12%12));

                getYearPosition = monthPosition/12%12;

                month = monthPosition;

                datesArray.clear();
                getMonth(year,tab.getPosition(),day);

                if (monthPosition == 0){
                    left_btn_month.setVisibility(View.INVISIBLE);
                }else{
                    left_btn_month.setVisibility(View.VISIBLE);
                }

//                if (monthPosition == monthArray.length-1){
//                    right_btn_month.setVisibility(View.INVISIBLE);
//                }else {
//                    right_btn_month.setVisibility(View.VISIBLE);
//                }

                if (monthPosition >= 10){
                    for (int i = 0; i< monthArray.length; i++) {
                        tabLayout.addTab(tabLayout.newTab().setText(monthArray[i]));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        monthPosition = month;
                        tabLayout.getTabAt(month).select();
                        datesArray.clear();
                        getMonth(year, month,day);
                    }
                }, 100);

     //   exitActivity();
    }

    public void widget(){
        tv_date = (TextView) findViewById(R.id.textView_day);
        tv_year = (TextView) findViewById(R.id.textView_year);
        tv_month = (TextView) findViewById(R.id.textView_month);
        tv_weekday = (TextView) findViewById(R.id.textView_weekday);
        tv_to = (TextView) findViewById(R.id.textFrom);

        btn_back = (Button) findViewById(R.id.button_back);
        btn_back.setOnClickListener(onclick);

        btn_next = (Button) findViewById(R.id.button_next);
        btn_next.setOnClickListener(onclick);

        left_btn_date = (Button) findViewById(R.id.btn_left);
        left_btn_date.setOnClickListener(onclick);

        right_btn_date = (Button) findViewById(R.id.btn_right);
        right_btn_date.setOnClickListener(onclick);

        btn_exit = (Button) findViewById(R.id.button_exit);
        btn_exit.setOnClickListener(onclick);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_weekday);
        tabLayoutDay = (TabLayout) findViewById(R.id.tab_layout_day);

        left_btn_month = (Button) findViewById(R.id.button_left);
        left_btn_month.setOnClickListener(onclick);

        right_btn_month = (Button) findViewById(R.id.button_right);
        right_btn_month.setOnClickListener(onclick);

        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                MonthStringArray = getResources().getStringArray(R.array.s_month);
                WeekStringArray = getResources().getStringArray(R.array.s_week_day);

                btn_next.setText(getString(R.string.s_next));
                btn_back.setText(getString(R.string.s_back));
                tv_to.setText(getString(R.string.s_to));
                btn_exit.setText(getString(R.string.s_exit));
                break;

            case "t":
                MonthStringArray = getResources().getStringArray(R.array.t_month);
                WeekStringArray = getResources().getStringArray(R.array.t_week_day);
                btn_next.setText(getString(R.string.t_next));
                btn_back.setText(getString(R.string.t_back));
                tv_to.setText(getString(R.string.t_to));
                btn_exit.setText(getString(R.string.t_exit));
                break;

            case "e":
                MonthStringArray = getResources().getStringArray(R.array.e_month);
                WeekStringArray = getResources().getStringArray(R.array.e_week_day);
                btn_next.setText(getString(R.string.e_next));
                btn_back.setText(getString(R.string.e_back));
                tv_to.setText(getString(R.string.e_to));
                btn_exit.setText(getString(R.string.e_exit));
                break;
        }
        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 20);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness =1f;// 100 / 100.0f;
        getWindow().setAttributes(lp);
    }

    public void Date(){
        mycal = Calendar.getInstance();
        year = mycal.get(Calendar.YEAR);
        month = mycal.get(Calendar.MONTH);
        day = mycal.get(Calendar.DAY_OF_MONTH);
        mycal.setTimeInMillis(System.currentTimeMillis());

        Calendar calendarToday = new GregorianCalendar(year, month, day);
        todayTimestamp = calendarToday.getTimeInMillis()/1000;

        toDay = day;
    }


    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_next){
                if (playSound(1)) {
                    if (validation()) {
                        activityAction =1;
                        intent = new Intent(ToDateActivity.this, ConfarmActivity.class);
                        intent.putExtra("json", jsonResult);
                        intent.putExtra("from_date", from_date);
                        intent.putExtra("to_date", to_date);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            if (v == btn_back){
                if (playSound(1)) {
                    activityAction =1;
                    intent = new Intent(ToDateActivity.this, ApplicationActivity.class);
                    intent.putExtra("json" ,jsonResult);
                    startActivity(intent);
                    finish();
                }
            }

            if (v == btn_exit){
                if (playSound(1)) {
                    activityAction =1;
                    intent = new Intent(ToDateActivity.this, LanguageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            if (v == left_btn_month){
                new Handler().postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    tabLayout.getTabAt(monthPosition - 1).select();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    left_btn_month.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, 100);
            }

            if (v == right_btn_month){
                new Handler().postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    tabLayout.getTabAt(monthPosition + 1).select();
                                }catch (Exception e){
                                    e.printStackTrace();
                                //    right_btn_month.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, 100);
            }


            if (v == left_btn_date){
                new Handler().postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    tabLayoutDay.getTabAt(datePosition - 1).select();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    left_btn_date.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, 100);
            }

            if (v == right_btn_date){
                new Handler().postDelayed(
                        new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    tabLayoutDay.getTabAt(datePosition + 1).select();
                                }catch (Exception e){
                                    e.printStackTrace();
                                    right_btn_date.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, 100);
            }
        }
    };

    public boolean playSound(int position){
        try {
            switch (position){
                case 0:
                    if (mediaPlayer_2.isPlaying()){
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                    }
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer = MediaPlayer.create(ToDateActivity.this, R.raw.click);
                    } mediaPlayer.start();
                    break;

                case 1:
                    if (mediaPlayer_2.isPlaying()){
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                    }

                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.reset();
                        mediaPlayer_1 = MediaPlayer.create(ToDateActivity.this, R.raw.click_2);
                    } mediaPlayer_1.start();
                    break;

                case 2:
                    if (mediaPlayer_2.isPlaying()) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                        mediaPlayer_2 = MediaPlayer.create(ToDateActivity.this, R.raw.error);
                    } mediaPlayer_2.start();
                    break;
            }
        } catch(Exception e) { e.printStackTrace();
        }
        return true;
    }

    public boolean validation(){

        String message = "";
        String[] toDate = to_date.split("/");

        int date = Integer.parseInt(toDate[0]);
        int month =  Integer.parseInt(toDate[1]);
        int year =  Integer.parseInt(toDate[2]);


        String[] fromDate = from_date.split("/");

        int fdate = Integer.parseInt(fromDate[0]);
        int fmonth =  Integer.parseInt(fromDate[1]);
        int fyear =  Integer.parseInt(fromDate[2]);

        Calendar calendarFromdate = new GregorianCalendar(fyear, (fmonth-1), fdate);

        long selectedFromTimestamp = calendarFromdate.getTimeInMillis()/1000;

        Calendar calendarTodate = new GregorianCalendar(year, (month-1), date);

        selectedTimestamp = calendarTodate.getTimeInMillis()/1000;


        if (selectedTimestamp < selectedFromTimestamp){
            switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
                case "s":
                    message = getString(R.string.s_wrong_date);
                    break;

                case "e":
                    message = getString(R.string.e_wrong_date);
                    break;

                case "t":
                    message = getString(R.string.t_wrong_date);
                    break;
            }

            Toast.makeText(context,message ,Toast.LENGTH_SHORT).show();
            return false;
        }


        Date last30day = new Date(todayTimestamp*1000 - 2592000000L); // 30 * 24 * 60 * 60 * 1000 30 days
        long lstday = last30day.getTime();

        if (lstday/1000 > selectedTimestamp){

            switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
                case "s":
                    message = getString(R.string.s_wrong_date);
                    break;

                case "e":
                    message = getString(R.string.e_wrong_date);
                    break;

                case "t":
                    message = getString(R.string.t_wrong_date);
                    break;
            }

            Toast.makeText(context,message ,Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void getMonth (final int year, final int month, final int date){

        tabLayoutDay.removeAllTabs();
        datesArray.clear();
        mycal = new GregorianCalendar((year+getYearPosition), month, 1);

    //    Log.e("Leap year : ", String.valueOf(isLeapYear((year+getYearPosition))));

    //    Log.e("Month : ", String.valueOf(month%12));
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int i = 1 ;i<= daysInMonth; i++){
            datesArray.add(String.valueOf(i));
        }

        if (isLeapYear((year+getYearPosition))){
            if (month%12 ==1){
                datesArray.add(String.valueOf(29));
            }
        }

        for (int j = 0; j< datesArray.size(); j++) {
            tabLayoutDay.addTab(tabLayoutDay.newTab().setText(datesArray.get(j).toString()));
        }

        tabLayoutDay.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                datePosition = tab.getPosition();
                playSound(0);

                Log.e("Date td: ", String.valueOf(datePosition));
                //    setDate(datePosition);
                getDayofweek(datePosition);

                if (datePosition == 0){
                    left_btn_date.setVisibility(View.INVISIBLE);
                }else{
                    left_btn_date.setVisibility(View.VISIBLE);
                }

                if (datePosition == datesArray.size()-1){
                    right_btn_date.setVisibility(View.INVISIBLE);
                }else {
                    right_btn_date.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {

                        try {
                            tabLayoutDay.getTabAt(toDay - 1).select();
                            //    getMonth(year, month,day);
                        }catch (Exception e){
                            tabLayoutDay.getTabAt(2).select();
                            Log.e("TAG : ", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, 100);

        getDayofweek(toDay);
    }

    public boolean isLeapYear(int year) {

        if ((year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0) )) {
            return true;
        } else {
            return false;
        }
    }

    public void getDayofweek(int date){
        mycal = new GregorianCalendar((year+getYearPosition), (month%12), (date+1));
        int reslut = mycal.get(Calendar.DAY_OF_WEEK);
        switch (reslut) {

            case Calendar.MONDAY:
                tv_weekday.setText(WeekStringArray[0]);
                break;

            case Calendar.TUESDAY:
                tv_weekday.setText(WeekStringArray[1]);
                break;

            case Calendar.WEDNESDAY:
                tv_weekday.setText(WeekStringArray[2]);
                break;

            case Calendar.THURSDAY:
                tv_weekday.setText(WeekStringArray[3]);
                break;

            case Calendar.FRIDAY:
                tv_weekday.setText(WeekStringArray[4]);
                break;

            case Calendar.SATURDAY:
                tv_weekday.setText(WeekStringArray[5]);
                break;

            case Calendar.SUNDAY:
                tv_weekday.setText(WeekStringArray[6]);
                break;
        }

        tv_year.setText(String.valueOf((year+getYearPosition)));
        tv_month.setText(MonthStringArray[month%12].toUpperCase());
        tv_date.setText(String.valueOf(date+1));

        to_date = String.valueOf(date+1) +"/"+ String.valueOf(month%12+1 )+"/"+String.valueOf(year+getYearPosition);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME) {
            exitAlertMessageBox();
        }

        if(keyCode==KeyEvent.KEYCODE_BACK) {
            exitAlertMessageBox();
        }
        return false;
    }

    private void exitAlertMessageBox() {

        AlertDialog dialog = new AlertDialog.Builder(ToDateActivity.this)
                .setTitle("ERROR")
                .setMessage("PRESS OK")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
        Log.e("onPause() :  " , String.valueOf(activityAction));
        switch (activityAction){
            case 0:
                intent = new Intent(ToDateActivity.this, LanguageActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        currentFocus = hasFocus;

        if (!hasFocus) {
            // Method that handles loss of window focus
            collapseNow();
        }
    }

    public void collapseNow() {

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    // Use reflection to trigger a method from 'StatusBarManager'

                    Object statusBarService = getSystemService("statusbar");
                    Class<?> statusBarManager = null;

                    try {
                        statusBarManager = Class.forName("android.app.StatusBarManager");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Method collapseStatusBar = null;

                    try {

                        // Prior to API 17, the method to call is 'collapse()'
                        // API 17 onwards, the method to call is `collapsePanels()`

                        if (Build.VERSION.SDK_INT > 16) {
                            collapseStatusBar = statusBarManager .getMethod("collapsePanels");
                        } else {
                            collapseStatusBar = statusBarManager .getMethod("collapse");
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    collapseStatusBar.setAccessible(true);

                    try {
                        collapseStatusBar.invoke(statusBarService);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    // Check if the window focus has been returned
                    // If it hasn't been returned, post this Runnable again
                    // Currently, the delay is 100 ms. You can change this
                    // value to suit your needs.
                    if (!currentFocus && !isPaused) {
                        collapseNotificationHandler.postDelayed(this, 100L);
                    }
                }
            }, 300L);
        }
    }

    public void exitActivity(){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                intent = new Intent(ToDateActivity.this,LanguageActivity.class);
                startActivity(intent);
                finish();
            }

        }, 1000*60*1); // 1000ms = 1s
    }
}
