package com.rype3.pocket_hrm;


import android.content.Context;

class Constants {

    static final String OAUTH_CODE= "oauth_code";
    static final String PIN= "pin_number";
    static final String EPF_NUMBER= "epf_number";
    static final String TOKEN= "token";
    static final String USERNAME= "user_name";
    static final String LAST_TIME = "last_time";
    static final String EXIT_STATAUS = "exit_state";
    static final String DEVICE_ID = "device_id";
    static final String CHECKED_STATE = "checked_state";
    static final String LOCATION = "show_room";
    static final String START_TIMESTAMP = "start_timestamp";
    static final String USER_ID = "user_id";
    static final String USER_EPF_NO = "epf_no";
    static final String DEVICE_NAME = "device_name";
    static final String BATTERY_LEVEL = "battery_level";
    static final String LAT = "lat";
    static final String LONG = "long";
    static final String GEO_LATLONG = "geo_latlong";
    static final String TEMP_ID = "temp_id";
    protected Context context ;
    static final String BASE_URL = "http://wmmmendis.rype3.net";
    static final String last_sync_time = "last_sync";
    static final String SYNC_STATE = "sync_state";

    Constants(Context context) {
        this.context = context;
    }

//    String urls(int position){
//        String Url = "";
//        switch (position) {
//
////            case 0:
////                Url = "http://wmmmendis.rype3.net/human/api/v1/login";
////                break;
////
////            case 1:
////                Url = "http://wmmmendis.rype3.net/human/api/v1/leave/store";
////                break;
////
////            case 2:
////                Url = "http://wmmmendis.rype3.net/io/api/v1/device/register";
////                break;
////
////            case 3:
////                Url = "http://wmmmendis.rype3.net/human/api/v1/check-in";
////                break;
////
////            case 4:
////                Url = "http://wmmmendis.rype3.net/human/api/v1/check-out";
////                break;
////
////            case 5:
////                Url = "http://wmmmendis.rype3.net/human/api/v1/me";
////                break;
//
//        }
//        return Url;
//    }
}
