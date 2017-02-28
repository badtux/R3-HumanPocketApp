package com.rype3.leaveapp.rype3leaveapp;


import android.content.Context;

public class Constants {

    public static final String LANGUAGE_TYPE= "language_type";
    public static final String OAUTH_TOKEN= "oauth_token";
    public static final String OAUTH_CODE= "oauth_code";
    public static final String PIN= "pin_number";
    public static final String EPF_NUMBER= "epf_number";
    public static final String TOKEN= "token";
    public static final String USERNAME= "user_name";
    public static final String USERPROFILEPIC= "user_profile_pic";
    public static final String ANUAL_LEAVE_COUNT= "annual_count";
    public static final String CASUAL_LEAVE_COUNT= "casual_count";
    public static final String MEDICAL_LEAVE_COUNT= "medical_count";
    public static final String NOPAY_LEAVE_COUNT= "nopay_count";
    private Context context ;
    public Constants(Context context) {
        this.context = context;
    }

    public String urls(int position){
        String Url = "";
        switch (position) {

            case 0:
                Url = "http://wmmmendis.rype3.net/human/api/v1/login";
                break;

            case 1:
                Url = "http://wmmmendis.rype3.net/human/api/v1/leave/store";
                break;

            case 2:
                Url = "";
                break;

            case 3:
                Url = "";
                break;
        }

        return Url;
    }

}
