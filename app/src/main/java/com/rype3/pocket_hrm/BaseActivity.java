package com.rype3.pocket_hrm;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    public Toolbar toolbar;
    private Utils utils;
    private Context context;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        configureToolbar(ToolBarName() ,ToolBarIcon());

        context = getApplicationContext();
        utils = new Utils(context);
    }

    protected abstract int getLayoutResource();
    protected abstract int getMenuResource();
    protected abstract String ToolBarName();
    protected abstract int ToolBarIcon();
    protected abstract String Number();
    protected abstract String Place();

    private void configureToolbar(String title, int icon) {
        toolbar = (Toolbar) findViewById(R.id.appbar);

        if (toolbar != null) {
            toolbar.setTitle(title);

            setSupportActionBar(toolbar);
            getSupportActionBar().setIcon(icon);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            switch (ToolBarName()){
                case "Search":
                    getSupportActionBar().setIcon(null);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    break;

                default:

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getMenuResource(), menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (getMenuResource()){

            case R.menu.checkinout_main:
                if (id == R.id.action_in_out) {
                    if (PocketHr.checkConnection()) {
                        PocketHr.startSpecificActivity(BaseActivity.this,context,LeaveActivity.class);
                        return true;
                    }else{
                        PocketHr.setToast(context,"Network error..!" ,Toast.LENGTH_SHORT);
                    }
                }

                if (id == R.id.action_in_out_history) {
                    PocketHr.startSpecificActivityWithExtra(
                            BaseActivity.this,
                            context,
                            HistoryActivity.class,
                            "number",
                            "",
                            "",
                            "",
                            "2",
                            "",
                            "",
                            0);
                    return true;
                }
                break;

            case R.menu.search_menu:
                if (id == R.id.action_done){
                    addPlace(Place());
                }
        }

        if (id == android.R.id.home) {
            PocketHr.startSpecificActivity(BaseActivity.this,context,MainActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean exitState(){

        if (utils.getBoolean(context,Constants.EXIT_STATAUS)){

            String state = utils.getSharedPreference(context,Constants.EXIT_STATAUS);
            if (state.equals("")){
                return false;
            }else if (state.equals("false")){
                return false;
            }

        }
        return true;
    }

    public void addPlace(String place){
        PocketHr.startSpecificActivityWithExtra(
                BaseActivity.this,
                context,
                MainActivity.class,
                "place",
                "",
                "",
                "",
                place,
                "",
                "",
                0);

    }

    public static boolean EnableSyncAutomatically(boolean status){
        ContentResolver.setMasterSyncAutomatically(status);

        ContentResolver.getMasterSyncAutomatically();
        return true;
    }
}
