package edu.gcc.comp350;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Schedule {

    private int userID;
    private int scheduleID;
    private String name;
    private Map<Integer, String> courses; // courseID to color
    private Map<Integer, String> events; // eventID to color

    public Schedule(int userID, String name) {


    }

    public boolean addCourse(Course course) {
        if(hasConflict(course)){
            return false;
        }
        else{
            courses.put(course.getCourseID(), "none");
        }
        return true;
    }

    public void removeCourse(Course course) {
        courses.remove(course.getCourseID());
    }

    public void addEvent(Event event) { }

    public void removeEvent(Event event) { }

    public boolean hasConflict(Course course) {
        return false;
    }

    public int getTotalCredits() {
        return 0;
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

    public Map<Integer, String> getCourses() {
        return courses;
    }

    public Map<Integer, String> getEvents() {
        return events;
    }
}
