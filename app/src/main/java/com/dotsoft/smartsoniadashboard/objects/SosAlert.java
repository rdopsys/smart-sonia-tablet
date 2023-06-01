package com.dotsoft.smartsoniadashboard.objects;

public class SosAlert {

    private String author,date,time,location, id;

    public SosAlert(String id, String title, String timestamp, String location) {

        String[] spitTitle = title.split(" ",2);
        String[] spitTimestamp = timestamp.split(" ",2);
        String[] spitDate = spitTimestamp[0].split("-");

        this.id = id;
        this.author = spitTitle[1];
        this.date = spitDate[2] + "-" + spitDate[1] + "-" +spitDate[0];
        this.time = spitTimestamp[1];
        this.location = location;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getId() {
        return id;
    }
}
