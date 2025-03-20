package edu.gcc.comp350;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        run();
    }
    public static void run() {

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
        ArrayList<User> users = new ArrayList<>();
        User tempUser = new User("Temp", "a@a.com", "pw");
        users.add(tempUser);
        // Course used for testing
        double[][] time = {{}, {14, 14.5}, {}, {14, 14.5}, {}, {14, 14.5}, {}};
        Course softwareEngineeringA = new Course("COMP", 350,
                'A', "Software Engineering", 3, "description",
                "Dr. Hutchins", 123456, Course.Days.MWF, 14.0, 14.5,
                time);
        double[][] time1 = {{}, {12, 12.5}, {}, {12, 12.5}, {11, 11.5}, {12, 12.5}, {}};
        Course discrete = new Course("MATH", 214,
                'A', "Discrete Math", 3, "description",
                "Dr. Bancroft", 123456, Course.Days.MWF, 12.0, 12.5,
                time1);
        double[][] time2 = {{}, {}, {9.3, 10.45}, {}, {9.3, 10.45}, {}, {}};
        Course OS = new Course("COMP", 340,
                'B', "Operating Systems", 3, "description",
                "Dr. Zhang", 123456, Course.Days.TR, 9.3, 10.45,
                time2);

        Scanner scanner = new Scanner(System.in);

        User currentUser = null;
        while (true) {
            System.out.print("Enter 'login', 'signup', or 'quit': ");
            String nextInput = scanner.nextLine();

            if (nextInput.equalsIgnoreCase("login")) {
                while (true) {
                    System.out.print("Enter email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();

                    for (User s : users) {
                        if (s.getEmail().equalsIgnoreCase(email)) {
                            if (Arrays.equals(s.getPasswordHash(), s.hash(password))) {
                                currentUser = s;
                                break;
                            }
                        }
                    }
                    if (currentUser == null) {
                        System.out.println("Invalid credentials");
                    } else {
                        break;
                    }
                }
            } else if (nextInput.equalsIgnoreCase("signup")) {
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                System.out.print("Enter email: ");
                String email = scanner.nextLine();
                System.out.print("Enter password: ");
                String password = scanner.nextLine();
                User newUser = new User(name, email, password);
                users.add(newUser);
                currentUser = newUser;
            } else if (nextInput.equalsIgnoreCase("quit")) {
                break;
            } else {
                System.out.println("Invalid input, try again");
                continue;
            }

            String scheduleInput = "";
            while (true) { // loop until user quits
                System.out.println("\nWelcome " + currentUser.getName() + "!");

                // show schedules
                if (currentUser.getSchedules().isEmpty()) {
                    System.out.println("You don't have any schedules.");
                } else {
                    System.out.println("Here are your schedules:");
                    for (Schedule schedule : currentUser.getSchedules()) {
                        System.out.println(schedule.listView());
                    }

                }

                // user opens a schedule
                System.out.print("Enter the schedule id you would like to open, or 'new', or 'delete' (or 'quit'): ");
                scheduleInput = scanner.nextLine();
                Schedule currentSchedule = null; // schedule to open

                // check what user input
                if (scheduleInput.equalsIgnoreCase("new")) { // create schedule
                    System.out.print("Enter a name for the schedule: ");
                    String scheduleName = scanner.nextLine();
                    Schedule mySchedule = new Schedule(currentUser.getUserID(), scheduleName, currentUser.getNumSchedulesCreated());
                    currentUser.addSchedule(mySchedule);

                    mySchedule.addCourse(softwareEngineeringA);
                    mySchedule.addCourse(OS);
                    mySchedule.addCourse(discrete);

                    currentSchedule = mySchedule;
                } else if (scheduleInput.equalsIgnoreCase("quit")) { // sign out
                    break;
                } else if (currentUser.getSchedules().isEmpty()) { // user doesn't have any schedule to open
                    System.out.println("You don't have any existing schedules, try creating one.");
                    continue;
                } else if (scheduleInput.equalsIgnoreCase("delete")) {
                    System.out.print("Enter the ID of the schedule to delete (or 'quit'): ");
                    scheduleInput = scanner.nextLine();
                    if (scheduleInput.equalsIgnoreCase("quit")) {
                        continue;
                    }
                    try { //
                        int scheduleID = Integer.parseInt(scheduleInput);

                        ArrayList<Schedule> schedules = currentUser.getSchedules();
                        boolean deleted = false;
                        for (Schedule schedule : schedules) {
                            if (schedule.getScheduleID() == scheduleID) { // schedule exists
                                currentUser.deleteSchedule(schedule);
                                deleted = true;
                                System.out.println("Schedule " + schedule.getScheduleID() + " successfully deleted");
                                break;
                            }
                        }
                        if (!deleted) { // schedule does not exist
                            System.out.println("No schedule " + scheduleInput);
                        }
                        continue;
                    } catch (NumberFormatException e) { // didn't input a number
                        System.out.println("Input not valid");
                        continue;
                    }
                } else { // user does have schedules
                    try { //
                        int scheduleID = Integer.parseInt(scheduleInput);

                        ArrayList<Schedule> schedules = currentUser.getSchedules();
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
                        break;
                    } else if (nextAction.equalsIgnoreCase("view")) { // view schedule
                        while (true) {
                            System.out.println(currentSchedule.scheduleView());
                            System.out.print("Enter 'e' to add event, 'rc' to remove a course, 're' to remove an event, or 'quit': ");
                            String next = scanner.nextLine();

                            if (next.equalsIgnoreCase("quit")) {
                                break;
                            } else if (next.equalsIgnoreCase("e")) {
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
                                            days[i] = true;
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
                            } else if (next.equalsIgnoreCase("rc")) {
                                // TODO: remove course
                            } else if (next.equalsIgnoreCase("re")) {
                                // TODO: remove event
                            } else {
                                System.out.println("Input not recognized, try again");
                            }
                        }
                    } else if (nextAction.equalsIgnoreCase("search")) { // search
                        System.out.print("Enter your search, or 'quit': ");
                        String search = scanner.nextLine();

                        if (search.equalsIgnoreCase("quit")) {
                            continue;
                        }

                        System.out.println("Searched for " + search);
                        // TODO: Make search object
                        Search s;
                        // TODO: Make a filter object
                        Filter f;
                        while (true) {
                            // TODO: Print search results
                            System.out.println("Search results will appear here");

                            System.out.print("Filters: Enter 'c' for credits, 'dep' for department, 'd' for days, 's' for start time, 'e' for end time, or 'quit': ");
                            String filter = scanner.nextLine();

                            if (filter.equalsIgnoreCase("quit")) {
                                break;
                            } else if (filter.equalsIgnoreCase("c")) {
                                System.out.print("Enter credit filter: ");
                                String credits = scanner.nextLine();
                                // TODO: Make a copy of 'f' and update credits
                            } else if (filter.equalsIgnoreCase("dep")) {
                                System.out.print("Enter department filter: ");
                                String department = scanner.nextLine();
                                // TODO: Make a copy of 'f' and update department
                            } else if (filter.equalsIgnoreCase("d")) {
                                System.out.print("Enter days filter ('MWF' or 'TR'): ");
                                String days = scanner.nextLine();
                                // TODO: Make a copy of 'f' and update days
                            } else if (filter.equalsIgnoreCase("s")) {
                                System.out.print("Enter start time filter: ");
                                String startTime = scanner.nextLine();
                                // TODO: Make a copy of 'f' and update startTime
                            } else if (filter.equalsIgnoreCase("e")) {
                                System.out.print("Enter end time filter: ");
                                String endTime = scanner.nextLine();
                                // TODO: Make a copy of 'f' and update endTime
                            } else {
                                System.out.print("Invalid input, try again");
                            }
                        }

                    } else { // invalid input
                        System.out.println("Input not valid, try again\n");
                    }
                }
            }
        }
        System.out.println("Exiting");
    }
}
