package com.rype3.pocket_hrm;


import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    private SharedPreferences sharedPreferences;
    private Context context;
    public Utils(Context context) {
        this.context = context;
    }

    public String setSharedPreference(Context context, String value, String keyname){
        sharedPreferences = context.getApplicationContext().getSharedPreferences("mendis_check", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyname, value);
        editor.apply();
        return value;
    }

    public String getSharedPreference(Context context, String keyname){
        String value = "";
        sharedPreferences = context.getApplicationContext().getSharedPreferences("mendis_check", Context.MODE_PRIVATE);
        value = sharedPreferences.getString(keyname, null);
        return value;
    }

    public boolean getBoolean(Context context, String keyname){
        String value = "";
        sharedPreferences = context.getApplicationContext().getSharedPreferences("mendis_check", Context.MODE_PRIVATE);
        value = sharedPreferences.getString(keyname, null);
        if (value != null) {
            return true;
        }
            return false;
    }
}
