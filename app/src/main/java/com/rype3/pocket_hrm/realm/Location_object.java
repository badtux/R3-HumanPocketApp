package com.rype3.pocket_hrm.realm;

import com.rype3.pocket_hrm.user.Location;

import io.realm.RealmObject;

public class Location_object extends RealmObject {

    private String location;
    private String lat;
    private String lon;
    private String checkStatus;
    private String deviceId;
   // private String test;
    private int timeStamp;
    private boolean internetState;
    private boolean syncState;

    public Location_object() {

    }

    public Location_object(
            String location,
            String lat,
            String lon,
            String checkStatus,
            String deviceId,
            String test,
            int timeStamp,
            boolean internetState,
            boolean syncState) {
        this.location = location;
        this.lat = lat;
        this.lon = lon;
        this.checkStatus = checkStatus;
        this.deviceId = deviceId;
    //    this.test = test;
        this.timeStamp = timeStamp;
        this.internetState = internetState;
        this.syncState = syncState;
    }

//    public void fill(final Location location) {
//
//     //   setTest(user.getTest());
//        setLocation(location.getLocation());
//        setLat(location.getLat());
//        setLon(location.getLon());
//        setCheckStatus(location.getCheckStatus());
//        setInternetState(location.isInternetState());
//        setSyncState(location.isSyncState());
//        setTimeStamp(location.getTimeStamp());
//        setDeviceId(location.getDeviceId());
//    }

//    public String getTest() {
//        return test;
//    }
//
//    public void setTest(String test) {
//        this.test = test;
//    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public boolean isInternetState() {
        return internetState;
    }

    public void setInternetState(boolean internetState) {
        this.internetState = internetState;
    }

    public boolean isSyncState() {
        return syncState;
    }

    public void setSyncState(boolean syncState) {
        this.syncState = syncState;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
