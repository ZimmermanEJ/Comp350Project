package edu.gcc.comp350;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCodec implements Codec<Schedule> {

    @Override
    public void encode(BsonWriter writer, Schedule schedule, EncoderContext encoderContext) {
        writer.writeStartDocument();
        if (schedule.getId() != null) {
            writer.writeObjectId("_id", schedule.getId());
        }
        writer.writeInt32("userID", schedule.getUserID());
        writer.writeString("scheduleName", schedule.getScheduleName());
        writer.writeInt32("scheduleID", schedule.getScheduleID());

        writer.writeStartArray("courses");
        for (Integer course : schedule.getCourses()) {
            writer.writeInt32(course);
        }
        writer.writeEndArray();

        writer.writeStartArray("events");
        for (Integer event : schedule.getEvents()) {
            writer.writeInt32(event);
        }
        writer.writeEndArray();

        writer.writeEndDocument();
    }

    @Override
    public Schedule decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId("_id");
        int userID = reader.readInt32("userID");
        String name = reader.readString("scheduleName");
        Schedule schedule = new Schedule(userID, name);
        schedule.setId(id);

        if (reader.readBsonType() != null && reader.readName().equals("scheduleID")) {
            schedule.setScheduleID(reader.readInt32());
        }

        reader.readName("courses");
        reader.readStartArray();
        List<Integer> courses = new ArrayList<>();
        while (reader.readBsonType() == BsonType.INT32) {
            courses.add(reader.readInt32());
        }
        reader.readEndArray();

        reader.readName("events");
        reader.readStartArray();
        List<Integer> events = new ArrayList<>();
        while (reader.readBsonType() == BsonType.INT32) {
            events.add(reader.readInt32());
        }
        reader.readEndArray();

        reader.readEndDocument();

        for (Integer course : courses) {
            schedule.addCourse(Main.data.GetCourseByRef(course));
        }
        // add events here

        return schedule;
    }

    @Override
    public Class<Schedule> getEncoderClass() {
        return Schedule.class;
    }
}