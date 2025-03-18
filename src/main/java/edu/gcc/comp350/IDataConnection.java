package edu.gcc.comp350;

import java.util.List;

public interface IDataConnection {

    public Search GetCoursesSearch(Search search);

    public Schedule SaveSchedule(Schedule schedule);

    public User GetUserByName(String name);

    public Course GetCourseByName(String name);

    public List<Schedule> GetUserIdSchedules(int userID);

    public Schedule GetScheduleId(int scheduleID);

    public void CloseConnection();

}
