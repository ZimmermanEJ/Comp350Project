package edu.gcc.comp350;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class LocalDataStorage implements IDataConnection {

    ArrayList<User> users;
    ArrayList<Schedule> schedules;
    ArrayList<Course> courses;
    File usersFile;
    File schedulesFile;
    File courseFile;

    /**
     * Constructor to initialize LocalDataStorage with file paths.
     * @param coursePath Path to the courses file.
     * @param usersPath Path to the users file.
     * @param schedulesPath Path to the schedules file.
     */
    public LocalDataStorage(String coursePath, String usersPath, String schedulesPath){
        usersFile = new File(usersPath);
        schedulesFile = new File(schedulesPath);
        courseFile = new File(coursePath);
        Gson gson = new Gson();
        // users
        try {
            FileReader usersReader = new FileReader(usersFile);
            users = gson.fromJson(usersReader,new TypeToken<ArrayList<User>>(){}.getType());
            usersReader.close();
        }
        catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            users = new ArrayList<>();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            courses = new ArrayList<>();
        }
        // schedules
        try {
            FileReader schedulesReader = new FileReader(schedulesFile);
            schedules = gson.fromJson(schedulesReader, new TypeToken<List<Schedule>>() {}.getType());
            schedulesReader.close();
        }
        catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            schedules = new ArrayList<>();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            courses = new ArrayList<>();
        }
        // courses
        try{
            FileReader courseReader = new FileReader(courseFile);
            courses = gson.fromJson(courseReader,new TypeToken<List<Course>>(){}.getType());
            courseReader.close();
        }
        catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            courses = new ArrayList<>();
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
            courses = new ArrayList<>();
        }
    }

    /**
     * Searches for courses that match the given search criteria.
     * @param search The search criteria.
     * @return The search results.
     */
    @Override
    public Search GetCoursesSearch(Search search){
        ArrayList<Course> retCourses = new ArrayList<>();
        for(Course course : courses){
            if (CourseContainsKeywords(course, search.getKeywords())){
                retCourses.add(course);
            }
        }
        search.SetResults(retCourses);
        return search;
    }

    /**
     * Retrieves a course by its reference number.
     * @param referenceNumber The reference number of the course.
     * @return The course with the given reference number, or null if not found.
     */
    public Course GetCourseByRef(int referenceNumber){
        for (Course course: courses){
            if (course.getReferenceNumber() == referenceNumber){
                return course;
            }
        }
        return null;
    }

    /**
     * Checks if a course contains all the given keywords.
     * @param course The course to check.
     * @param keywords The keywords to search for.
     * @return True if the course contains all the keywords, false otherwise.
     */
    private boolean CourseContainsKeywords(Course course, ArrayList<String> keywords){
        for (String keyword : keywords){
            if (!course.getTitle().toLowerCase().contains((keyword.toLowerCase()))){
                return false;
            }
        }
        return true;
    }

    /**
     * Saves a schedule.
     * @param schedule The schedule to save.
     * @return The saved schedule.
     */
    @Override
    public Schedule SaveSchedule(Schedule schedule){
        for (int i = 0; i < schedules.size(); i++) {
            if (schedules.get(i).getScheduleID() == schedule.getScheduleID()
                    && schedules.get(i).getUserID() == schedule.getUserID()) {
                return schedule;
            }
        }
        schedules.add(schedule);

        return schedule;
    }

    /**
     * Retrieves a user by their name.
     * @param name The name of the user.
     * @return The user with the given name, or null if not found.
     */
    @Override
    public User GetUserByName(String name){
        for (User user: users){
            if (user.getName().equals(name)){
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves a user by their email.
     * @param email The email of the user.
     * @return The user with the given email, or null if not found.
     */
    @Override
    public User GetUserByEmail(String email){
        for (User user: users){
            if (user.getEmail().equalsIgnoreCase(email)){
                return user;
            }
        }
        return null;
    }

    /**
     * Creates a new user.
     * @param user The user to create.
     * @return The created user, or null if a user with the same email already exists.
     */
    @Override
    public User CreateNewUser(User user){
        if (GetUserByEmail(user.getEmail())==null){
            users.add(user);
            user.setUserID(users.size());
            return user;
        }
        return null;
    }

    /**
     * Creates a new schedule.
     * @param schedule The schedule to create.
     * @return The created schedule.
     */
    @Override
    public Schedule CreateNewSchedule(Schedule schedule){
        if (GetUserIdSchedules(schedule.getUserID()).isEmpty()){
            schedule.setScheduleID(0);
            return schedule;
        }
        schedule.setScheduleID(GetUserIdSchedules(schedule.getUserID()).get(GetUserIdSchedules(schedule.getUserID()).size() - 1).getScheduleID() + 1);
        schedules.add(schedule);
        return schedule;
    }

    /**
     * Retrieves a course by its name.
     * @param name The name of the course.
     * @return The course with the given name, or null if not found.
     */
    @Override
    public Course GetCourseByName(String name){
        for (Course course: courses){
            String courseName = course.getDepartment() + " " + course.getCourseID() + course.getSectionCode();
            if (courseName.equals(name)){
                return course;
            }
        }
        return null;
    }

    /**
     * Retrieves all schedules for a given user ID.
     * @param userID The user ID.
     * @return A list of schedules for the user.
     */
    @Override
    public ArrayList<Schedule> GetUserIdSchedules(int userID){
        ArrayList<Schedule> userSchedules = new ArrayList<>();
        for (Schedule schedule : schedules) {
            if (schedule.getUserID() == userID) {
                userSchedules.add(schedule);
            }
        }
        return userSchedules;
    }

    /**
     * Retrieves a schedule by its ID.
     * @param scheduleID The schedule ID.
     * @return The schedule with the given ID, or null if not found.
     */
    @Override
    public Schedule GetScheduleId(int userID, int scheduleID){
        ArrayList<Schedule> userSchedules = GetUserIdSchedules(userID);
        for (Schedule schedule: userSchedules){
            if (schedule.getScheduleID()==scheduleID){
                return schedule;
            }
        }
        return null;
    }

    /**
     * Deletes a schedule.
     * @param schedule The schedule to delete.
     * @return True if the schedule was deleted, false otherwise.
     */
    @Override
    public boolean DeleteSchedule(Schedule schedule){
        schedules.remove(schedule);
        return true;
    }

    /**
     * Closes the connection and saves the data to the files.
     */
    @Override
    public void CloseConnection(){
        Gson gson = new GsonBuilder().create();
        try {
            System.out.println(users);
            System.out.println(schedules);
            FileWriter usersWriter = new FileWriter(usersFile);
            FileWriter schedulesWriter = new FileWriter(schedulesFile);
            usersWriter.write(gson.toJson(users, new TypeToken<ArrayList<User>>() {}.getType()));
            schedulesWriter.write(gson.toJson(schedules, new TypeToken<ArrayList<Schedule>>() {}.getType()));
            usersWriter.close();
            schedulesWriter.close();

        }
        catch (IOException ex){
            System.out.println("Error saving data");
            System.out.println(ex.getMessage());
        }
    }

}