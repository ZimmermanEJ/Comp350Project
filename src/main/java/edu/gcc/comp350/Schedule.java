package edu.gcc.comp350;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Schedule {

    private int userID;
    private int scheduleID;
    private String name;
    private Map<Course, String> courses; // course to color
    private Map<Event, String> events; // event to color

    public Schedule(int userID, String name, int scheduleID) {
        this.userID = userID;
        this.scheduleID = scheduleID;
        this.name = name;
        this.courses = new HashMap<Course, String>();
        this.events = new HashMap<Event, String>();
    }

    public boolean addCourse(Course course) {
        // TODO: check for conflicts
        courses.put(course, "Red");
        return true;
    }

    public void removeCourse(Course course) { }

    public void addEvent(Event event) {
        events.put(event, "Blue");
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    public boolean hasConflict(Course course) {
        return false;
    }

    public int getTotalCredits() {
        int total = 0;
        for (Course course : this.getCourses().keySet()){
            total += course.getCredits();
        }
        return total;
    }

    public int getUserID() {
        return userID;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Course, String> getCourses() {
        return courses;
    }

    public Map<Event, String> getEvents() {
        return events;
    }

    public String scheduleView() {
        StringBuilder toReturn = new StringBuilder(this.getName() + " - " + this.getTotalCredits() + " credits\n");
        String[] dayList = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (int i = 0; i < 7; i++) {
            toReturn.append(dayList[i]).append(":\n");
            for (int hour = 8; hour <= 21; hour++) {
                toReturn.append(((hour-1) % 12) + 1).append(hour <= 11 ? "AM" :"PM").append(":\t");
                for (Course course : this.getCourses().keySet()) {
                    double[][] timeslot = course.getTimeSlot();
                    double[] day = timeslot[i];
                    if (day.length == 2 && hour >= day[0] && hour < day[1]) {
                        if (i == 1 || i == 3 || i == 5) {
                            toReturn.append(course.getTitle()).append("\t");
                            toReturn.append("ID: ").append(course.getCourseID()).append("\t");
                            toReturn.append(String.format("%.2f", ((day[0]-1) % 12) + 1)).append(" - ").append(String.format("%.2f", ((day[1]-1) % 12) + 1));
                        } else if (i == 2 || i == 4) {
                            toReturn.append(course.getTitle()).append("\t");
                            toReturn.append("ID: ").append(course.getCourseID()).append("\t");
                            toReturn.append(String.format("%.2f", ((day[0]-1) % 12) + 1)).append(" - ").append(String.format("%.2f", ((day[1]-1) % 12) + 1));
                        }
                    }
                }
                toReturn.append("\n");
            }
            toReturn.append("\n");
        }
        // Print out the events at the bottom
        if (!this.getEvents().isEmpty()) {
            toReturn.append("Events:\n");
            for (Event event : this.getEvents().keySet()) {
                toReturn.append(event.toString()).append("\n");
            }
        } else {
            toReturn.append("No events scheduled\n");
        }


        return toReturn.toString();
    }

    public String listView() {
        StringBuilder toReturn = new StringBuilder("ID: ");

        toReturn.append(this.getScheduleID()).append("\t");
        toReturn.append("Name: ").append(this.getName());
        toReturn.append(" - ").append(this.getTotalCredits()).append(" credits");

        return toReturn.toString();
    }
}
