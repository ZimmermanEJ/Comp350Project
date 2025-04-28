package edu.gcc.comp350;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;

import static spark.Spark.*;

public class Main2 {

    static IDataConnection data = new RemoteDataStorage();
    // static IDataConnection data = new LocalDataStorage("courses.json", "users.json", "schedules.json");
    static User currentUser;
    static ArrayList<Schedule> schedules;
    static Gson gson = new Gson();
    static ArrayList<Integer> previousActions;

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

            User tempUser = data.GetUserByEmail(email);
            previousActions = new ArrayList<>();
            if (tempUser == null) {
                res.status(401);
                return "{\"status\": \"error\", \"message\": \"Invalid email\"}";
            } else if (!Arrays.equals(tempUser.getPasswordHash(), tempUser.hash(password))) {
                res.status(401);
                return "{\"status\": \"error\", \"message\": \"Invalid password\"}";
            }
            else {
                currentUser = tempUser;
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
                previousActions = new ArrayList<>();
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
            boolean useAI = Boolean.parseBoolean(req.queryParams("useAI"));
            String major = req.queryParams("major");
            int year = Integer.parseInt(req.queryParams("year"));

            Schedule schedule;
            if (currentUser.getUserID() == userID) {
                if (useAI) {
                    CurlExecutor curl = new CurlExecutor(major, String.valueOf(year));
                    String output = null;
                    try {
                        output = curl.runCurlCommand();
                    } catch (UnsupportedEncodingException e) {
                    }

                    output = output.replaceAll("[\\[\\]\\s]", ""); // remove [ ] and spaces

                    // Split the string into individual number strings
                    String[] numberStrings = output.split(",");

                    // Convert to int array
                    int[] courseRefs = new int[numberStrings.length];
                    for (int i = 0; i < numberStrings.length; i++) {
                        courseRefs[i] = Integer.parseInt(numberStrings[i]);
                    }

                    schedule = new Schedule(currentUser.getUserID(), name);
                    for (int ref : courseRefs) {
                        Course course = data.GetCourseByRef(ref);
                        schedule.addCourse(course);
                    }

                } else {
                    schedule = new Schedule(userID, name);
                }


                schedule = data.CreateNewSchedule(schedule);
                String scheduleJson = gson.toJson(schedule);
                data.SaveSchedule(schedule);
                // data.CloseConnection();
                previousActions = new ArrayList<>();
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
                    // data.CloseConnection();
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
                    previousActions.add(referenceNumber);
                    String scheduleJson = gson.toJson(schedule);
                    data.SaveSchedule(schedule);
                    // data.CloseConnection();
                    return "{\"status\": \"success\", \"message\": \"Course removed\", \"schedule\": " + scheduleJson + "}";
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
            // data.CloseConnection();
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
            newUser = data.CreateNewUser(newUser);
            schedules = new ArrayList<>();
            // data.CloseConnection();
            currentUser = newUser;
            previousActions = new ArrayList<>();
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
                // data.CloseConnection();
                return "{\"status\": \"success\", \"message\": \"Schedule saved\"}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // get major year route
        get("/api/getmajoryear", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));

             if (currentUser.getUserID() == userID) {
                User user = data.GetUserByEmail(currentUser.getEmail());
                String major = user.getMajor();
                int year = user.getYear();
                return "{\"status\": \"success\", \"message\": \"Major and year retrieved\", \"major\": \"" + major + "\", \"year\": " + year + "}";
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
                    data.SaveUser(user);
                    // data.CloseConnection();
                } catch (Exception e) {
                    res.status(501);
                    return "{\"status\": \"error\", \"message\": " + e.getMessage() + "}";
                }
                return "{\"status\": \"success\", \"message\": \"Major and year updated\"}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        //create search route
//        get("/api/search", (req, res) -> {
//            res.type("application/json");
//            String searchString = req.queryParams("searchString");
//            Scanner scan = new Scanner(searchString);
//            ArrayList<String> keywords = new ArrayList<>();
//            while (scan.hasNext()) {
//                String word = scan.next();
//                keywords.add(word);
//            }
//            Search s = new Search(keywords);
//            s = data.GetCoursesSearch(s);
//            String coursesJson = gson.toJson(s.getSearchResults());
//            return "{\"status\": \"success\", \"message\": \"Courses retrieved\", \"courses\": " + coursesJson + "}";
//        });

        get("/api/search", (req, res) -> {
            res.type("application/json");
            String searchString = req.queryParams("searchString");
            Scanner scan = new Scanner(searchString);
            ArrayList<String> keywords = new ArrayList<>();
            while (scan.hasNext()) {
                String word = scan.next();
                keywords.add(word);
            }
            Search search = new Search(keywords);
            search = data.GetCoursesSearch(search); // Populate search results
            String searchJson = gson.toJson(search);
            return searchJson;
        });


        // add course route
        put("/api/addToSchedule", (req, res) -> {
            res.type("application/json");
            try {
                int userID = Integer.parseInt(req.queryParams("userID"));
                int scheduleID = Integer.parseInt(req.queryParams("scheduleID"));
                int referenceNumber = Integer.parseInt(req.queryParams("referenceNumber"));

                if (currentUser == null) {
                    res.status(401);
                    return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
                }

                Schedule schedule = data.GetScheduleId(userID, scheduleID);
                if (schedule == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
                }

                Course course = data.GetCourseByRef(referenceNumber);
                if (course == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Course not found\"}";
                }

                String conflict = schedule.addCourse(course);
                if (conflict == null) {
                    previousActions.add(course.getReferenceNumber());
                    String scheduleJson = gson.toJson(schedule);
                    String newCourseJson = gson.toJson(course);
                    data.SaveSchedule(schedule);
                    // data.CloseConnection();
                    return "{\"status\": \"success\", \"message\": \"Course added\", \"schedule\": " + scheduleJson + ", \"course\": " + newCourseJson + "}";
                }
                return "{\"status\": \"error\", \"message\": \"Course conflict with " + conflict + "\"}";
            } catch (NumberFormatException e) {
                e.printStackTrace();
                res.status(400);
                return "{\"status\": \"error\", \"message\": \"Invalid input format\"}";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "{\"status\": \"error\", \"message\": \"Internal server error\"}";
            }
        });

        // export schedule route
        post("/api/exportSchedule", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            int scheduleID = Integer.parseInt(req.queryParams("scheduleID"));
            String fileName = req.queryParams("fileName");

            if (currentUser.getUserID() == userID) {
                Schedule schedule = data.GetScheduleId(userID, scheduleID);
                if (schedule == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
                }
                String scheduleJson = gson.toJson(schedule);
                schedule.exportSchedule(fileName);
                return "{\"status\": \"success\", \"message\": \"Schedule exported\", \"schedule\": " + scheduleJson + "}";
            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });

        // undo course-related action
        put("/api/undo", (req, res) -> {
            res.type("application/json");
            int userID = Integer.parseInt(req.queryParams("userID"));
            int scheduleID = Integer.parseInt(req.queryParams("scheduleID"));

            if (currentUser.getUserID() == userID) {
                Schedule schedule = data.GetScheduleId(userID, scheduleID);
                if (schedule == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Schedule not found\"}";
                }

                if (previousActions.isEmpty()) {
                    return "{\"status\": \"error\", \"message\": \"No actions to undo\"}";
                }
                int refNum = previousActions.get(previousActions.size() - 1);
                Course course = data.GetCourseByRef(refNum);
                if (course == null) {
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Course not found\"}";
                }

                if (schedule.getCourses().contains(refNum)) {
                    boolean rem = schedule.removeCourse(course.getReferenceNumber());
                    if (rem) {
                        String scheduleJson = gson.toJson(schedule);
                        String courseJson = gson.toJson(course);
                        previousActions.remove(previousActions.size() - 1);
                        boolean isLastAction = previousActions.isEmpty();
                        // data.CloseConnection();
                        return "{\"status\": \"success\", \"message\": \"Course removed\", \"schedule\": " + scheduleJson + ", \"course\": " + courseJson + ", \"isLast\": " + isLastAction + "}";
                    }
                    res.status(404);
                    return "{\"status\": \"error\", \"message\": \"Course not found\"}";
                } else {
                    String conflict = schedule.addCourse(course);
                    if (conflict == null) {
                        String scheduleJson = gson.toJson(schedule);
                        String newCourseJson = gson.toJson(course);
                        data.SaveSchedule(schedule);
                        // data.CloseConnection();
                        previousActions.remove(previousActions.size() - 1);
                        boolean isLastAction = previousActions.isEmpty();
                        return "{\"status\": \"success\", \"message\": \"Course added\", \"schedule\": " + scheduleJson + ", \"course\": " + newCourseJson + ", \"isLast\": " + isLastAction + "}";
                    }
                    // there is conflict
                    res.status(409);
                    return "{\"status\": \"error\", \"message\": \"Course conflict with " + conflict + "\"}";
                }

            }
            res.status(401);
            return "{\"status\": \"error\", \"message\": \"Unauthorized\"}";
        });
    }
}