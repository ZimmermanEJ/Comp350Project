package edu.gcc.comp350;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class LocalDataStorage implements IDataConnection {

    List<User> users;
    List<Schedule> schedules;
    List<Course> courses;
    File usersFile;
    File schedulesFile;
    File courseFile;


    public LocalDataStorage(String coursePath, String usersPath, String schedulesPath){
        usersFile = new File(usersPath);
        schedulesFile = new File(schedulesPath);
        courseFile = new File(coursePath);
        Gson gson = new Gson();
        try {
            FileReader usersReader = new FileReader(usersFile);
            FileReader schedulesReader = new FileReader(schedulesFile);
            FileReader courseReader = new FileReader(courseFile);
            users = gson.fromJson(usersReader.toString(),new TypeToken<List<User>>(){}.getType());
            schedules = gson.fromJson(schedulesReader.toString(),new TypeToken<List<Schedule>>(){}.getType());
            courses = gson.fromJson(courseReader.toString(),new TypeToken<List<Course>>(){}.getType());

        }
        catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            users = new ArrayList<>();
            schedules = new ArrayList<>();
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
        return new Search(search.getKeywords(),retCourses);
    }

    private boolean CourseContainsKeywords(Course course, List<String> keywords){
        for (String keyword : keywords){
            if (!course.getDescription().contains((keyword))){
                return false;
            }
        }
        return true;
    }

    @Override
    public Schedule SaveSchedule(Schedule schedule){
        for (int i = 0; i<schedules.size(); i++){
            if (schedules.get(i).getScheduleID() == schedule.getScheduleID()
                    && schedules.get(i).getUserID() == schedule.getUserID()){
                schedules.set(i,schedule);
                return schedule;
            }
        }
        schedules.add(schedule);
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
    public List<Schedule> GetUserIdSchedules(int userID){
        List<Schedule> userSchedules = new ArrayList<>();
        for (Schedule schedule: schedules){
            if (schedule.getUserID()==userID){
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
    public void CloseConnection(){
        Gson gson = new GsonBuilder().create();
        try {
            FileWriter usersWriter = new FileWriter(usersFile);
            FileWriter schedulesWriter = new FileWriter(schedulesFile);
            gson.toJson(users, usersWriter);
            gson.toJson(schedules, schedulesWriter);
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

}
