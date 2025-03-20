package edu.gcc.comp350;

import java.util.ArrayList;
import java.util.Objects;

public class Filter {
    private Search search;
    private ArrayList<Course> filteredResults;
    private int credits;
    private String department; // COMP
    private int courseNumber; // 350
    private char sectionCode; // A
    private enum Days{MWF, TR}
    private double startTime;
    private double endTime;

    public Filter(int credits, String department, int courseNumber,
                  char sectionCode, String classdays, double startTime, double endTime,
                  Search search) {
            Days d = null;
            this.credits = credits;
            this.department = department;
            this.courseNumber = courseNumber;
            this.sectionCode = sectionCode;
            if(classdays.equals("MWF")){
                d = Days.MWF;
            }
            else if(classdays.equals("TR")){
                d = Days.TR;
            }
            this.startTime = startTime;
            this.endTime = endTime;
            this.search = search;

        for (int i = 0; i < search.getSearchResults().size(); i++) {
           if(helper(search.getSearchResults().get(i), credits, department, courseNumber, sectionCode, d, startTime, endTime)){
               filteredResults.add(search.getSearchResults().get(i));
           }
        }
        for (int i = 0; i < filteredResults.size(); i++) {
            if(helper(filteredResults.get(i), credits, department, courseNumber, sectionCode, d, startTime, endTime)){
                filteredResults.remove(i);
            }
        }

    }
    public boolean helper(Course course, int credits, String department, int courseNumber, char sectionCode, Days d, double startTime, double endTime) {
        if (course.getCredits() != credits && credits != 0) {
            return false;
        } else if (!Objects.equals(course.getDays().toString(), d.toString()) && d != null) {
            return false;
        } else if (!course.getDepartment().equals(department) && !department.isEmpty()) {
            return false;
        } else if (course.getCourseNumber() != courseNumber && courseNumber != 0) {
            return false;
        } else if (course.getSectionCode() != sectionCode && sectionCode != ' ') {
            return false;
        } else if (course.getStartTime() != startTime && startTime != 0) {
            return false;
        } else if (course.getEndTime() != endTime && endTime != 0) {
            return false;
        }
        return true;
    }

    public ArrayList<Course> getFilteredResults() {return filteredResults;}
    public Search getSearch() {
        return search;
    }

    public int getCredits() {return credits;}

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
