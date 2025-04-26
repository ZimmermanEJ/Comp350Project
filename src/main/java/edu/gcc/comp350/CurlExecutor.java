package edu.gcc.comp350;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class CurlExecutor {
    private String major;
    private String year;

    // Constructor to initialize major and year
    public CurlExecutor(String major, String year) {
        this.major = major;
        this.year = year;
    }

    // Method to execute the cURL command
    public String runCurlCommand() throws UnsupportedEncodingException {
        String encodedMajor = URLEncoder.encode(major, "UTF-8");
        String encodedYear = URLEncoder.encode(year, "UTF-8");

        String url = "http://127.0.0.1:4200/api/run-script?major=" + encodedMajor + "&year=" + encodedYear;
        String filePath = "C:\\GCC\\COMP 350\\schedule_generator.py";

        // Using -F to send the file as multipart/form-data
        ProcessBuilder processBuilder = new ProcessBuilder(
                "curl", "-s", "-X", "POST",
                "-F", "file=@" + filePath,
                url
        );

        processBuilder.redirectErrorStream(true);

        StringBuilder output = new StringBuilder();

        try {
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Process exited with code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}