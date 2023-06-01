package com.dotsoft.smartsoniadashboard.objects;

import com.dotsoft.smartsoniadashboard.objects.cms.WeatherCMS;

import java.util.ArrayList;

public class Worker {
    private String infoTextFormat;
    private User workerProfileInfo;
    private ArrayList<WorkerInfo> info;
    private ArrayList<WorkerDataEntry> data;
    private WeatherCMS.Root weatherData;
    private String selectedDay;

    public Worker(User workerProfileInfo, String infoTextFormat) {
        this.workerProfileInfo = workerProfileInfo;
        this.infoTextFormat = infoTextFormat;
        data = new ArrayList<>();
        info = new ArrayList<>();
        selectedDay = "";
    }


    public void addWorkerInfo(String active, String smartwatchID, String timestamp){
        info.add(new WorkerInfo(active,smartwatchID,timestamp));
    }

    public void addWorkerData(String timestamp, String heartRate, String height, String location, String beaconId, String beaconDistance) {
        data.add(0,new WorkerDataEntry( timestamp, heartRate, height, location, beaconId, beaconDistance));
    }

    public void setWeatherData(WeatherCMS.Root weatherData) { this.weatherData = weatherData; }

    public WeatherCMS.Root getWeatherData() { return weatherData; }

    public User getWorkerProfileInfo() {
        return workerProfileInfo;
    }

    public String getInfoTextFormat(){return infoTextFormat;}

    public ArrayList<WorkerInfo> getWorkerInfo() { return info;}

    public ArrayList<WorkerDataEntry> getData() { return data; }

    public WorkerInfo getCurrentWorkerInfo() {return info.get(0);}

    public void setSelectedDay(String selectedDay) { this.selectedDay = selectedDay; }

    public String getSelectedDay() { return selectedDay; }
}
