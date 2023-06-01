package com.dotsoft.smartsoniadashboard.objects;

import java.util.ArrayList;

public class CalendarDay {

    private String dayNumber, monthNumber, yearNumber;
    private boolean activeDay;
    private int sos, alerts, accidents;
    private ArrayList<SosAlert> sosList;
    private ArrayList<AlertObject> alertsList;
    private ArrayList<AccidentObject> accidentsList;


    public CalendarDay(String dayNumber, String monthNumber, String yearNumber, boolean activeDay, int sos, int alerts, int accidents) {
        this.dayNumber = dayNumber;
        this.monthNumber = monthNumber;
        this.yearNumber = yearNumber;
        this.activeDay = activeDay;
        this.sos = sos;
        this.alerts = alerts;
        this.accidents = accidents;
        this.sosList = new ArrayList<>();
        this.alertsList = new ArrayList<>();
        this.accidentsList = new ArrayList<>();
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public String getMonthNumber() {
        return monthNumber;
    }

    public String getYearNumber() {
        return yearNumber;
    }

    public boolean isActiveDay() {
        return activeDay;
    }

    public int getSos() {
        return sos;
    }

    public int getAlerts() {
        return alerts;
    }

    public int getAccidents() { return accidents; }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public void setAlerts(int alerts) {
        this.alerts = alerts;
    }

    public void setAccidents(int accidents) { this.accidents = accidents; }

    public void addAlert(AlertObject alert){ alertsList.add(alert); }

    public void addSos(SosAlert sos){ sosList.add(sos); }

    public void addAccidents(AccidentObject accident){ accidentsList.add(accident); }

    public ArrayList<AlertObject> getAlertsList() { return alertsList; }

    public ArrayList<SosAlert> getSosList() { return sosList; }

    public ArrayList<AccidentObject> getAccidentsList() { return accidentsList; }

    public void setAlertsList(ArrayList<AlertObject> alertsList) {
        this.alertsList = alertsList;
    }

    public void setAccidentsList(ArrayList<AccidentObject> accidentsList) {
        this.accidentsList = accidentsList;
    }

    public void setSosList(ArrayList<SosAlert> sosList) {
        this.sosList = sosList;
    }
}
