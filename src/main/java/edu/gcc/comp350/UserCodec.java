package edu.gcc.comp350;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;

import java.util.Base64;

public class UserCodec implements Codec<User> {

    @Override
    public void encode(BsonWriter writer, User user, EncoderContext encoderContext) {
        writer.writeStartDocument();
        if (user.getId() != null) {
            writer.writeObjectId("_id", user.getId());
        }
        writer.writeString("name", user.getName());
        writer.writeString("major", user.getMajor());
        writer.writeInt32("year", user.getYear());
        writer.writeString("email", user.getEmail());
        writer.writeInt32("userID", user.getUserID());
        writer.writeString("passwordHashBase64", Base64.getEncoder().encodeToString(user.getPasswordHash()));
        writer.writeString("saltBase64", Base64.getEncoder().encodeToString(user.getSalt()));
        writer.writeEndDocument();
    }

    @Override
    public User decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId("_id");
        String name = reader.readString("name");
        String major = reader.readString("major");
        int year = reader.readInt32("year");
        String email = reader.readString("email");
        int userID = reader.readInt32("userID");
        byte[] passwordHash = Base64.getDecoder().decode(reader.readString("passwordHashBase64"));
        byte[] salt = Base64.getDecoder().decode(reader.readString("saltBase64"));
        reader.readEndDocument();

        User user = new User(name, email, ""); // Password is not set here
        user.setId(id);
        user.setMajor(major);
        user.setYear(year);
        user.setUserID(userID);
        user.setPasswordHash(passwordHash, salt);
        return user;
    }

    @Override
    public Class<User> getEncoderClass() {
        return User.class;
    }
}