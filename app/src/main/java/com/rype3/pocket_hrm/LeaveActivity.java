package com.rype3.pocket_hrm;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class LeaveActivity extends AppCompatActivity {

    TextView select_leave_type,leave_category,reason,from,to;
    EditText et_from,et_to;
    Spinner reason_spinner;
    Button next_btn,calender_1,calender_2;
    RadioButton half_day_m,half_day_e,full_day,casual,annual,medical,no_pay,selectType,selectCategory;
    RadioGroup radioGroupType,radioGroupCategory;
    Toolbar toolbar;
    Intent intent;
    private Calendar calendar;
    private int hour,minute,date,month,year;
    RelativeLayout relativeLayout_to;
    int selectedTypeId,selectedcategoryId, leaveType_position = 0,leaveCategory_position = 0,p_reason = 0;

    private String[] leaveTypeArray = {"","HALF DAY MORNING","HALF DAY EVENING","FULL DAY"};
    private String[] leaveCategoryArray = {"","CASUAL","ANNUAL","MEDICAL","NO PAY"};
    String [] array_reason;
    Context context;
    long todayTimestamp;
    Calendar calender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        widget();
        date();
        toolbar();

        context = getApplicationContext();
        array_reason = getResources().getStringArray(R.array.e_reason);

        ArrayAdapter<String> driverNameAdapter = new ArrayAdapter<String>(LeaveActivity.this, R.layout.spinner_name, array_reason);
        driverNameAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        reason_spinner.setAdapter(driverNameAdapter);

        reason_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                p_reason= position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void widget(){
        select_leave_type = (TextView) findViewById(R.id.title_leave_type);
        leave_category = (TextView) findViewById(R.id.title_leave_category);
        reason = (TextView) findViewById(R.id.title_leave_reason);
        from = (TextView) findViewById(R.id.tv_from);
        to = (TextView) findViewById(R.id.tv_to);

        et_from = (EditText) findViewById(R.id.editText_from);
        et_from.setInputType(InputType.TYPE_NULL);

        et_to = (EditText) findViewById(R.id.editText_to);
        et_to.setInputType(InputType.TYPE_NULL);

        reason_spinner = (Spinner) findViewById(R.id.spinner);

        relativeLayout_to = (RelativeLayout) findViewById(R.id.relative_layout_to);

        next_btn = (Button) findViewById(R.id._next_1);
        next_btn.setOnClickListener(onclick);

        calender_1 = (Button) findViewById(R.id.date_icon);
        calender_1.setOnClickListener(onclick);

        calender_2 = (Button) findViewById(R.id.date_icon_1);
        calender_2.setOnClickListener(onclick);

        half_day_m = (RadioButton) findViewById(R.id.cb_h_m);
        half_day_e = (RadioButton) findViewById(R.id.cb_h_e);
        full_day = (RadioButton) findViewById(R.id.cb_f_d);

        casual = (RadioButton) findViewById(R.id.cb_casual);
        annual = (RadioButton) findViewById(R.id.cb_annual);
        medical = (RadioButton) findViewById(R.id.cb_medical);
        no_pay = (RadioButton) findViewById(R.id.cb_no_pay);

        radioGroupType = (RadioGroup) findViewById(R.id.radio_leave_type);
        radioGroupCategory = (RadioGroup) findViewById(R.id.radio_leave_category);

        selectedTypeId = radioGroupType.getCheckedRadioButtonId();
        selectedcategoryId = radioGroupType.getCheckedRadioButtonId();
        selectType = (RadioButton) findViewById(selectedTypeId);
        selectCategory = (RadioButton) findViewById(selectedcategoryId);
//
//
//
//
//        radioGroupCategory = (RadioGroup) findViewById(R.id.radio_leave_category);

        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.cb_h_m) {
                    leaveType_position = 1;
                    relativeLayout_to.setVisibility(View.INVISIBLE);
                    from.setText("DATE");
                } else if(checkedId == R.id.cb_h_e) {
                    leaveType_position = 2;
                    relativeLayout_to.setVisibility(View.INVISIBLE);
                    from.setText("DATE");
                } else if (checkedId == R.id.cb_f_d) {
                    relativeLayout_to.setVisibility(View.VISIBLE);
                    from.setText("FROM");
                    leaveType_position = 3;
                }
            }
        });

        radioGroupCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.cb_casual) {
                    leaveCategory_position = 1;
                } else if(checkedId == R.id.cb_annual) {
                    leaveCategory_position = 2;
                } else if (checkedId == R.id.cb_medical) {
                    leaveCategory_position = 3;
                }else if (checkedId == R.id.cb_no_pay) {
                    leaveCategory_position = 4;
                }
            }
        });
    }

    public void date(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        date = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        Calendar calendarToday = new GregorianCalendar(year, month, date);
        todayTimestamp = calendarToday.getTimeInMillis()/1000;
    }

    public void toolbar() {
        toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setSubtitle("");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_m);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == next_btn){
               if (validation()) {
                   intent = new Intent(LeaveActivity.this,SummaryActivity.class);
                   intent.putExtra("result" , String.valueOf(setData(leaveTypeArray[leaveType_position], leaveCategoryArray[leaveCategory_position], array_reason[p_reason], et_from.getText().toString(), et_to.getText().toString())));
                   startActivity(intent);
                   finish();
               }
                Log.e ("TAG : " , String.valueOf(setData(leaveTypeArray[leaveType_position], leaveCategoryArray[leaveCategory_position], array_reason[p_reason], et_from.getText().toString(), et_to.getText().toString())));
            }

            if (v == calender_1){
                showDatePicker(0);
            }

            if (v == calender_2){
                showDatePicker(1);
            }

        }
    };

    private void getDate(int year, int month, int day ,int position) {

        switch (position){
            case 0:
                String  currentDate_1 = String.valueOf(month + 1) + "/" + day + "/" + year;
                et_from.setText(currentDate_1);
                break;

            case 1:
                String  currentDate_2 = String.valueOf(month + 1) + "/" + day + "/" + year;
                et_to.setText(currentDate_2);
                break;
        }
    }

    private void showDatePicker(int position) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */

        switch (position) {

            case 0:
                date.setCallBack(ondate_1);
                date.show(this.getFragmentManager(), "Date Picker");
                break;
            case 1:
                date.setCallBack(ondate_2);
                date.show(this.getFragmentManager(), "Date Picker");
                break;
        }
    }


    DatePickerDialog.OnDateSetListener ondate_1 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            view.setCalendarViewShown(false);
            getDate(year, monthOfYear, dayOfMonth,0);
        }
    };

    DatePickerDialog.OnDateSetListener ondate_2 = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            view.setCalendarViewShown(false);
            getDate(year, monthOfYear, dayOfMonth,1);
        }
    };

    public boolean validation(){

        if (leaveType_position == 0){
            message(context, "Error select leave type");
            select_leave_type.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else{
            select_leave_type.setTextColor(getResources().getColor(R.color.black));
        }

        if (leaveCategory_position == 0){
            message(context, "Error leave category");
            leave_category.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else{
            leave_category.setTextColor(getResources().getColor(R.color.black));
        }

        if (p_reason == 0){
            message(context, "Error leave reason");
            reason.setTextColor(getResources().getColor(R.color.red));
            return  false;
        }else{
            reason.setTextColor(getResources().getColor(R.color.black));
        }

        if (et_from.getText().toString().isEmpty()){

            if (leaveType_position == 1 || leaveType_position == 2) {
                message(context, "Error date");
            }else{
                message(context, "Error from date");
            }
            from.setTextColor(getResources().getColor(R.color.red));
            return  false;
        }else{
            from.setTextColor(getResources().getColor(R.color.black));
        }

        if (leaveType_position != 1 && leaveType_position != 2) {

            if (et_to.getText().toString().isEmpty()) {
                message(context, "Error to date");
                to.setTextColor(getResources().getColor(R.color.red));
                return false;
            } else {
                to.setTextColor(getResources().getColor(R.color.black));
            }
        }

        String[] fromDate = et_from.getText().toString().split("/");

        int month = Integer.parseInt(fromDate[0]);
        int date =  Integer.parseInt(fromDate[1]);
        int year =  Integer.parseInt(fromDate[2]);

        Calendar calendarFrom = new GregorianCalendar(year, (month-1), date);

        long selectedTimestamp = calendarFrom.getTimeInMillis()/1000;

        Date last30day = new Date(todayTimestamp*1000 - 2592000000L); // 30 * 24 * 60 * 60 * 1000 30 days
        long lstday = last30day.getTime();

        if (lstday/1000 > selectedTimestamp){

            if (leaveType_position == 1 || leaveType_position == 2) {
                message(context, "Error date");
            }else{
                message(context, "Error from date");
            }
            from.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else{
            from.setTextColor(getResources().getColor(R.color.black));
        }

        if (leaveType_position == 3) {
            String[] toDate = et_to.getText().toString().split("/");

            int to_month = Integer.parseInt(toDate[0]);
            int to_date = Integer.parseInt(toDate[1]);
            int to_year = Integer.parseInt(toDate[2]);

            String[] from_Date = et_from.getText().toString().split("/");

            int from_month = Integer.parseInt(from_Date[0]);
            int from_date = Integer.parseInt(from_Date[1]);
            int from_year = Integer.parseInt(from_Date[2]);

            Calendar calendarFromdate = new GregorianCalendar(from_year, (from_month - 1), from_date);

            long selectedFromTimestamp = calendarFromdate.getTimeInMillis() / 1000;

            Calendar calendarTodate = new GregorianCalendar(to_year, (to_month - 1), to_date);

            long selectedToTimestamp = calendarTodate.getTimeInMillis() / 1000;

            if (selectedToTimestamp < selectedFromTimestamp) {
                message(context, "Error date");
                to.setTextColor(getResources().getColor(R.color.red));
                return false;
            } else {
                to.setTextColor(getResources().getColor(R.color.black));
            }

            if (lstday / 1000 > selectedToTimestamp) {
                message(context, "Error date");
                to.setTextColor(getResources().getColor(R.color.red));
                return false;
            } else {
                to.setTextColor(getResources().getColor(R.color.black));
            }
        }
        return true;
    }

    private void message(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.leave_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_leave) {
            intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private JSONObject setData(String leaveType, String leaveCategory, String leaveReason, String from, String to){
        JSONObject jsonObject = null;

        jsonObject = new JSONObject();
        try {
            jsonObject.put("leave_type",leaveType);
            jsonObject.put("leave_category",leaveCategory);
            jsonObject.put("leave_reason",leaveReason);
            jsonObject.put("leave_from",from);
            jsonObject.put("leave_to",to);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
