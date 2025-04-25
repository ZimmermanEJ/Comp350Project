package edu.gcc.comp350;

import org.bson.types.ObjectId;
import java.util.ArrayList;

public class Course {

    public enum Days {
        MWF, TR
    }

    private ObjectId _id; // MongoDB ObjectId
    private String department; // COMP
    private int courseNumber; // 350
    private char sectionCode; // A
    private String title;
    private int credits;
    private String description;
    private String professor;
    private int referenceNumber;
    private Days days;
    private ArrayList<Boolean> daysArray;
    private double startTime;
    private double endTime;
    private ArrayList<ArrayList<Double>> timeSlot;
    private int courseID;

    public Course(String department, int courseNumber, char sectionCode,
                  String title, int credits, String description, String professor,
                  int referenceNumber, Days days, ArrayList<Boolean> daysArray, double startTime, double endTime,
                  ArrayList<ArrayList<Double>> timeSlot) {
        this.department = department;
        this.courseNumber = courseNumber;
        this.sectionCode = sectionCode;
        this.title = title;
        this.credits = credits;
        this.description = description;
        this.professor = professor;
        this.referenceNumber = referenceNumber;
        this.days = days;
        this.daysArray = daysArray;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeSlot = timeSlot;
    }

    public boolean hasConflict(Course course) {
        for (int i = 0; i < timeSlot.size(); i++) {
            ArrayList<Double> thisDay = this.timeSlot.get(i);
            ArrayList<Double> otherDay = course.timeSlot.get(i);
            if (thisDay.size() == 2 && otherDay.size() == 2) {
                if (thisDay.get(0) < otherDay.get(1) && thisDay.get(1) > otherDay.get(0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getDepartment() {
        return department;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public char getSectionCode() {
        return sectionCode;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public String getDescription() {
        return description;
    }

    public String getProfessor() {
        return professor;
    }

    public int getReferenceNumber() {
        return referenceNumber;
    }

    public Days getDays() {
        return days;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double[][] getTimeSlot() {
        double[][] timeSlotArray = new double[timeSlot.size()][];
        for (int i = 0; i < timeSlot.size(); i++) {
            ArrayList<Double> innerList = timeSlot.get(i);
            timeSlotArray[i] = innerList.stream().mapToDouble(Double::doubleValue).toArray();
        }
        return timeSlotArray;
    }

    public ArrayList<ArrayList<Double>> getTimeSlotList() {
        return timeSlot;
    }

    public int getCourseID() {
        return courseID;
    }

    public ArrayList<Boolean> getDaysArray() {
        return daysArray;
    }

    public String toString() {
        return department + " " + courseNumber + sectionCode + " - " + title + " " +
                String.format("%.2f", ((startTime - 1) % 12) + 1) + " - " +
                String.format("%.2f", ((endTime - 1) % 12) + 1) + " " + days + " #" + referenceNumber;
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }
}