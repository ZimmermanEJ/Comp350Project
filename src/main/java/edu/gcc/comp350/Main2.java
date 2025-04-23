package edu.gcc.comp350;

import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;

import static spark.Spark.*;

public class Main2 {

    static IDataConnection data = new LocalDataStorage("courses.json", "users.json", "schedules.json");
    static User currentUser;
    static ArrayList<Schedule> schedules;
    static Gson gson = new Gson();

    public static void main(String[] args) {
        // Allow requests from any origin (you can restrict this later to a specific origin)
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // Enable CORS for port 3000 (React frontend)
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "http://localhost:3000");  // Restrict to a specific origin
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        // get user route
        get("/api/user", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            if (currentUser.getUserID() == userID) {
                String userJson = gson.toJson(currentUser);
                return "{\"status\": \"success\", \"message\": \"User retrieved\", \"user\": " + userJson + "}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // Login route
        post("/api/login", (req, res) -> {
            res.type("application/json");
            String email = req.queryParams("email");
            String password = req.queryParams("password");

            currentUser = data.GetUserByEmail(email);
            if (currentUser == null) {
                res.status(401);
                return "{\"status\": \"error\", \"message\": \"Invalid email\"}";
            } else if (!Arrays.equals(currentUser.getPasswordHash(), currentUser.hash(password))) {
                res.status(401);
                return "{\"status\": \"error\", \"message\": \"Invalid password\"}";
            }
            else {
                schedules = data.GetUserIdSchedules(currentUser.getUserID());
                String userJson = gson.toJson(currentUser);
                String schedulesJson = gson.toJson(schedules);
                return "{\"status\": \"success\", \"message\": \"Login successful\", \"user\": " + userJson + ", \"schedules\": " + schedulesJson + "}";
            }
        });

        // get all schedules route
        get("/api/schedules", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));

            if (currentUser.getUserID() == userID) {
                schedules = data.GetUserIdSchedules(userID);
                String schedulesJson = gson.toJson(schedules);
                return "{\"status\": \"success\", \"message\": \"Schedules retrieved\", \"schedules\": " + schedulesJson + "}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // Open one schedule route
        get("/api/schedule", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            int scheduleID = Integer.parseInt(req.queryParams("scheduleID"));

            if (currentUser.getUserID() == userID) {
                Schedule schedule = data.GetScheduleId(userID, scheduleID);
                if (schedule == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
                }
                String scheduleJson = gson.toJson(schedule);

                ArrayList<Course> courses = new ArrayList<>();
                for (int refNum : schedule.getCourses()) {
                    Course course = data.GetCourseByRef(refNum);
                    courses.add(course);
                }
                String coursesJson = gson.toJson(courses);
                return "{\"status\": \"success\", \"message\": \"Schedule opened\", \"schedule\": " + scheduleJson + ", \"courses\": " + coursesJson + ", \"credits\": " + schedule.getTotalCredits() + "}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // create schedule route
        post("/api/schedule", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            String name = req.queryParams("name");

            if (currentUser.getUserID() == userID) {
                Schedule schedule = new Schedule(userID, name);
                schedule = data.CreateNewSchedule(schedule);
                String scheduleJson = gson.toJson(schedule);
                data.CloseConnection();

                res.status(200);
                return "{\"status\": \"success\", \"message\": \"Schedule created\", \"schedule\": " + scheduleJson + "}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // delete schedule route
        delete("/api/schedule", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            int scheduleID = Integer.parseInt(req.queryParams("scheduleID"));

            if (currentUser.getUserID() == userID) {
                Schedule schedule = data.GetScheduleId(userID, scheduleID);
                if (schedule == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
                }
                boolean del = data.DeleteSchedule(schedule);
                if (del) {
                    data.CloseConnection();
                    return "{\"status\": \"success\", \"message\": \"Schedule deleted\"}";
                }
                res.status(404);
                return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // delete course route
        delete("/api/course", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            int scheduleID = Integer.parseInt(req.queryParams("scheduleID"));
            int referenceNumber = Integer.parseInt(req.queryParams("referenceNumber"));

            if (currentUser.getUserID() == userID) {
                Schedule schedule = data.GetScheduleId(userID, scheduleID);
                if (schedule == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
                }
                boolean rem = schedule.removeCourse(referenceNumber);
                if (rem) {
                    return "{\"status\": \"success\", \"message\": \"Course deleted\"}";
                }
                res.status(404);
                return "{\"status\": \"error\", \"message\": \"Course not found\"}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // logout route
        post("/api/logout", (req, res) -> {
            res.type("application/json");
            currentUser = null;
            schedules = null;
            return "{\"status\": \"success\", \"message\": \"Logged out\"}";
        });

        // signup route
        post("/api/signup", (req, res) -> {
            res.type("application/json");
            String email = req.queryParams("email");
            String password = req.queryParams("password");
            String name = req.queryParams("name");

            if (data.GetUserByEmail(email) != null) {
                res.status(409);
                return "{\"status\": \"error\", \"message\": \"Email already exists\"}";
            }

            User newUser = new User(name, email, password);
            data.CreateNewUser(newUser);
            data.CloseConnection();
            currentUser = newUser;
            String userJson = gson.toJson(currentUser);
            String schedulesJson = gson.toJson(schedules);
            return "{\"status\": \"success\", \"message\": \"Login successful\", \"user\": " + userJson + ", \"schedules\": " + schedulesJson + "}";
        });

        // save schedule route
        post("/api/saveschedule", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            int scheduleID = Integer.parseInt(req.queryParams("scheduleID"));

            if (currentUser.getUserID() == userID) {
                Schedule schedule = data.GetScheduleId(userID, scheduleID);
                if (schedule == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
                }
                data.SaveSchedule(schedule);
                data.CloseConnection();
                return "{\"status\": \"success\", \"message\": \"Schedule saved\"}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // set major year route
        put("/api/setmajoryear", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            String major = req.queryParams("major");
            int year = Integer.parseInt(req.queryParams("year"));

            if (currentUser.getUserID() == userID) {
                try {
                    User user = data.GetUserByEmail(currentUser.getEmail());
                    user.setMajor(major);
                    user.setYear(year);
                    data.CloseConnection();
                } catch (Exception e) {
                    res.status(501);
                    return "{\"status\": \"error\", \"message\": " + e.getMessage() + "}";
                }
                return "{\"status\": \"success\", \"message\": \"Major and year updated\"}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });
    }
}