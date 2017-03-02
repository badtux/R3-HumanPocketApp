package com.rype3.leaveapp.rype3leaveapp;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestActivity extends AppCompatActivity {


    Context context;
    Utils utils;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        context = this.getApplication();
        utils = new Utils(context);





        Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);



 //       int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

//        datesArray.clear();
//        for(int i = 1 ;i<= daysInMonth; i++){
//            datesArray.add(String.valueOf(i));
//        }
//
//        for (int j = 0; j< datesArray.size(); j++) {
//            tabLayoutDay.addTab(tabLayoutDay.newTab().setText(datesArray.get(j).toString()));
//        }
    }

    public View.OnClickListener onclick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };


}
