package edu.gcc.comp350;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;

public class User {
    private String name;
    private String major;
    private int year;
    private String email;
    private int userID;
    private byte[] passwordHash;
    private byte[] salt;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
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
    }

    public byte[] hash(String password) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), this.salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}