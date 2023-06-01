package com.dotsoft.smartsoniadashboard.objects;

public class WorkerInfo {

    private String timestamp, smartwatchID;
    private Boolean active;

    public WorkerInfo(String active, String smartwatchID, String timestamp) {
        this.smartwatchID = smartwatchID;
        this.timestamp = timestamp;
        if(active.equals("1")){
            this.active = true;
        }else {
            this.active = false;
        }
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSmartwatchID(){ return smartwatchID;}

    public Boolean isActive(){
        return active;
    }
}
