package edu.gcc.comp350;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        run();
    }
    public static void run() {
        double[][] time = {{}, {2, 2.5}, {}, {2, 2.5}, {}, {2, 2.5}, {}};
        Course softwareEngineeringA = new Course("COMP", 350,
                'A', "Software Engineering", 3, "description",
                "Dr. Hutchins", 123456, Course.Days.MWF, 2.0, 2.5,
                time);

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User currentUser = new User("Temp", email, password);

        String scheduleInput = "";
        while (true) { // loop until user quits
            System.out.println("\nWelcome " + currentUser.getName() + "!");

            // show schedules
            if (currentUser.getSchedules().isEmpty()) {
                System.out.println("You don't have any schedules.");
            } else {
                System.out.println("Here are your schedules:");
                for (Schedule schedule : currentUser.getSchedules()){
                    System.out.println(schedule.listView());
                }

            }

            // user opens a schedule
            System.out.print("Enter the schedule id you would like to open, or 'new' for new schedule ('quit' to quit): ");
            scheduleInput = scanner.nextLine();
            Schedule currentSchedule = null; // schedule to open

            // check what user input
            if (scheduleInput.equalsIgnoreCase("new")) { // create schedule
                System.out.print("Enter a name for the schedule: ");
                String scheduleName = scanner.nextLine();
                Schedule mySchedule = new Schedule(currentUser.getUserID(), scheduleName);
                currentUser.addSchedule(mySchedule);

                mySchedule.addCourse(softwareEngineeringA);

                currentSchedule = mySchedule;
            } else if (scheduleInput.equalsIgnoreCase("quit")) { // sign out
                break;
            } else if (currentUser.getSchedules().isEmpty()) { // user doesn't have any schedule to open
                System.out.println("You don't have any existing schedules, try creating one.");
                continue;
            } else { // user does have schedules
                try { //
                    int scheduleID = Integer.parseInt(scheduleInput);

                    ArrayList<Schedule> schedules = currentUser.getSchedules();
                    boolean assigned = false;
                    for (Schedule schedule : schedules){
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

                if (nextAction.equalsIgnoreCase("quit")){
                    break;
                } else if (nextAction.equalsIgnoreCase("view")) { // view schedule
                    System.out.println(currentSchedule.scheduleView());
                } else if (nextAction.equalsIgnoreCase("search")) { // search
                    System.out.println("Search to be implemented");
                } else { // invalid input
                    System.out.println("Input not valid, try again\n");
                }
            }
        }

        System.out.println("Exiting");
    }
}
