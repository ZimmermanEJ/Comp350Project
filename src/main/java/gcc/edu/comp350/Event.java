package edu.gcc.comp350;

public class Event {

    private String name;
    private int[][] timeSlots; // always 7 x 2
    private String description;
    private int eventID;

    public Event(String name, int[][] timeSlots, String description){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[][] getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(int[][] timeSlots) {
        this.timeSlots = timeSlots;
    }

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
