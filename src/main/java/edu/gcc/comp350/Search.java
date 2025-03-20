package edu.gcc.comp350;

import java.util.ArrayList;

public class Search {

    private ArrayList<String> keywords;
    private ArrayList<Course> searchResults;

    public Search(ArrayList<String> keywords) {

    }
    public Search(ArrayList<String> keywords, ArrayList<Course> searchResults){

    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public ArrayList<Course> getSearchResults() {
        return searchResults;
    }
}
