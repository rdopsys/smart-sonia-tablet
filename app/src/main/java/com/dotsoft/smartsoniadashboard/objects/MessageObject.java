package com.dotsoft.smartsoniadashboard.objects;

public class MessageObject {

    private String title,id,workerName,date,read,time,notes;

    public MessageObject(String id, String workerName, String title, String read, String date, String time,String notes){
        this.title = title;
        this.time = time;
        this.id = id;
        this.workerName = workerName;
        this.date = date;
        this.notes = notes;
        this.read = read;
    }

    public String getTitle() {
        return title;
    }

    public String getId(){ return  id;}

    public String getTime() { return time; }

    public String getWorkerName() { return workerName; }

    public String getRead() { return  read;}

    public String getNotes() { return notes; }

    public String getDate() {
        return date;
    }
}