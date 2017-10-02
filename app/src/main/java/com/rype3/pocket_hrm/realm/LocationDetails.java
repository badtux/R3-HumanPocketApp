package com.rype3.pocket_hrm.realm;

import com.rype3.pocket_hrm.user.Location;

import io.realm.RealmObject;

public class LocationDetails extends RealmObject{

    private int id;
    private String meta;
    private String checkState;
    private String type;
    private boolean state;

    public LocationDetails() {
    }

    public LocationDetails(int id, String meta, String checkState,String type,boolean state) {
        this.id = id;
        this.meta = meta;
        this.checkState = checkState;
        this.type = type;
        this.state = state;
    }

    public void fill(final Location location) {
        setId(location.getId());
        setMeta(location.getMeta());
        setCheckState(location.getCheckState());
        setType(location.getType());
        setState(location.isState());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
