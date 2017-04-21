package com.rype3.leaveapp.rype3leaveapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;

public class ProgrammaticallyExitMessage {

    private Handler mHandler,mHandler1;
    private Intent intent;
    private MediaPlayer mediaPlayer = null;
    AlertDialog dialog;

    public boolean ProgrammaticallyExitMessage(final Activity activity,final Context context,int position,final String language) {
        mHandler = new Handler();
        switch (position){
            case 0:
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!(activity.isFinishing())) {
                            exitAlertMessageBox(activity ,language);
                        }
                    }
                }, 1000*45); // 1000ms = 45s
                break;

            case 1:
                if (mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                break;

        }
      return true;

    }

    private void exitAlertMessageBox(final Activity activity ,String language) {
        String tittle = "";
        String message = "";
        String ok = "";

        mediaPlayer = MediaPlayer.create(activity, R.raw.alarm);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

        switch (language) {
            case "s":
                tittle = activity.getString(R.string.s_exit);
                message = activity.getString(R.string.s_exit_message);
                ok = activity.getString(R.string.s_ok);
                break;

            case "e":
                tittle = activity.getString(R.string.e_exit);
                message = activity.getString(R.string.e_exit_message);
                ok = activity.getString(R.string.e_ok);
                break;

            case "t":
                tittle = activity.getString(R.string.t_exit);
                message = activity.getString(R.string.t_exit_message);
                ok = activity.getString(R.string.t_ok);
                break;
        }

                dialog = new AlertDialog.Builder(activity)
                .setTitle(tittle)
                .setMessage(message)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                if(mediaPlayer!= null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }

                dialog.dismiss();
                exitActivity(activity);

                    }
                }).create();

            dialog.show();
//                mHandler1 = new Handler();
//                mHandler1.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//
//                        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
//                            mediaPlayer.stop();
//                            mediaPlayer.reset();
//                            mediaPlayer.release();
//                        }
//                        intent = new Intent(activity,LanguageActivity.class);
//                        activity.startActivity(intent);
//                        activity.finish();
//
//                    }
//                }, 1000 * 20 * 1); // 1000ms = 1s
//
//            return;
//        }
    }

    public void exitActivity(final Activity activity){
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }


                intent = new Intent(activity,LanguageActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }, 1000*20); // 20000ms = 20s
    }
}
