package com.rype3.leaveapp.rype3leaveapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ErrorMessageActivity extends AppCompatActivity {

    TextView title_1,title_2,warning;
    Button button;
    Utils utils;
    private MediaPlayer mediaPlayer_1 = null;
    private Intent intent = null;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_message);

        context = this.getApplication();
        utils =  new Utils(context);

        title_1 = (TextView) findViewById(R.id.title_1);
        title_2 = (TextView) findViewById(R.id.title_3);
        warning = (TextView) findViewById(R.id.tv_warnning);
        button = (Button) findViewById(R.id.btn_next);

        mediaPlayer_1 = MediaPlayer.create(this, R.raw.click);

        switch (utils.getSharedPreference(context,Constants.LANGUAGE_TYPE)){
            case "s":
                title_1.setText(getString(R.string.s_wrong_1));
                title_2.setText(getString(R.string.s_press));
                warning.setText(getString(R.string.s_warning));
                button.setText(getString(R.string.s_button));
                break;

            case "t":
                title_1.setText(getString(R.string.t_wrong_1));
                title_2.setText(getString(R.string.t_press));
                warning.setText(getString(R.string.t_warning));
                button.setText(getString(R.string.t_button));
                break;

            case "e":
                title_1.setText(getString(R.string.e_wrong_1));
                title_2.setText(getString(R.string.e_press));
                warning.setText(getString(R.string.e_warning));
                button.setText(getString(R.string.e_button));
                break;
        }
        button.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (playSound(1)) {
                intent = new Intent(ErrorMessageActivity.this, EPFnumberActivity.class);
                startActivity(intent);
                finish();
            }
        }
        });
    }

    public boolean playSound(int position){
        try {
            switch (position){
                case 1:
                    if (mediaPlayer_1.isPlaying()) {
                        mediaPlayer_1.stop();
                        mediaPlayer_1.release();
                        mediaPlayer_1 = MediaPlayer.create(ErrorMessageActivity.this, R.raw.click_2);
                    } mediaPlayer_1.start();
                    break;
            }
        } catch(Exception e) { e.printStackTrace();
        }
        return true;
    }
}
