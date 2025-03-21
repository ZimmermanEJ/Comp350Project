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

    private boolean CourseContainsKeywords(Course course, ArrayList<String> keywords){
        for (String keyword : keywords){
            if (!course.getTitle().toLowerCase().contains((keyword.toLowerCase()))){
                return false;
            }
        }
        return true;
    }

    @Override
    public Schedule SaveSchedule(Schedule schedule){
        try {
            for (int i = 0; i < schedules.size(); i++) {
                if (schedules.get(i).getScheduleID() == schedule.getScheduleID()
                        && schedules.get(i).getUserID() == schedule.getUserID()) {
                    schedules.set(i, schedule);
                    return schedule;
                }
            }
        }
        catch (Exception e){
            schedules = new ArrayList<>();
            schedules.add(schedule);
        }

        return schedule;
    }

    @Override
    public User GetUserByName(String name){
        for (User user: users){
            if (user.getName().equals(name)){
                return user;
            }
        }
        return null;
    }

    @Override
    public User GetUserByEmail(String email){
        try {
            for (User user : users) {
                if (user.getEmail().equalsIgnoreCase(email)) {
                    return user;
                }
            }
        }catch (Exception ignored){
        }
        return null;
    }

    @Override
    public User CreateNewUser(User user){
        try {
            if (GetUserByEmail(user.getEmail()) == null) {
                users.add(user);
                user.setUserID(users.size());
                return user;
            }
        } catch (Exception ingored){
            this.users = new ArrayList<>();
            users.add(user);
            user.setUserID(users.size());
            return user;
        }
        return null;
    }

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

    @Override
    public Schedule GetScheduleId(int scheduleID){
        for (Schedule schedule: schedules){
            if (schedule.getScheduleID()==scheduleID){
                return schedule;
            }
        }
        return null;
    }

    @Override
    public boolean DeleteSchedule(Schedule schedule){
        Schedule target = GetScheduleId(schedule.getScheduleID());
        if (target != null) {
            schedules.remove(target);
            return true;
        }
        return false;
    }

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
