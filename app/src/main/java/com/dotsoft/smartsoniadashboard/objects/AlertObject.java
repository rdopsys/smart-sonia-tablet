package com.dotsoft.smartsoniadashboard.objects;

public class AlertObject {

    private String title,id,workerName,date,riskGrading,time;

    public AlertObject(String id, String workerName, String title, String riskGrading, String date, String time){
        this.title = title;
        this.time = time;
        this.id = id;
        this.workerName = workerName;
        if(riskGrading==null){
            this.riskGrading = "";
        }else {
            this.riskGrading = riskGrading;
        }
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getId(){ return  id;}

    public String getTime() { return time; }

    public String getWorkerName() { return workerName; }

    public String getRiskGrading() { return riskGrading; }

    public String getDate() {
        return date;
    }
}