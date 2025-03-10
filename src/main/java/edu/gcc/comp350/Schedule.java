package edu.gcc.comp350;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Schedule {

    private int userID;
    private int scheduleID;
    private String name;
    private Map<Course, String> courses; // course to color
    private Map<Event, String> events; // event to color

    public Schedule(int userID, String name, int scheduleID) {
        this.userID = userID;
        this.scheduleID = scheduleID;
        this.name = name;
        this.courses = new HashMap<Course, String>();
        this.events = new HashMap<Event, String>();
    }

    public boolean addCourse(Course course) {
        courses.put(course, "Red");
        return true;
    }

    public void removeCourse(Course course) { }

    public void addEvent(Event event) { }

    public void removeEvent(Event event) { }

    public boolean hasConflict(Course course) {
        return false;
    }

    public int getTotalCredits() {
        int total = 0;
        for (Course course : this.getCourses().keySet()){
            total += course.getCredits();
        }
        return total;
    }

    public int getUserID() {
        return userID;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Course, String> getCourses() {
        return courses;
    }

    public Map<Event, String> getEvents() {
        return events;
    }

    public String scheduleView() {
        StringBuilder toReturn = new StringBuilder(this.getName() + " - " + this.getTotalCredits() + " credits\n");

        for (Course course : this.getCourses().keySet()) {
            toReturn.append(course.getTitle()).append("\t");
            toReturn.append(course.getDays()).append("\t");
            toReturn.append(String.format("%.2f", course.getStartTime())).append(" - ").append(String.format("%.2f", course.getEndTime()));
            toReturn.append("\n");
        }

        return toReturn.toString();
    }

    public String listView() {
        StringBuilder toReturn = new StringBuilder("ID: ");

        toReturn.append(this.getScheduleID()).append("\t");
        toReturn.append("Name: ").append(this.getName());
        toReturn.append(" - ").append(this.getTotalCredits()).append(" credits");

        return toReturn.toString();
    }
}
