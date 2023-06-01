package com.dotsoft.smartsoniadashboard;

import com.dotsoft.smartsoniadashboard.objects.AccidentObject;
import com.dotsoft.smartsoniadashboard.objects.AlertObject;
import com.dotsoft.smartsoniadashboard.objects.Beacon;
import com.dotsoft.smartsoniadashboard.objects.MessageObject;
import com.dotsoft.smartsoniadashboard.objects.RecommendationObject;
import com.dotsoft.smartsoniadashboard.objects.Smartwatch;
import com.dotsoft.smartsoniadashboard.objects.SosAlert;
import com.dotsoft.smartsoniadashboard.objects.User;
import com.dotsoft.smartsoniadashboard.objects.Worker;

import java.util.ArrayList;


public class DataHolder {

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() { return holder; }

    //settings
    private double singleFloorHeight = 3.0;

    //user
    private User selectedUser;

    //beacons
    private ArrayList<Beacon> beaconsList;

    //workers
    private ArrayList<Worker> workersList;

    //watches
    private ArrayList<Smartwatch> smartwatchesList;

    //sos Alerts
    private ArrayList<SosAlert> sosAlertsList;

    //sos results
    private int sosResults = 0;

    //alerts
    private ArrayList<AlertObject> alertsList;

    //Recommendations
    private ArrayList<RecommendationObject> recommendationsList;

    //messages
    private ArrayList<MessageObject> messagesList;

    //accidents
    private ArrayList<AccidentObject> accidentsList;


    // Getter and setter for selected User
    public void setSelectedUser(User selectedUser) { this.selectedUser = selectedUser; }
    public User getSelectedUser() { return this.selectedUser; }

    // Getter and setter for beacons
    public void setBeaconsList(ArrayList<Beacon> beaconsList){ this.beaconsList = beaconsList; }
    public ArrayList<Beacon> getBeaconsList() { return beaconsList; }

    // Getter and setter for workers
    public void setWorkersList(ArrayList<Worker> workersList) { this.workersList = workersList; }
    public ArrayList<Worker> getWorkersList() { return workersList; }

    // Getter and setter for smartwatches
    public void setSmartwatchesList(ArrayList<Smartwatch> smartwatchesList) { this.smartwatchesList = smartwatchesList; }
    public ArrayList<Smartwatch> getSmartwatchesList() { return smartwatchesList; }

    // Getter and setter for sos alerts
    public void setSosAlertsList(ArrayList<SosAlert> sosAlertsList) { this.sosAlertsList = sosAlertsList; }
    public ArrayList<SosAlert> getSosAlertsList() { return  sosAlertsList; }

    // Getter and setter for sos results
    public void setSosResults(int sosResults) { this.sosResults = sosResults; }
    public int getSosResults() { return sosResults; }

    // Getter and setter for setting
    public void setSingleFloorHeight(double singleFloorHeight) { this.singleFloorHeight = singleFloorHeight; }
    public double getSingleFloorHeight() { return singleFloorHeight; }

    // Getter and setter for alerts
    public void setAlertsList(ArrayList<AlertObject> alertsList) {
        this.alertsList = alertsList;
    }
    public ArrayList<AlertObject> getAlertsList() { return alertsList; }

    // Getter and setter for sos alerts
    public void setMessagesList(ArrayList<MessageObject> messagesList) { this.messagesList = messagesList; }
    public ArrayList<MessageObject> getMessagesList() { return messagesList; }

    // Getter and setter for recommendations
    public ArrayList<RecommendationObject> getRecommendationsList() { return recommendationsList; }
    public void setRecommendationsList(ArrayList<RecommendationObject> recommendationsList) { this.recommendationsList = recommendationsList; }

    // Getter and setter for accidents
    public ArrayList<AccidentObject> getAccidentsList() { return accidentsList; }
    public void setAccidentsList(ArrayList<AccidentObject> accidentsList) { this.accidentsList = accidentsList; }
}

