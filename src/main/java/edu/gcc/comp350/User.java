package edu.gcc.comp350;

import org.bson.BsonObjectId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class User {
    private ObjectId _id; // MongoDB ObjectId
    private String name;
    private String major;
    private int year;
    private String email;
    private int userID;
    @BsonIgnore
    private byte[] passwordHash;
    @BsonIgnore
    private byte[] salt;
    private String passwordHashBase64; // Stored as Base64-encoded String
    private String saltBase64; // Stored as Base64-encoded String

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.major = "";
        this.year = 0;
        this.userID = 0;
        setPasswordHash(password);
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String password) {
        SecureRandom random = new SecureRandom();
        this.salt = new byte[16];
        random.nextBytes(salt);
        this.passwordHash = hash(password);

        this.saltBase64 = Base64.getEncoder().encodeToString(salt);
        this.passwordHashBase64 = Base64.getEncoder().encodeToString(passwordHash);
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] hash(String password) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPasswordHash(byte[] passwordHash, byte[] salt) {
        this.passwordHash = passwordHash;
        this.salt = salt;

        this.passwordHashBase64 = Base64.getEncoder().encodeToString(passwordHash);
        this.saltBase64 = Base64.getEncoder().encodeToString(salt);
    }

    public ObjectId getId() {
        return _id;
    }

    public void setId(ObjectId id) {
        this._id = id;
    }
}