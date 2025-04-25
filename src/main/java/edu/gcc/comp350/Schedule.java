package edu.gcc.comp350;
import org.bson.types.ObjectId;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Schedule {

    private ObjectId _id;
    private int userID;
    private String scheduleName;
    private int scheduleID;
    private ArrayList<Integer> courses;
    private ArrayList<Integer> events;

    public Schedule(int userID, String name) {
        this.userID = userID;
        this.scheduleName = name;
        this.courses = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public String addCourse(Course course) {
        if(hasConflict(course) != null) {
            return hasConflict(course);
        }else {
            courses.add(course.getReferenceNumber());
            return null;
        }
    }

    public boolean removeCourse(int refNum) {
        for (int c: courses) {
            if (c == refNum) {
                courses.remove(Integer.valueOf(c));
                return true;
            }
        }
        return false;
    }

    public void addEvent(Event event) {
        events.add(event.getEventID());
    }

    public boolean removeEvent(int eventID) {
        for (int e: events) {
            if (e == eventID) {
                events.remove(e);
                return true;
            }
        }
        return false;
    }

   /**
     * Checks if the given course has a time conflict with any existing courses in the schedule.
     *
     * @param course the course to check for conflicts
     * @return name of conflict if there is a conflict, null otherwise
     */
   public String hasConflict(Course course) {
       for (Integer courseRef : this.getCourses()) {
           Course c = Main.data.GetCourseByRef(courseRef);
            if (c.hasConflict(course)) {
                return c.getTitle();
            }
        }
        return null;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getTotalCredits() {
        int total = 0;
        for (Integer courseRef : this.getCourses()){
            Course course = Main.data.GetCourseByRef(courseRef);
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

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public ArrayList<Integer> getCourses() {
        return courses;
    }

    public ArrayList<Integer> getEvents() {
        return events;
    }

    public String scheduleView() {
        StringBuilder toReturn = new StringBuilder(this.getScheduleName() + " - " + this.getTotalCredits() + " credits\n");
        String[] dayList = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

//        for (int i = 1; i < 6; i++) { // monday through friday
//            toReturn.append(dayList[i]).append(":\n");
//            for (int hour = 8; hour <= 21; hour++) {
//                toReturn.append(((hour-1) % 12) + 1).append(hour <= 11 ? "AM" :"PM").append(":\t");
//                for (Course course : this.getCourses().keySet()) {
//                    double[][] timeslot = course.getTimeSlot();
//                    double[] day = timeslot[i];
//                    if (day.length == 2 && hour >= day[0] && hour < day[1]) {
//                        if (i == 1 || i == 3 || i == 5) {
//                            toReturn.append(course.getTitle()).append("\t");
//                            toReturn.append("# ").append(course.getReferenceNumber()).append("\t");
//                            toReturn.append(String.format("%.2f", ((day[0]-1) % 12) + 1)).append(" - ").append(String.format("%.2f", ((day[1]-1) % 12) + 1));
//                        } else if (i == 2 || i == 4) {
//                            toReturn.append(course.getTitle()).append("\t");
//                            toReturn.append("# ").append(course.getReferenceNumber()).append("\t");
//                            toReturn.append(String.format("%.2f", ((day[0]-1) % 12) + 1)).append(" - ").append(String.format("%.2f", ((day[1]-1) % 12) + 1));
//                        }
//                    }
//                }
//                toReturn.append("\n");
//            }
//            toReturn.append("\n");
//        }
        // print out monday through friday row
        toReturn.append("\t\t\t");
        toReturn.append(String.format("%-48s", "Monday")).append("|\t\t");
        toReturn.append(String.format("%-48s", "Tuesday")).append("|\t\t");
        toReturn.append(String.format("%-48s", "Wednesday")).append("|\t\t");
        toReturn.append(String.format("%-48s", "Thursday")).append("|\t\t");
        toReturn.append(String.format("%-48s", "Friday")).append("\n");
        for (int hour = 8; hour <= 21; hour++) {
            toReturn.append(String.format("%-5s", ((hour-1) % 12) + 1 + (hour <= 11 ? "AM" :"PM"))).append("|\t");
            for (int i = 1; i < 6; i++) { // monday through friday
                boolean courseAdded = false;
                for (Integer courseRef : this.getCourses()) {
                    Course course = Main.data.GetCourseByRef(courseRef);
                    double[][] timeslot = course.getTimeSlot();
                    double[] day = timeslot[i];
                    if (day.length == 2 && hour >= day[0] && hour < day[1]) {
                        toReturn.append(String.format("%-48s", course.getTitle() + " #" + course.getReferenceNumber() + " " +
                                String.format("%.2f", ((day[0]-1) % 12) + 1) + " - " + String.format("%.2f", ((day[1]-1) % 12) + 1)));
                        courseAdded = true;
                        break;
                    }
                }
                if (!courseAdded) {
                    toReturn.append(String.format("%-48s", ""));
                }
                toReturn.append("\t|\t");
            }
            toReturn.append("\n");
        }
        toReturn.append("\n");

        // Print out the events at the bottom
        if (!this.getEvents().isEmpty()) {
            toReturn.append("Events:\n");
            for (Integer eventID : this.getEvents()) {
                // Event event = LocalDataStorage.GetEventByID(eventID);
                // toReturn.append(event.toString()).append("\n");
            }
        } else {
            toReturn.append("No events scheduled\n");
        }


        return toReturn.toString();
    }

    public String listView() {
        StringBuilder toReturn = new StringBuilder("ID: ");

        toReturn.append(this.getScheduleID()).append("\t");
        toReturn.append("Name: ").append(this.getScheduleName());
        toReturn.append(" - ").append(this.getTotalCredits()).append(" credits");

        return toReturn.toString();
    }

    public void exportSchedule(String filename) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(filename + ".ics");
        pw.println("BEGIN:VCALENDAR");
        pw.println("VERSION:2.0");
        pw.println("PRODID:-//Grove City College//" + scheduleName + "//EN");

        for (Integer courseRef : this.getCourses()) {
            Course course = Main.data.GetCourseByRef(courseRef);
            double[][] timeslot = course.getTimeSlot();
            for (int i = 1; i < 6; i++) { // Monday to Friday
                double[] day = timeslot[i];
                if (day[1] - day[0] > 0) {
                    pw.println("BEGIN:VEVENT");
                    pw.println("SUMMARY:" + course.getTitle());
                    pw.println("DTSTART;TZID=America/New_York:" + formatTime(i, day[0]));
                    pw.println("DTEND;TZID=America/New_York:" + formatTime(i, day[1]));
                    pw.println("RRULE:FREQ=WEEKLY;WKST=SU;UNTIL=20251210T035959Z");
                    pw.println("UID:" + course.getReferenceNumber() + i + "@gcc.edu");
                    pw.println("END:VEVENT");
                }
            }
        }
        pw.println("END:VCALENDAR");
        pw.close();
    }

    private String formatTime(int dayOfWeek, double time) {
        int hour = (int) time;
        int minute = (int) ((time - hour) * 100);
        return String.format("2025080%dT%02d%02d00", dayOfWeek + 24, hour, minute);
    }

    private String getDayOfWeek(int day) {
        switch (day) {
            case 1: return "MO";
            case 2: return "TU";
            case 3: return "WE";
            case 4: return "TH";
            case 5: return "FR";
            default: return "";
        }
    }


    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }

    public String getName() {
       return scheduleName;
    }
}
