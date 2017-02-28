package com.rype3.leaveapp.rype3leaveapp;


import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    private SharedPreferences sharedPreferences;
    private Context context;
    public Utils(Context context) {
        this.context = context;
    }

    public String setSharedPreference(Context context, String value, String keyname){
        sharedPreferences = context.getApplicationContext().getSharedPreferences("leave", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyname, value);
        editor.commit();
        return value;
    }

    public String getSharedPreference(Context context, String keyname){
        String value = "";
        sharedPreferences = context.getApplicationContext().getSharedPreferences("leave", context.MODE_PRIVATE);
        value = sharedPreferences.getString(keyname, null);
        return value.toString();
    }

    public boolean getBoolean(Context context, String keyname){
        String value = "";
        sharedPreferences = context.getApplicationContext().getSharedPreferences("leave", context.MODE_PRIVATE);
        value = sharedPreferences.getString(keyname, null);
        if (value != null) {
            return true;
        }
            return false;
    }
}
