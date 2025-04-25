package edu.gcc.comp350;

import org.bson.AbstractBsonReader;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class CourseCodec implements Codec<Course> {

    @Override
    public void encode(BsonWriter writer, Course course, EncoderContext encoderContext) {
        writer.writeStartDocument();
        if (course.getId() != null) {
            writer.writeObjectId("_id", course.getId());
        }
        writer.writeString("department", course.getDepartment());
        writer.writeInt32("courseNumber", course.getCourseNumber());
        writer.writeString("sectionCode", String.valueOf(course.getSectionCode()));
        writer.writeString("title", course.getTitle());
        writer.writeInt32("credits", course.getCredits());
        writer.writeString("description", course.getDescription());
        writer.writeString("professor", course.getProfessor());
        writer.writeInt32("referenceNumber", course.getReferenceNumber());
        writer.writeString("days", course.getDays().name());
        // Encode daysArray
        writer.writeStartArray("daysArray");
        for (Boolean day : course.getDaysArray()) {
            writer.writeBoolean(day);
        }
        writer.writeEndArray();
        writer.writeDouble("startTime", course.getStartTime());
        writer.writeDouble("endTime", course.getEndTime());
        // Encode timeSlot
        writer.writeStartArray("timeSlot");
        for (ArrayList<Double> slot : course.getTimeSlotList()) {
            writer.writeStartArray();
            for (Double time : slot) {
                writer.writeDouble(time);
            }
            writer.writeEndArray();
        }
        writer.writeEndArray();
        writer.writeEndDocument();
    }

    @Override
    public Course decode(BsonReader reader, DecoderContext decoderContext) {
        AbstractBsonReader abstractReader;
        if (reader instanceof AbstractBsonReader){
            abstractReader = (AbstractBsonReader) reader;




            abstractReader.readStartDocument();
            ObjectId id = abstractReader.readObjectId("_id");
            String department = abstractReader.readString("department");
            int courseNumber = abstractReader.readInt32("courseNumber");
            char sectionCode = abstractReader.readString("sectionCode").charAt(0);
            String title = abstractReader.readString("title");
            int credits = abstractReader.readInt32("credits");
            String description = abstractReader.readString("description");
            String professor = abstractReader.readString("professor");
            int referenceNumber = abstractReader.readInt32("referenceNumber");
            Course.Days days = Course.Days.valueOf(abstractReader.readString("days"));

            // Decode daysArray
            ArrayList<Boolean> daysArray = new ArrayList<>();
            abstractReader.readName("daysArray");
            abstractReader.readStartArray();
            while (abstractReader.readBsonType() == BsonType.BOOLEAN) {
                daysArray.add(abstractReader.readBoolean());
            }
            abstractReader.readEndArray();

            double startTime;
            double endTime;

            if (abstractReader.readBsonType() == BsonType.DOUBLE) {
                startTime = abstractReader.readDouble();
            } else {
                startTime = (double) abstractReader.readInt32();
            }
            if (abstractReader.readBsonType() == BsonType.DOUBLE) {
                endTime = abstractReader.readDouble();
            } else {
                endTime = (double) abstractReader.readInt32();
            }

            // Decode timeSlot
            ArrayList<ArrayList<Double>> timeSlot = new ArrayList<>();
            abstractReader.readName("timeSlot");

            abstractReader.readStartArray();
            while (abstractReader.readBsonType() != null) {
                ArrayList<Double> slot = new ArrayList<>();
                if (abstractReader.getState()== AbstractBsonReader.State.END_OF_ARRAY){
                    abstractReader.readEndArray();
                    break;
                }
                abstractReader.readStartArray();
                BsonType lastRead;
                while ((lastRead = abstractReader.readBsonType()) == BsonType.DOUBLE || lastRead == BsonType.INT32) {
                    if (lastRead==BsonType.DOUBLE) slot.add(abstractReader.readDouble());
                    else if (lastRead==BsonType.INT32){slot.add((double) abstractReader.readInt32());}
                }
                abstractReader.readEndArray();
                timeSlot.add(slot);
            }
            if (abstractReader.getState()== AbstractBsonReader.State.END_OF_ARRAY){
                abstractReader.readEndArray();
            }


            abstractReader.readEndDocument();

            Course course = new Course(department, courseNumber, sectionCode, title, credits, description, professor, referenceNumber, days, daysArray, startTime, endTime, timeSlot);
            course.setId(id);
            return course;
        }
        return null;
    }

    @Override
    public Class<Course> getEncoderClass() {
        return Course.class;
    }
}