package com.rype3.pocket_hrm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity{

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        configureToolbar(ToolBarName() ,ToolBarIcon());
    }

    protected abstract int getLayoutResource();
    protected abstract int getMenuResource();
    protected abstract String ToolBarName();
    protected abstract int ToolBarIcon();

    private void configureToolbar(String title, int icon) {
        toolbar = (Toolbar) findViewById(R.id.appbar);

        if (toolbar != null) {
            toolbar.setTitle(title);

            setSupportActionBar(toolbar);
            getSupportActionBar().setIcon(icon);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                    startActivity(intent);
                    finish();
                    return true;
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
