package edu.gcc.comp350;

import java.util.ArrayList;

public class Search {

    private ArrayList<String> keywords;
    private ArrayList<Course> searchResults;

    public Search(ArrayList<String> keywords) {

    }
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
