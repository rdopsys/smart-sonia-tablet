package com.dotsoft.smartsoniadashboard.objects;

public class RecommendationObject {

    private String title,id,workerName,date,time,read;

    public RecommendationObject(String id, String workerName, String title, String read, String date, String time){
        this.title = title;
        this.time = time;
        this.id = id;
        this.workerName = workerName;
        this.read = read;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getId(){ return  id;}

    public String getTime() { return time; }

    public String getWorkerName() { return workerName; }

    public String getRead() { return read; }

    public String getDate() {
        return date;
    }
}