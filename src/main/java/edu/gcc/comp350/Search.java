package edu.gcc.comp350;

import java.util.ArrayList;

public class Search {

    private ArrayList<String> keywords;
    private ArrayList<Course> searchResults;

    /**
     * Constructor for Search object, initializes the keywords and searchResults.
     * @param keywords
     */
    public Search(ArrayList<String> keywords) {
        this.keywords = keywords;
        this.searchResults = new ArrayList<Course>();
    }
//    public Search(ArrayList<Course> example){
//        searchResults = example;
//    }

    /**
     * Sets the search results to the given list of courses.
     * @param courses
     */
    public void SetResults(ArrayList<Course> courses) {
        searchResults = courses;
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public ArrayList<Course> getSearchResults() {
        return searchResults;
    }
}
