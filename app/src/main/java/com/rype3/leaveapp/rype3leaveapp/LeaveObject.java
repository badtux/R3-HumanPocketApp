package com.rype3.leaveapp.rype3leaveapp;


import io.realm.RealmObject;

public class LeaveObject extends RealmObject {
    private String tempId;
    private String name;
    private String epf_no;
    private String leave_type;
    private String leave_category;
    private String leave_period;
    private String leave_time_to;
    private String leave_time_from;
    private String leave_days_count;
    private String leave_reson;
    private boolean networkstate;

    public LeaveObject() {
    }

    public LeaveObject(
            String tempId,
            String name,
            String leave_type,
            String epf_no,
            String leave_category,
            String leave_period,
            String leave_time_to,
            String leave_time_from,
            String leave_days_count,
            String leave_reson,
            boolean networkstate) {
        this.tempId = tempId;
        this.name = name;
        this.leave_type = leave_type;
        this.epf_no = epf_no;
        this.leave_category = leave_category;
        this.leave_period = leave_period;
        this.leave_time_to = leave_time_to;
        this.leave_time_from = leave_time_from;
        this.leave_days_count = leave_days_count;
        this.leave_reson = leave_reson;
        this.networkstate = networkstate;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEpf_no() {
        return epf_no;
    }

    public void setEpf_no(String epf_no) {
        this.epf_no = epf_no;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getLeave_category() {
        return leave_category;
    }

    public void setLeave_category(String leave_category) {
        this.leave_category = leave_category;
    }

    public String getLeave_period() {
        return leave_period;
    }

    public void setLeave_period(String leave_period) {
        this.leave_period = leave_period;
    }

    public String getLeave_time_to() {
        return leave_time_to;
    }

    public void setLeave_time_to(String leave_time_to) {
        this.leave_time_to = leave_time_to;
    }

    public String getLeave_time_from() {
        return leave_time_from;
    }

    public void setLeave_time_from(String leave_time_from) {
        this.leave_time_from = leave_time_from;
    }

    public String getLeave_days_count() {
        return leave_days_count;
    }

    public void setLeave_days_count(String leave_days_count) {
        this.leave_days_count = leave_days_count;
    }

    public String getLeave_reson() {
        return leave_reson;
    }

    public void setLeave_reson(String leave_reson) {
        this.leave_reson = leave_reson;
    }

    public boolean isNetworkstate() {
        return networkstate;
    }

    public void setNetworkstate(boolean networkstate) {
        this.networkstate = networkstate;
    }
}
