package edu.gcc.comp350;

import java.util.ArrayList;
import java.util.List;

public interface IDataConnection {


    public Course GetCourseByRef(int ref);

    public Search GetCoursesSearch(Search search);

    public Schedule SaveSchedule(Schedule schedule);

    public User GetUserByName(String name);

    public User GetUserByEmail(String email);

    public User CreateNewUser(User user);

    public Schedule CreateNewSchedule(Schedule schedule);

    public Course GetCourseByName(String name);

    public ArrayList<Schedule> GetUserIdSchedules(int userID);

    public Schedule GetScheduleId(int userID, int scheduleID);

    public boolean DeleteSchedule(Schedule schedule);

    public void CloseConnection();

}
