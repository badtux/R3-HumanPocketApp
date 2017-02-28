package com.rype3.leaveapp.rype3leaveapp;

        import android.app.Activity;
        import android.app.Application;
        import android.content.Context;
        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.TextView;
        import java.util.Calendar;
        import java.util.GregorianCalendar;

public class HalfDayActivity extends Activity {
    private DatePicker dpResult;
    private Button btn_back,btn_next,btn_exit;
    private TextView tv_date,tv_year,tv_month,tv_weekday, tv_to;
    Utils utils;
    Context context;
    private Intent intent = null;
    private Constants constants;
    private String jsonResult ,from_date;
    private String[] MonthStringArray ;
    private String[] WeekStringArray ;
    private MediaPlayer mediaPlayer_1 = null,mediaPlayer = null,mediaPlayer_2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_half_day);

        context = this.getApplication();
        utils =  new Utils(context);
        constants = new Constants(context);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click_2);
        mediaPlayer_2 = MediaPlayer.create(this, R.raw.error);

        intent = getIntent();
        jsonResult = intent.getStringExtra("json");

        dpResult = (DatePicker) findViewById(R.id.datePicker);
        tv_date = (TextView) findViewById(R.id.textView_day);
        tv_year = (TextView) findViewById(R.id.textView_year);
        tv_month = (TextView) findViewById(R.id.textView_month);
        tv_weekday = (TextView) findViewById(R.id.textView_weekday);
        tv_to = (TextView) findViewById(R.id.textFrom);

        btn_back = (Button) findViewById(R.id.button_back);
        btn_back.setOnClickListener(onclick);

        btn_exit = (Button) findViewById(R.id.button_exit);
        btn_exit.setOnClickListener(onclick);

        btn_next = (Button) findViewById(R.id.button_next);
        btn_next.setOnClickListener(onclick);


        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                MonthStringArray = getResources().getStringArray(R.array.s_month);
                WeekStringArray = getResources().getStringArray(R.array.s_week_day);

                btn_next.setText(getString(R.string.s_next));
                btn_back.setText(getString(R.string.s_back));
                tv_to.setText(getString(R.string.s_date));
                btn_exit.setText(getString(R.string.s_exit));
                break;

            case "t":
                MonthStringArray = getResources().getStringArray(R.array.t_month);
                WeekStringArray = getResources().getStringArray(R.array.t_week_day);
                btn_next.setText(getString(R.string.t_next));
                btn_back.setText(getString(R.string.t_back));
                tv_to.setText(getString(R.string.t_date));
                btn_exit.setText(getString(R.string.t_exit));
                break;

            case "e":
                MonthStringArray = getResources().getStringArray(R.array.e_month);
                WeekStringArray = getResources().getStringArray(R.array.e_week_day);
                btn_next.setText(getString(R.string.e_next));
                btn_back.setText(getString(R.string.e_back));
                tv_to.setText(getString(R.string.e_date));
                btn_exit.setText(getString(R.string.e_exit));
                break;
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        tv_year.setText(String.valueOf(calendar.get(Calendar.YEAR)));
        tv_month.setText(MonthStringArray[calendar.get(Calendar.MONTH)]);


        Calendar calendar1 = new GregorianCalendar(calendar.get(Calendar.YEAR),(calendar.get(Calendar.MONTH) + 1), calendar.get(Calendar.DAY_OF_MONTH)); // Note that Month value is 0-based. e.g., 0 for January.
        int reslut = calendar1.get(Calendar.DAY_OF_WEEK);
        switch (reslut) {

            case Calendar.MONDAY:
                System.out.println("It's MONDAY !");
                tv_weekday.setText(WeekStringArray[0]);
                break;

            case Calendar.TUESDAY:
                System.out.println("It's TUESDAY !");
                tv_weekday.setText(WeekStringArray[1]);
                break;

            case Calendar.WEDNESDAY:
                System.out.println("It's WEDNESDAY !");
                tv_weekday.setText(WeekStringArray[2]);
                break;

            case Calendar.THURSDAY:
                System.out.println("It's THURSDAY !");
                tv_weekday.setText(WeekStringArray[3]);
                break;

            case Calendar.FRIDAY:
                System.out.println("It's FRIDAY !");
                tv_weekday.setText(WeekStringArray[4]);
                break;

            case Calendar.SATURDAY:
                System.out.println("It's SATURDAY !");
                tv_weekday.setText(WeekStringArray[5]);
                break;

            case Calendar.SUNDAY:
                System.out.println("It's SUNDAY !");
                tv_weekday.setText(WeekStringArray[6]);
                break;
        }

        tv_date.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        //new StringBuilder().append(day).append("/").append(month).append("/").append(year)

        from_date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) +"/"+ String.valueOf(calendar.get(Calendar.MONTH) + 1)+"/"+String.valueOf(calendar.get(Calendar.YEAR));

        dpResult.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Log.e("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth+"");

                Calendar calendar = new GregorianCalendar(year,(month + 1), dayOfMonth); // Note that Month value is 0-based. e.g., 0 for January.
                int reslut = calendar.get(Calendar.DAY_OF_WEEK);
                switch (reslut) {

                    case Calendar.MONDAY:
                        System.out.println("It's MONDAY !");
                        tv_weekday.setText(WeekStringArray[0]);
                        break;

                    case Calendar.TUESDAY:
                        System.out.println("It's TUESDAY !");
                        tv_weekday.setText(WeekStringArray[1]);
                        break;

                    case Calendar.WEDNESDAY:
                        System.out.println("It's WEDNESDAY !");
                        tv_weekday.setText(WeekStringArray[2]);
                        break;

                    case Calendar.THURSDAY:
                        System.out.println("It's THURSDAY !");
                        tv_weekday.setText(WeekStringArray[3]);
                        break;

                    case Calendar.FRIDAY:
                        System.out.println("It's FRIDAY !");
                        tv_weekday.setText(WeekStringArray[4]);
                        break;

                    case Calendar.SATURDAY:
                        System.out.println("It's SATURDAY !");
                        tv_weekday.setText(WeekStringArray[5]);
                        break;

                    case Calendar.SUNDAY:
                        System.out.println("It's SUNDAY !");
                        tv_weekday.setText(WeekStringArray[6]);
                        break;
                }
                tv_date.setText(String.valueOf(dayOfMonth));
                tv_month.setText(MonthStringArray[month]);
                tv_year.setText(String.valueOf(year));

                from_date = String.valueOf(dayOfMonth) +"/"+ String.valueOf(month + 1)+"/"+String.valueOf(year);
            }
        });
    }


    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_next){
                if (playSound(1)) {
                    intent = new Intent(HalfDayActivity.this, ConfarmActivity.class);
                    intent.putExtra("json" ,jsonResult);
                    intent.putExtra("from_date" ,from_date);
                    intent.putExtra("to_date" ,from_date);
                    startActivity(intent);
                    finish();
                }
            }
            if (v == btn_back){
                if (playSound(1)) {
                    intent = new Intent(HalfDayActivity.this, ApplicationActivity.class);
                    intent.putExtra("json" ,jsonResult);
                    startActivity(intent);
                    finish();
                }
            }

            if (v == btn_exit){
                if (playSound(1)) {
                    intent = new Intent(HalfDayActivity.this, LanguageActivity.class);
                    startActivity(intent);
                    finish();
                }
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
                        mediaPlayer = MediaPlayer.create(HalfDayActivity.this, R.raw.click);
                    }
                    mediaPlayer.start();
                    break;

                case 1:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.release();
                        mediaPlayer_1 = MediaPlayer.create(HalfDayActivity.this, R.raw.click_2);
                    }
                    mediaPlayer_1.start();
                    break;

                case 2:
                    if (mediaPlayer_2.isPlaying()) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.release();
                        mediaPlayer_2 = MediaPlayer.create(HalfDayActivity.this, R.raw.error);
                    } mediaPlayer_2.start();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
