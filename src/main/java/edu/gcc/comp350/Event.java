package edu.gcc.comp350;

import java.util.ArrayList;

public class Event {

    private String name;
    private boolean[] days;
    private double startTime;
    private double endTime;
    private String description;
    private int eventID;

    public Event(String name, boolean[] days, double startTime, double endTime, String description){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean[] getDays() { return days; }

    public void setDays(boolean[] days) { this.days = days; }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) { this.startTime = startTime; }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) { this.endTime = endTime; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEventID() {
        return eventID;
    }
}
