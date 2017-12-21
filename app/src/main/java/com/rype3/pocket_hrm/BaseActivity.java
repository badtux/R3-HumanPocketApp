package com.rype3.pocket_hrm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity{

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
                    Intent intent = new Intent(getBaseContext(), LeaveActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                if (id == R.id.action_in_out_history) {
                    Intent intent = new Intent(getBaseContext(), HistoryActivity.class);
                    intent.putExtra("number","2");
                    startActivity(intent);
                    finish();
                    return true;
                }
                break;

            case R.menu.search_menu:
                if (id == R.id.action_done){
                    addPlace(Place());
                }
        }

        if (id == android.R.id.home) {
            intent = new Intent(BaseActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
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
        intent = new Intent(BaseActivity.this,MainActivity.class);
        intent.putExtra("place" , place);
        startActivity(intent);
        finish();

    }
}
