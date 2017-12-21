package com.rype3.pocket_hrm.Sqldb;


public class LocationDetails {
    private int _id;
    private String location_name;
    private String lat;
    private String lon;
    private String check_Status;
    private String device_Id;
    private String meta;
    private String type;

    // Empty constructor
    public LocationDetails(){
    }

    // constructor
    public LocationDetails(int _id, String location_name, String lat,String lon,String check_Status,String device_Id,String meta,String type){
        this._id = _id;
        this.location_name = location_name;
        this.lat = lat;
        this.lon = lon;
        this.check_Status = check_Status;
        this.device_Id = device_Id;
        this.meta = meta;
        this.type = type;
    }

    // constructor
    public LocationDetails( String location_name, String lat,String lon,String check_Status,String device_Id,String meta,String type){
        this.location_name = location_name;
        this.lat = lat;
        this.lon = lon;
        this.check_Status = check_Status;
        this.device_Id = device_Id;
        this.meta = meta;
        this.type = type;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
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

    public String getCheck_Status() {
        return check_Status;
    }

    public void setCheck_Status(String check_Status) {
        this.check_Status = check_Status;
    }

    public String getDevice_Id() {
        return device_Id;
    }

    public void setDevice_Id(String device_Id) {
        this.device_Id = device_Id;
    }


    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
