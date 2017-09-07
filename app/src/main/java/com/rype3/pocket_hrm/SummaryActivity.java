package com.rype3.pocket_hrm;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class SummaryActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    private TextView user_name, epf_no,leave_type, leave_category, leave_reason, from_date,to_date;
    private Button btn_cancel ,btn_done;
    private Context context;
    private Constants constants;
    Intent intent;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        widget();
        toolbar();

        Intent intent = getIntent();
        result = intent.getStringExtra("result");

        context = this.getApplicationContext();
        constants = new Constants(context);

        if (result != null){

            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("leave_type")){
                    String leaveType = jsonObject.getString("leave_type");
                    leave_type.setText(leaveType);
                }

                if (jsonObject.has("leave_category")){
                    String leaveCategory = jsonObject.getString("leave_category");
                    leave_category.setText(leaveCategory);
                }

                if (jsonObject.has("leave_reason")){
                    String leaveReason = jsonObject.getString("leave_reason");
                    leave_reason.setText(leaveReason);
                }

                if (jsonObject.has("leave_from")){
                    String fromDate = jsonObject.getString("leave_from");
                    from_date.setText(fromDate);
                }

                if (jsonObject.has("leave_to")){
                    String toDate = jsonObject.getString("leave_to");
                    to_date.setText(toDate);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void widget(){

        user_name = (TextView) findViewById(R.id.full_name);
        epf_no = (TextView) findViewById(R.id.epf_no);
        leave_type = (TextView) findViewById(R.id.leave_type);
        leave_category = (TextView) findViewById(R.id.leave_category);
        leave_reason = (TextView) findViewById(R.id.leave_reason);
        from_date = (TextView) findViewById(R.id.from_date);
        to_date = (TextView) findViewById(R.id.to_date);

        btn_cancel = (Button) findViewById(R.id.cancel);
        btn_cancel.setOnClickListener(onclick);

        btn_done = (Button) findViewById(R.id.done);
        btn_done.setOnClickListener(onclick);
    }


    public View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == btn_cancel){
                intent = new Intent(SummaryActivity.this,LeaveActivity.class);
                startActivity(intent);
                finish();
            }

            if (v == btn_done){

                if (checkConnection()) {
                    new ProcressAsyncTaskLeave(
                            SummaryActivity.this,
                            constants.urls(1),
                            "",//epf
                            null,
                            "",//epf
                            "POST", 1, "1.0", null,
                            leave_type.getText().toString(),
                            leave_category.getText().toString(),
                            from_date.getText().toString(),
                            to_date.getText().toString(),
                            "",//token
                            leave_reason.getText().toString(),
                            "" //language
                    ).execute();
                }
            }
        }
    };

    public void parseJsonResponse(final String result) {
        if (result != null) {
            Log.e("Result : " ,result);

            JSONObject jsonObjectResult = null;
            try {
                jsonObjectResult = new JSONObject(result);

                boolean status = jsonObjectResult.getBoolean("status");

                if (status){

                    intent = new Intent(SummaryActivity.this, LeaveActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(context,"Error" ,Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setSubtitle("");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher_m);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
    }

    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {
            return true;
        }else{
            Toast.makeText(context,"Connection error" ,Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onResume () {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }
}
