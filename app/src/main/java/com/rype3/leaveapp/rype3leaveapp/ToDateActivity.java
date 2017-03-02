
package com.rype3.leaveapp.rype3leaveapp;

        import android.app.Activity;
        import android.app.Application;
        import android.content.Context;
        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.design.widget.TabLayout;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.Calendar;
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
    int monthPosition = 0,datePosition = 0,year,month,day,toDay;
    ArrayList datesArray;
    private Button left_btn_month, right_btn_month, left_btn_date, right_btn_date;
    Calendar mycal;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer = null,mediaPlayer_2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_date);

        context = this.getApplication();
        utils =  new Utils(context);

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

                datesArray.clear();
                getMonth(year,tab.getPosition(),day);

                if (monthPosition == 0){
                    left_btn_month.setVisibility(View.INVISIBLE);
                }else{
                    left_btn_month.setVisibility(View.VISIBLE);
                }

                if (monthPosition == monthArray.length-1){
                    right_btn_month.setVisibility(View.INVISIBLE);
                }else {
                    right_btn_month.setVisibility(View.VISIBLE);
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
    }

    public void Date(){
        mycal = Calendar.getInstance();
        year = mycal.get(Calendar.YEAR);
        month = mycal.get(Calendar.MONTH);
        day = mycal.get(Calendar.DAY_OF_MONTH);
        mycal.setTimeInMillis(System.currentTimeMillis());

        toDay = day;
    }


    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_next){
                if (playSound(1)) {
                    intent = new Intent(ToDateActivity.this, ConfarmActivity.class);
                    intent.putExtra("json" ,jsonResult);
                    intent.putExtra("from_date" ,from_date);
                    intent.putExtra("to_date" ,to_date);
                    startActivity(intent);
                    finish();
                }
            }
            if (v == btn_back){
                if (playSound(1)) {
                    intent = new Intent(ToDateActivity.this, ApplicationActivity.class);
                    intent.putExtra("json" ,jsonResult);
                    startActivity(intent);
                    finish();
                }
            }

            if (v == btn_exit){
                if (playSound(1)) {
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
                                    right_btn_month.setVisibility(View.INVISIBLE);
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

    public boolean playSound(int position) {
        try {
            switch (position) {
                case 0:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = MediaPlayer.create(ToDateActivity.this, R.raw.click);
                    }
                    mediaPlayer.start();
                    break;

                case 1:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.release();
                        mediaPlayer_1 = MediaPlayer.create(ToDateActivity.this, R.raw.click_2);
                    }
                    mediaPlayer_1.start();
                    break;

                case 2:
                    if (mediaPlayer_2.isPlaying()) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.release();
                        mediaPlayer_2 = MediaPlayer.create(ToDateActivity.this, R.raw.error);
                    } mediaPlayer_2.start();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void getMonth (final int year, final int month, final int date){

        tabLayoutDay.removeAllTabs();
        datesArray.clear();
        mycal = new GregorianCalendar(year, month, 1);
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

        for(int i = 1 ;i<= daysInMonth; i++){
            datesArray.add(String.valueOf(i));
        }

        for (int j = 0; j< datesArray.size(); j++) {
            tabLayoutDay.addTab(tabLayoutDay.newTab().setText(datesArray.get(j).toString()));
        }

        tabLayoutDay.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                datePosition = tab.getPosition();

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
                        tabLayoutDay.getTabAt(toDay-1).select();

                    }
                }, 100);

        getDayofweek(toDay);
    }

    public void getDayofweek(int date){
        mycal = new GregorianCalendar(year, (monthPosition), (date+1));
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

        tv_year.setText(String.valueOf(year));
        tv_month.setText(MonthStringArray[monthPosition].toUpperCase());
        tv_date.setText(String.valueOf(date+1));

        to_date = String.valueOf(date+1) +"/"+ String.valueOf(monthPosition+1 )+"/"+String.valueOf(year);
    }
}
