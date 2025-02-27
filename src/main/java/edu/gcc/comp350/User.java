package edu.gcc.comp350;
import java.util.ArrayList;

public class User {
    private ArrayList<Schedule> schedules;
    private String name;
    private String major;
    private int year;
    private String email;
    private int userID;
    private String passwordHash;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.passwordHash = password;
        this.schedules = new ArrayList<Schedule>();
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public void deleteSchedule(Schedule schedule) { }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public Schedule getSchedule(int scheduleID) { return null; }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {

    }
}