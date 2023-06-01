package com.dotsoft.smartsoniadashboard.objects;


import java.util.ArrayList;

public class AccidentDetailsObject {

    private ArrayList<AlertObject> alerts = new ArrayList<>();
    private ArrayList<MessageObject> messages = new ArrayList<>();
    private ArrayList<WorkerDataEntry> data = new ArrayList<>();
    private ArrayList<RecommendationObject> recommendations = new ArrayList<>();
    private ArrayList<SosAlert> sos = new ArrayList<>();

    public AccidentDetailsObject(ArrayList<AlertObject> alerts, ArrayList<MessageObject> messages, ArrayList<WorkerDataEntry> data, ArrayList<RecommendationObject> recommendations, ArrayList<SosAlert> sos) {
        this.alerts = alerts;
        this.messages = messages;
        this.data = data;
        this.recommendations = recommendations;
        this.sos = sos;
    }

    public ArrayList<AlertObject> getAlerts() {
        return alerts;
    }

    public ArrayList<MessageObject> getMessages() {
        return messages;
    }

    public ArrayList<WorkerDataEntry> getData() {
        return data;
    }

    public ArrayList<RecommendationObject> getRecommendations() {
        return recommendations;
    }

    public ArrayList<SosAlert> getSos() {
        return sos;
    }

    public void setAlerts(ArrayList<AlertObject> alerts) {
        this.alerts = alerts;
    }

    public void setSos(ArrayList<SosAlert> sos) {
        this.sos = sos;
    }

    public void setData(ArrayList<WorkerDataEntry> data) {
        this.data = data;
    }

    public void setMessages(ArrayList<MessageObject> messages) {
        this.messages = messages;
    }

    public void setRecommendations(ArrayList<RecommendationObject> recommendations) {
        this.recommendations = recommendations;
    }
}
