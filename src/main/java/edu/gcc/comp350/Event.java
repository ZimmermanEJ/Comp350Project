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
        this.name = name;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.eventID = 0;
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

    public String toString() {
        // string builder to return event in the form name ID: eventID startTime - endTime SMTWRFS
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ID: ").append(eventID).append(" ");
        sb.append((((startTime-1) % 12) + 1)).append(startTime <= 11 ? "AM" :"PM").append(" - ");
        sb.append(((endTime-1) % 12) + 1).append(endTime <= 11 ? "AM" :"PM").append(" ");
        for (int i = 0; i < days.length; i++) {
            if (days[i]) {
                sb.append("SMTWRFS".charAt(i));
            }
        }
        return sb.toString();
    }
}
