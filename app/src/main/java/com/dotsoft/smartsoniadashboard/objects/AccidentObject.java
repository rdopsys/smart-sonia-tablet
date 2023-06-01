package com.dotsoft.smartsoniadashboard.objects;

public class AccidentObject {

    private String title,id,workerName,date,dangerousZone,time,notes;
    private AccidentDetailsObject details;

    public AccidentObject(String id, String workerName, String title, String dangerousZone, String date, String time, String notes){
        this.title = title;
        this.time = time;
        this.id = id;
        this.workerName = workerName;
        this.date = date;
        this.notes = notes;
        this.dangerousZone = dangerousZone;
    }

    public String getTitle() {
        return title;
    }

    public String getId(){ return  id;}

    public String getTime() { return time; }

    public String getWorkerName() { return workerName; }

    public String getDangerousZone() { return  dangerousZone;}

    public String getNotes() { return notes; }

    public String getDate() {
        return date;
    }

    public AccidentDetailsObject getDetails() { return details; }

    public void setDetails(AccidentDetailsObject details) { this.details = details; }
}