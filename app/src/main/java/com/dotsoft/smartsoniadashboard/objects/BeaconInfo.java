package com.dotsoft.smartsoniadashboard.objects;

public class BeaconInfo {

    private String timestamp, location, note;
    private Boolean active;

    public BeaconInfo(String active,String location, String note, String timestamp) {
        this.location = location;
        this.note = note;
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

    public String getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public Boolean isActive(){
        return active;
    }
}
