package com.dotsoft.smartsoniadashboard.objects;

import java.util.ArrayList;

public class Beacon {

    private String majorID,postID,infoTextFormat;
    private ArrayList<BeaconInfo> beaconInfo = new ArrayList<>();
    private String selectedDay;


    public Beacon(String majorID, String postID, String infoTextFormat) {
        this.majorID = majorID;
        this.postID = postID;
        this.infoTextFormat = infoTextFormat;
        selectedDay = "";
    }

    public void addInfo(String location, String note,String active,String timestamp){
        beaconInfo.add(new BeaconInfo(active,location,note,timestamp));
    }

    public String getMajorID() {
        return majorID;
    }

    public String getPostID() {
        return postID;
    }

    public String getInfoTextFormat() { return infoTextFormat; }

    public ArrayList<BeaconInfo> getBeaconInfo() {return beaconInfo;}

    public BeaconInfo getCurrentBeaconInfo() {return beaconInfo.get(0);}

    public void setSelectedDay(String selectedDay) { this.selectedDay = selectedDay; }

    public String getSelectedDay() { return selectedDay; }

}
