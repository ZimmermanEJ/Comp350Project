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

    /**
     * Constructs a Filter object with the specified criteria and filters the search results.
     *
     * @param credits      the number of credits for the course
     * @param department   the department offering the course (e.g., COMP)
     * @param courseNumber the course number (e.g., 350)
     * @param sectionCode  the section code (e.g., A)
     * @param classdays    the days the class is held (e.g., MWF or TR)
     * @param startTime    the start time of the course
     * @param endTime      the end time of the course
     * @param search       the search object containing the list of courses to filter
     */
    public Filter(int credits, String department, int courseNumber,
                  char sectionCode, String classdays, double startTime, double endTime,
                  Search search) {
            this.credits = credits;
            this.department = department;
            this.courseNumber = courseNumber;
            this.sectionCode = sectionCode;
            this.startTime = startTime;
            this.endTime = endTime;
            this.search = search;
            this.filteredResults = new ArrayList<>(this.search.getSearchResults());


        this.filteredResults.removeIf(course -> !helper(course, credits, department, courseNumber, sectionCode, classdays, startTime, endTime));

    }
    /**
     * Checks if the given course matches the specified filter criteria.
     *
     * @param course       the course to check against the filter criteria
     * @param credits      the number of credits for the course
     * @param department   the department offering the course
     * @param courseNumber the course number
     * @param sectionCode  the section code
     * @param d            the days the class is held
     * @param startTime    the start time of the course
     * @param endTime      the end time of the course
     * @return boolean     true if the course matches the filter criteria, false otherwise
     */
    public boolean helper(Course course, int credits, String department, int courseNumber, char sectionCode, String d, double startTime, double endTime) {
        if (course.getCredits() != credits && credits != 0) {
            return false;
        } else if (!Objects.equals(course.getDays().toString(), d) && !d.isEmpty()) {
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
