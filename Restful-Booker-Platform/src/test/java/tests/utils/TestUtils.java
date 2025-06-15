package tests.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.LoginRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.stream.Collectors;

/**
 * Utility class for common test operations, such as reading data from resource files
 * and deserializing JSON content into Java objects.
 */
public class TestUtils {

    /**
     * Reusable ObjectMapper instance for efficient JSON processing.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Reads the entire content of a specified resource file into a single String.
     * @param filePath The path to the resource file (e.g., "testData/myFile.json").
     * @return The content of the file as a String.
     * @throws IOException If the file cannot be found or read.
     */
    public static String readResourceFile(String filePath) throws IOException {
        try (InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Resource file not found on classpath: " + filePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    /**
     * Loads a {@link LoginRequest} object by reading JSON data from a specified resource file
     * and deserializing it.
     * @param filePath The path to the JSON resource file (e.g., "testData/authorization/correctData.json").
     * @return A {@link LoginRequest} object populated with data from the JSON file.
     * @throws IOException If the file cannot be found, read, or if JSON parsing fails.
     */
    public static LoginRequest loadLoginRequestFromFile(String filePath) throws IOException {
        try (InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Resource file not found on classpath: " + filePath);
            }
            return OBJECT_MAPPER.readValue(inputStream, LoginRequest.class);
        }
    }
}