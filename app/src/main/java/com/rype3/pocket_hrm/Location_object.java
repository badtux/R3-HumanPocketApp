package com.rype3.pocket_hrm;

import io.realm.RealmObject;

public class Location_object extends RealmObject {

    private String location;
    private String lat;
    private String lon;
    private String checkStatus;
    private String deviceId;
    private int timeStamp;
    private boolean internetState;
    private boolean syncState;

    public Location_object() {

    }

    public Location_object(String location, String lat, String checkStatus, String deviceId, int timeStamp, String lon, boolean internetState, boolean syncState) {
        this.location = location;
        this.lat = lat;
        this.checkStatus = checkStatus;
        this.deviceId = deviceId;
        this.timeStamp = timeStamp;
        this.lon = lon;
        this.internetState = internetState;
        this.syncState = syncState;
    }

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
