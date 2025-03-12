package edu.gcc.comp350;
public class Course {

    public enum Days {
        MWF, TR
    }

    private String department; // COMP
    private int courseNumber; // 350
    private char sectionCode; // A
    private String title;
    private int credits;
    private String description;
    private String professor;
    private int referenceNumber;
    private Days days;
    private double startTime;
    private double endTime;
    private double[][] timeSlot;
    private int courseID;

    public Course(String department, int courseNumber, char sectionCode,
                  String title, int credits, String description, String professor,
                  int referenceNumber, Days days, double startTime, double endTime,
                  double[][] timeSlot) {
        this.department = department;
        this.courseNumber = courseNumber;
        this.sectionCode = sectionCode;
        this.title = title;
        this.credits = credits;
        this.description = description;
        this.professor = professor;
        this.referenceNumber = referenceNumber;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeSlot = timeSlot;
        this.courseID = 1;
    }

    public boolean hasConflict(Course course) { return false; }

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

    public Days getDays() { return days; }

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public double[][] getTimeSlot() {
        return timeSlot;
    }

    public int getCourseID() {
        return courseID;
    }
}
