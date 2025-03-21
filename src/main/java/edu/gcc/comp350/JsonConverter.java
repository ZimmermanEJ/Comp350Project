package edu.gcc.comp350;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class JsonConverter {
    public static void main(String[] args) {

        List<OriginalCourse> originalCourses;
        List<Course> courses = new ArrayList<>();
        Gson gson = new Gson();
        try {
            FileReader courseReader = new FileReader("data_wolfe.json");
            originalCourses = gson.fromJson(courseReader, new TypeToken<List<OriginalCourse>>() {}.getType());
            courseReader.close();




            for (OriginalCourse oc: originalCourses){
                StringBuilder build = new StringBuilder();
                for (int i = 0; i < oc.faculty.length; i++){
                    build.append(oc.faculty[i]);
                    if (i<oc.faculty.length-1){
                        build.append(", ");
                    }
                }
                double startTime = 0;
                double endTime = 0;
                double[][] timeSlots = new double[5][2];
                int slotoffset = 0;
                Course.Days days = Course.Days.MWF;

                for (int i = 0; i<oc.times.length; i++){
                    switch (oc.times[i].day){
                        case 'M':
                            slotoffset = 0;
                            break;
                        case 'T':
                            slotoffset = 1;
                            days = Course.Days.TR;
                            break;
                        case 'W':
                            slotoffset = 2;
                            break;
                        case 'R':
                            slotoffset = 3;
                            break;
                        case 'F':
                            slotoffset = 4;
                            break;
                    }
                    if (oc.times[i].startTime!=null) {
                        String[] times = oc.times[i].startTime.split(":");
                        timeSlots[slotoffset][0] = (double) Integer.parseInt(times[0]) + 0.01 * Integer.parseInt(times[1]);

                        times = oc.times[i].endTime.split(":");
                        timeSlots[slotoffset][1] = (double) Integer.parseInt(times[0]) + 0.01 * Integer.parseInt(times[1]);

                        if (startTime == 0) {
                            startTime = timeSlots[slotoffset][0];
                            endTime = timeSlots[slotoffset][1];
                        }
                    }

                }



                Course nc = new Course(oc.subject, oc.number, oc.section, oc.name, oc.credits, "TBD", build.toString(), 0, days, startTime, endTime, timeSlots);
                courses.add(nc);
            }
            FileWriter writer = new FileWriter("courses.json");
            writer.write(gson.toJson(courses, new TypeToken<ArrayList<Course>>() {}.getType()));
            writer.close();



        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
