package edu.gcc.comp350;
public class Course {
    private String department; // COMP
    private int courseNumber; // 350
    private char sectionCode; // A
    private String title;
    private int credits;
    private String description;
    private String professor;
    private int referenceNumber;
    private enum days {MWF, TR};
    private double startTime;
    private double endTime;
    private double[][] timeSlot;
    private int courseID;

    public Course(String department, int courseNumber, char sectionCode,
                  String title, int credits, String description, String professor,
                  int referenceNumber, double startTime, double endTime,
                  double[][] timeSlot) {

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
