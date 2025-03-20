package edu.gcc.comp350;

import java.util.ArrayList;

public class Filter {
    private Search search;
    private ArrayList<Course> filteredResults;
    private int credits;
    private String department; // COMP
    private int courseNumber; // 350
    private char sectionCode; // A
    private enum days{MWF, TR};
    private double startTime;
    private double endTime;

    public Filter(int credits, String department, int courseNumber,
                  char sectionCode, double startTime, double endTime,
                  Search search) {
            this.credits = credits;
            this.department = department;
            this.courseNumber = courseNumber;
            this.sectionCode = sectionCode;
            this.startTime = startTime;
            this.endTime = endTime;


    }

    public Search getSearch() {
        return search;
    }

    public ArrayList<Course> getFilteredResults() {

        return filteredResults;
    }

    public int getCredits() {
        return credits;
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

    public double getStartTime() {
        return startTime;
    }

    public double getEndTime() {
        return endTime;
    }
}
