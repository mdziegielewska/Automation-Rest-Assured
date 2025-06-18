package tests.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.request.BookingRequest;
import models.request.LoginRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.stream.Collectors;

import static tests.utils.DateUtils.generateRandomBookingDates;

/**
 * Utility class for common test operations, such as reading data from resource files
 * and deserializing JSON content into Java objects.
 */
public class TestUtils {

    private TestUtils() {
        // hidden constructor
    }

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
                throw new IOException(String.format("Resource file not found on classpath: %s", filePath));
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    /**
     * Loads and deserializes a JSON object of a specified type from a file located in the classpath.
     * @param <T> The type of the object to be loaded and returned.
     * @param filePath The path to the JSON file relative to the classpath (e.g., "testData/auth/login.json").
     * @param type The Class object representing the target type {@code <T>} for deserialization.
     * @return An object of type T, populated with data from the JSON file.
     * @throws RuntimeException if the file cannot be found, read, or deserialized.
     */
    public static <T> T loadRequestFromFile(String filePath, Class<T> type) throws IOException {
        try (InputStream inputStream = TestUtils.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException(String.format("Resource file not found on classpath: %s", filePath));
            }
            return OBJECT_MAPPER.readValue(inputStream, type);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to load or parse JSON for type %s from: %s",
                    type.getSimpleName(), filePath), e);
        }
    }

    /**
     * Loads LoginRequest object from a JSON file.
     * @param <T> The type of the object to be loaded and returned.
     * @param resourcePath The path to the JSON file containing data.
     * @param type The Class object representing the type {@code <T>} to which the JSON data should be mapped.
     * @return An object of type T, populated with data from the file.
     * @throws RuntimeException if loading the file fails.
     */
    public static <T> T loadRequest(String resourcePath, Class<T> type) {
        try {
            return loadRequestFromFile(resourcePath, type);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed to load test data for type %s from: %s",
                    type.getSimpleName(), resourcePath), e);
        }
    }

    /**
     * Builds a complete {@link BookingRequest} object for testing purposes.
     * It loads a base request from a JSON file and then overrides dynamic fields
     * like room ID and booking dates.
     * @param baseJsonPath The path to the base JSON file (e.g., ApiConstants.CORRECT_BOOKING_PATH).
     * @param roomId The room ID to set for the booking (e.g., "1").
     * @return A {@link BookingRequest} object ready for use in API calls.
     */
    public static BookingRequest buildBookingRequest(String baseJsonPath, String roomId) {
        BookingRequest bookingRequest = loadRequest(baseJsonPath, BookingRequest.class);

        // Override dynamic parts: set predefined room ID and dynamically generated booking dates
        bookingRequest.setRoomid(roomId);
        bookingRequest.setBookingdates(generateRandomBookingDates());
        return bookingRequest;
    }
}