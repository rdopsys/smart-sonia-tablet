package com.dotsoft.smartsoniadashboard.objects;

import android.util.Log;

import com.dotsoft.smartsoniadashboard.DataHolder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class WorkerDataEntry {
    private String time, date, heartRate, location, timestampRawFormat;
    private Double height;
    private int floor;
    private boolean areaRed;

    public WorkerDataEntry(String timestamp, String heartRate, String height, String location, String beaconId, String beaconDistance) {
        this.timestampRawFormat = timestamp;
        this.heartRate = heartRate;
        this.location = location;
        if(timestamp.equals("N/A")){
            this.date = "N/A";
            this.time = "N/A";
        }else {
            String[] spitTimestamp = timestamp.split(" ",2);
            String[] spitDate = spitTimestamp[0].split("-");
            this.date = spitDate[2] + "-" + spitDate[1] + "-" +spitDate[0];
            this.time = spitTimestamp[1];
        }


        try{
            this.height = Double.parseDouble(height);
        } catch (Exception e) {
            this.height = 1.0;
            e.printStackTrace();
        }
        this.floor = (int) (this.height / DataHolder.getInstance().getSingleFloorHeight());

        this.areaRed = false;
        if(beaconId!=null){
            if(!beaconId.equals("none")){
                if(!beaconDistance.equals("none")){
                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    try {
                        BigDecimal dValue = new BigDecimal(beaconDistance);
                        double d = dValue.doubleValue();
                        if (d <= 2.0) {
                            this.areaRed = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public String getLocation() {
        return location;
    }

    public Double getHeight() {
        return height;
    }

    public Integer getFloor() {
        return floor;
    }

    public boolean isAreaRed() {
        return areaRed;
    }

    public String getTimestampRawFormat() { return timestampRawFormat; }
}
