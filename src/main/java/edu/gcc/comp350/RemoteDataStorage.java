package edu.gcc.comp350;

import java.util.ArrayList;

import com.mongodb.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.*;

public class RemoteDataStorage implements IDataConnection {

    String uri = "mongodb+srv://client:scrumwhere@projectcluster.uwc5pcl.mongodb.net/?retryWrites=true&w=majority&appName=ProjectCluster";
    MongoClient client;
    MongoDatabase database;
    MongoCollection<Course> courses;
    MongoCollection<User> users;
    MongoCollection<Schedule> schedules;

    public RemoteDataStorage() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(new UserCodec(), new ScheduleCodec(), new CourseCodec()));

        // Initialize the MongoClient and keep it open
        client = MongoClients.create(uri);
        database = client.getDatabase("scheduling").withCodecRegistry(codecRegistry);

        try {
            // Send a ping to confirm a successful connection
            Bson command = new BsonDocument("ping", new BsonInt64(1));
            Document commandResult = database.runCommand(command);
            courses = database.getCollection("courses", Course.class);
            users = database.getCollection("users", User.class);
            schedules = database.getCollection("schedules", Schedule.class);
            System.out.println("Connected to DB");
        } catch (MongoException me) {
            System.err.println("Connection Error: " + me.getMessage());
        }
    }

    @Override
    public Course GetCourseByRef(int ref) {
        return courses.find(eq("referenceNumber",ref)).first();
    }

    @Override
    public Search GetCoursesSearch(Search search) {
        ArrayList<String> keywords = search.getKeywords();
        ArrayList<Bson> filters = new ArrayList<>();

        if (!keywords.isEmpty()) {
            for (String keyword : keywords) {
                filters.add(new Document("title", new Document("$regex", keyword).append("$options", "i")));
            }
            ArrayList<Course> results = courses.find(new Document("$and", filters)).into(new ArrayList<>());
            search.SetResults(results);
        }
        else {
            ArrayList<Course> results = courses.find(new Document()).into(new ArrayList<>());
            search.SetResults(results);
        }


        return search;
    }

    @Override
    public Schedule SaveSchedule(Schedule schedule) {
        try {
            Schedule sch = schedules.find(and(eq("userID", schedule.getUserID()), eq("scheduleID", schedule.getScheduleID()))).first();

            if (sch != null){
                // Update the existing schedule
                schedules.replaceOne(and(eq("userID", schedule.getUserID()), eq("scheduleID", schedule.getScheduleID())), schedule);
            } else {
                InsertOneResult res = schedules.insertOne(schedule);
                schedule = schedules.find(eq("_id",res.getInsertedId())).first();
            }
            return schedule;
        } catch (MongoException e) {
            System.err.println("Error saving schedule: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User GetUserByName(String name) {
        try {
            return users.find(eq("name", name)).first();
        } catch (MongoException e) {
            System.err.println("Error retrieving user by name: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User GetUserByEmail(String email) {
        try {
            return users.find(eq("email", email)).first();
        } catch (MongoException e) {
            System.err.println("Error retrieving user by email: " + e.getMessage());
            return null;
        }
    }

    @Override
    public User CreateNewUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getName() == null || user.getPasswordHash() == null) {
            throw new IllegalArgumentException("User fields cannot be null");
        }

        if (GetUserByEmail(user.getEmail()) == null) {
            try {
                users.insertOne(user);
                return GetUserByEmail(user.getEmail());
            } catch (MongoException e) {
                System.err.println("Error creating new user: " + e.getMessage());
                return null;
            }
        }
        return null; // Return null if a user with the same email already exists
    }

    @Override
    public Schedule CreateNewSchedule(Schedule schedule) {
        try {
            // Check if the user already has schedules
            ArrayList<Schedule> userSchedules = GetUserIdSchedules(schedule.getUserID());
            if (userSchedules.isEmpty()) {
                schedule.setScheduleID(0); // Assign the first schedule ID
            } else {
                // Assign the next available schedule ID
                int lastScheduleID = userSchedules.getLast().getScheduleID();
                schedule.setScheduleID(lastScheduleID + 1);
            }
            schedules.insertOne(schedule); // Insert the schedule into the database
            return schedule;
        } catch (MongoException e) {
            System.err.println("Error creating new schedule: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Course GetCourseByName(String name) {

        String deptName = name.substring(0, name.indexOf(" "));
        int courseNumber = Integer.parseInt(name.replaceAll("[^0-9]", ""));
        char sectionCode = name.charAt(name.length() - 1);
        try {
            return courses.find(and(
                    eq("department", deptName),
                    eq("courseID", courseNumber),
                    eq("sectionCode", String.valueOf(sectionCode))
            )).first();
        } catch (MongoException e) {
            System.err.println("Error retrieving course by name: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ArrayList<Schedule> GetUserIdSchedules(int userID) {
        try {
            return schedules.find(eq("userID", userID)).into(new ArrayList<>());
        } catch (MongoException e) {
            System.err.println("Error retrieving schedules for user ID: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Schedule GetScheduleId(int userID, int scheduleID) {
        try {
            return schedules.find(and(eq("userID", userID), eq("scheduleID", scheduleID))).first();
        } catch (MongoException e) {
            System.err.println("Error retrieving schedule by ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean DeleteSchedule(Schedule schedule) {
        try {
            schedules.deleteOne(and(eq("userID", schedule.getUserID()), eq("scheduleID", schedule.getScheduleID())));
            return true;
        } catch (MongoException e) {
            System.err.println("Error deleting schedule: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void CloseConnection() {
        client.close();
        client = null;
        database = null;
    }
}
