package edu.gcc.comp350;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        run();
    }
    public static void run() {

        IDataConnection data = new LocalDataStorage("courses.json", "users.json", "schedules.json");
        // structure of I/O

        // login or signup
            // open schedule
                // view
                    // event
                    // remove course
                // search
                    // filters
            // create schedule
            // delete schedule


        // arraylist used for storing users with fake user added for testing
        User tempUser = new User("Temp", "a@a.com", "pw");
        data.CreateNewUser(tempUser);

        // Course used for testing
        double[][] time = {{}, {14, 14.5}, {}, {14, 14.5}, {}, {14, 14.5}, {}};
        Course softwareEngineeringA = new Course("COMP", 350,
                'A', "Software Engineering", 3, "description",
                "Dr. Hutchins", 123456, Course.Days.MWF, 14.0, 14.5,
                time);
        double[][] time1 = {{}, {12, 12.5}, {}, {12, 12.5}, {11, 11.5}, {12, 12.5}, {}};
        Course discrete = new Course("MATH", 214,
                'A', "Discrete Math", 3, "description",
                "Dr. Bancroft", 111111, Course.Days.MWF, 12.0, 12.5,
                time1);
        double[][] time2 = {{}, {}, {9.3, 10.45}, {}, {9.3, 10.45}, {}, {}};
        Course OS = new Course("COMP", 340,
                'B', "Operating Systems", 3, "description",
                "Dr. Zhang", 222222, Course.Days.TR, 9.3, 10.45,
                time2);

        Scanner scanner = new Scanner(System.in);

        User currentUser = null;
        while (true) {

            System.out.print("Enter 'login', 'signup', or 'quit': ");
            String nextInput = scanner.nextLine();
            ArrayList<Schedule> schedules = new ArrayList<>();
            if (nextInput.equalsIgnoreCase("login")) {
                int failedAttempts = 0;
                while (true) {
                    if(failedAttempts >= 5){
                        System.out.println("Too many failed attempts, exiting");
                        break;

                    }
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    currentUser = data.GetUserByEmail(email);
                    if (currentUser == null || !Arrays.equals(currentUser.getPasswordHash(), currentUser.hash(password))) {
                        System.out.println("Invalid credentials");
                        failedAttempts++;
                    } else {
                        schedules = data.GetUserIdSchedules(currentUser.getUserID());
                        break;
                    }
                }
                if(failedAttempts >= 5){
                    break;
                }



            } else if (nextInput.equalsIgnoreCase("signup")) {
                String name;
                String email;
                String password;
                while(true) {
                    System.out.print("Enter name: ");
                    name = scanner.nextLine();
                    if (Objects.equals(name, "")) {
                        System.out.println("Please enter a name");
                    }
                    else{
                        break;
                    }
                }
                while(true) {
                    System.out.print("Enter email: ");
                    email = scanner.nextLine();
                    if (Objects.equals(email, "")) {
                        System.out.println("Please enter an email");
                    }
                    else{
                        break;
                    }
                }
                while(true) {
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    if (Objects.equals(password, "")) {
                        System.out.println("Please enter a password");
                    }
                    else{
                        break;
                    }
                }
              User newUser = new User(name, email, password);
              data.CreateNewUser(newUser);
              currentUser = newUser;
              schedules = data.GetUserIdSchedules(currentUser.getUserID());
            } else if (nextInput.equalsIgnoreCase("quit")) {
                break;
            } else {
                System.out.println("Invalid input, try again");
                continue;
            }
            String scheduleInput = "";
            while (true) { // loop until user quits
                System.out.println("\nWelcome " + currentUser.getName() + "!");

                // user opens a schedule
                System.out.print("Enter the schedule id you would like to open, or 'show schedules', 'new', or 'delete' (or 'quit'): ");
                scheduleInput = scanner.nextLine();
                Schedule currentSchedule = null; // schedule to open

                // check what user input
                if (scheduleInput.equalsIgnoreCase("new")) { // create schedule
                    System.out.print("Enter a name for the schedule: ");
                    String scheduleName = scanner.nextLine();
                    Schedule mySchedule = new Schedule(currentUser.getUserID(), scheduleName, schedules.size());
                    schedules.add(mySchedule);

                    mySchedule.addCourse(softwareEngineeringA);
                    mySchedule.addCourse(discrete);
                    mySchedule.addCourse(OS);
                    currentSchedule = mySchedule;
                } else if (scheduleInput.equalsIgnoreCase("quit")) { // sign out
                    break;
                } else if (schedules.isEmpty()) { // user doesn't have any schedule to open
                    System.out.println("You don't have any existing schedules, try creating one.");
                    continue;
                } else if (scheduleInput.equalsIgnoreCase("show schedules")) {  // show schedules
                    if (schedules.isEmpty()) {
                        System.out.println("You don't have any schedules.");
                    } else {
                        System.out.println("Here are your schedules:");
                        for (Schedule schedule : schedules) {
                            System.out.println(schedule.listView());
                        }

                    }
                } else if (scheduleInput.equalsIgnoreCase("delete")){
                    System.out.print("Enter the ID of the schedule to delete (or 'quit'): ");
                    scheduleInput = scanner.nextLine();
                    if (scheduleInput.equalsIgnoreCase("quit")) {
                        data.SaveSchedule(currentSchedule);
                        continue;
                    }
                    try { //
                        int scheduleID = Integer.parseInt(scheduleInput);

                        boolean deleted = false;
                        for (Schedule schedule : schedules) {
                            if (schedule.getScheduleID() == scheduleID) { // schedule exists
                                deleted = data.DeleteSchedule(schedule);
                                System.out.println("Schedule " + schedule.getScheduleID() + " successfully deleted");
                                break;
                            }
                        }
                        if (!deleted) { // schedule does not exist
                            System.out.println("No schedule " + scheduleInput);
                        }
                        else {
                            schedules.remove(scheduleID);
                        }
                        continue;
                    } catch (NumberFormatException e) { // didn't input a number
                        System.out.println("Input not valid");
                        continue;
                    }
                } else { // user does have schedules
                    try { //
                        int scheduleID = Integer.parseInt(scheduleInput);

                        boolean assigned = false;
                        for (Schedule schedule : schedules) {
                            if (schedule.getScheduleID() == scheduleID) { // schedule exists
                                currentSchedule = schedule;
                                assigned = true;
                                break;
                            }
                        }
                        if (!assigned) { // schedule does not exist
                            System.out.println("No schedule " + scheduleInput + ". Try again");
                            continue;
                        }
                    } catch (NumberFormatException e) { // didn't input a number
                        System.out.println("Input not valid, try again");
                        continue;
                    }
                }

                // do whatever user wants to do with schedule
                while (true) {
                    System.out.println("\nCurrently viewing " + currentSchedule.getName());

                    System.out.print("Enter 'view' to view schedule, 'search' to search, or 'quit': ");
                    String nextAction = scanner.nextLine();

                    if (nextAction.equalsIgnoreCase("quit")) {
                        data.SaveSchedule(currentSchedule);
                        break;
                    } else if (nextAction.equalsIgnoreCase("view")) { // view schedule
                        while (true) {
                            System.out.println(currentSchedule.scheduleView());
                            System.out.print("Enter 'e' to add event, 'rc' to remove a course, 're' to remove an event, or 'quit': ");
                            String next = scanner.nextLine();

                            if (next.equalsIgnoreCase("quit")) {
                                break;
                            } else if (next.equalsIgnoreCase("e")){
                                // Add event
                                System.out.print("Event name: ");
                                String name = scanner.nextLine();
                                boolean[] days = new boolean[7];
                                String[] week = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                                for (int i = 0; i < 7; i++) {
                                    String day = week[i];
                                    while (true) {
                                        System.out.print("Is the event on " + day + "? ('y' or 'n'): ");
                                        String response = scanner.nextLine();
                                        if (response.equalsIgnoreCase("y")) {
                                            days[i]= true;
                                            break;
                                        } else if (response.equalsIgnoreCase("n")) {
                                            break;
                                        } else {
                                            System.out.println("Invalid input, try again");
                                        }
                                    }
                                }
                                double startT;
                                while (true) {
                                    System.out.print("Event start time (from 0.00 to 24.00): ");
                                    String startTime = scanner.nextLine();
                                    try {
                                        startT = Double.parseDouble(startTime);
                                        if (startT < 0 || startT > 24) {
                                            throw new Exception();
                                        }
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Invalid time, try again");
                                    }
                                }
                                double endT;
                                while (true) {
                                    System.out.print("Event end time (from 0.00 to 24.00): ");
                                    String endTime = scanner.nextLine();
                                    try {
                                        endT = Double.parseDouble(endTime);
                                        if (endT < 0 || endT > 24) {
                                            throw new Exception();
                                        }
                                        break;
                                    } catch (Exception e) {
                                        System.out.println("Invalid time, try again");
                                    }
                                }
                                System.out.print("Event description: ");
                                String description = scanner.nextLine();
                                Event e = new Event(name, days, startT, endT, description);
                                currentSchedule.addEvent(e);
                                System.out.println("Successfully added " + e.getName());
                            } else if (next.equalsIgnoreCase("rc")){
                                System.out.println(currentSchedule.getCourses());
                                System.out.print("Enter the reference # of course to be removed: ");
                                String course = scanner.nextLine();
                                try {
                                    int refNum = Integer.parseInt(course);
                                    if (currentSchedule.removeCourse(refNum)) {
                                        System.out.println("Course removed");
                                    } else {
                                        System.out.println("Course not found");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not a reference #");
                                }
                            } else if (next.equalsIgnoreCase("re")){
                                System.out.print("Enter the ID of event to be removed: ");
                                String event = scanner.nextLine();
                                try {
                                    int eID = Integer.parseInt(event);
                                    if (currentSchedule.removeEvent(eID)) {
                                        System.out.println("Event removed");
                                    } else {
                                        System.out.println("Event not found");
                                    }
                                } catch (Exception e) {
                                    System.out.println("Not a valid ID");
                                }
                            } else {
                                System.out.println("Input not recognized, try again");
                            }
                        }
                    } else if (nextAction.equalsIgnoreCase("search")) { // search
                        System.out.print("Enter your search, or 'quit': ");
                        String search = scanner.nextLine();
                        ArrayList<String> keywords = new ArrayList<>();
                        Scanner words = new Scanner(search);
                        while(words.hasNext()){
                            String word = words.next();
                            keywords.add(word);
                        }

                        if (search.equalsIgnoreCase("quit")) {
                            continue;
                        }

                        System.out.println("Searched for " + search);

                        //temporary search results








                        while (true) {
                            Search s = data.GetCoursesSearch(new Search(keywords));

                            Filter f;
                            boolean skip = false;
                            System.out.println("Here are your search results!\n");
                            for (Course course : s.getSearchResults()) {
                                System.out.println(course.toString());
                            }

                            System.out.println("\nFilters: Enter the filters you would like with a space in between, 'c' for credits, 'dep' for department, 'cn' for course number, 'cs' for course section, 'd' for days, 's' for start time, or 'e' for end time: ");
                            System.out.println("If you would not like any filters, type nothing. If you would like to quit, type 'quit'");
                            String filter = scanner.nextLine();
                            Scanner filterScanner = new Scanner(filter);
                            ArrayList<String> filters = new ArrayList<>();
                            while(filterScanner.hasNext()){
                                String f1 = filterScanner.next();
                                filters.add(f1);
                            }

                            String credits = "";
                            String department = "";
                            String courseNumber = "";
                            String days = "";
                            String section = "";
                            String startTime = "";
                            String endTime = "";

                            if(filter.equalsIgnoreCase("quit")) {
                                break;
                            }
//                            } else if (filter.equalsIgnoreCase("none")) {
//                                f = new Filter(0, "", 0, 'A', "", 0, 0, s);
//
//                            }
                            for (String f1 : filters) {
                                if (f1.equalsIgnoreCase("c")) {
                                    System.out.print("Enter credit filter: ");
                                    credits = scanner.nextLine();
                                } else if (f1.equalsIgnoreCase("dep")) {
                                    System.out.print("Enter department filter: ");
                                    department = scanner.nextLine();
                                } else if (f1.equalsIgnoreCase("cn")) {
                                    System.out.print("Enter course number filter: ");
                                    courseNumber = scanner.nextLine();

                                } else if (f1.equalsIgnoreCase("d")) {
                                    System.out.print("Enter days filter ('MWF' or 'TR'): ");
                                    days = scanner.nextLine();
                                }
                                else if (f1.equalsIgnoreCase("cs")) {
                                    System.out.print("Enter course section filter: ");
                                    section = scanner.nextLine();
                                }
                                else if (f1.equalsIgnoreCase("s")) {
                                    System.out.print("Enter start time filter: ");
                                    startTime = scanner.nextLine();
                                } else if (f1.equalsIgnoreCase("e")) {
                                    System.out.print("Enter end time filter: ");
                                    endTime = scanner.nextLine();

                                }
                                else {
                                    System.out.println("Invalid input, try again");
                                    skip = true;
                                    break;
                                }
                            }
                            if(!skip) {
                                if (credits.isEmpty()) {
                                    credits = "0";
                                }
                                if (courseNumber.isEmpty()) {
                                    courseNumber = "0";
                                }
                                if (startTime.isEmpty()) {
                                    startTime = "0";
                                }

                                if (endTime.isEmpty()) {
                                    endTime = "0";
                                }
                                if (section.isEmpty()) {
                                    section = " ";
                                }
                                //Creates a filter with the provided criteria
                                f = new Filter(Integer.parseInt(credits), department.toUpperCase(), Integer.parseInt(courseNumber), section.charAt(0), days, Double.parseDouble(startTime), Double.parseDouble(endTime), s);

                                System.out.println("Here are the filtered results!\n");
                                for (Course course : f.getFilteredResults()) {
                                    System.out.println(course.toString());
                                }
                                while (true) {
                                    System.out.println("Would you like to add a course to your schedule? ('y' or 'n'): \n");
                                    Scanner addCourse = new Scanner(System.in);
                                    String answer = addCourse.nextLine();
                                    if (answer.equalsIgnoreCase("y")) {
                                        System.out.println("Enter the course code of the course you would like to add: \n");
                                        int code = addCourse.nextInt();
                                        System.out.println("Enter the section code of the course you would like to add: \n");
                                        String sectionCode = addCourse.next();
                                        for (int i = 0; i < f.getFilteredResults().size(); i++) {
                                            if (f.getFilteredResults().get(i).getCourseNumber() == code && f.getFilteredResults().get(i).getSectionCode() == sectionCode.charAt(0)) {
                                                if (currentSchedule.addCourse(f.getFilteredResults().get(i))) {
                                                    System.out.println("Course added to schedule!");
                                                    break;
                                                }
                                                break;
                                            }
                                        }

                                    } else if (answer.equalsIgnoreCase("n")) {
                                        break;
                                    } else {
                                        System.out.println("Invalid input, try again\n");
                                    }
                                }
                            }
//
                        }

                    } else { // invalid input
                        System.out.println("Input not valid, try again\n");
                    }
                }
            }
        }
        System.out.println("Exiting");
        data.CloseConnection();
    }
}
