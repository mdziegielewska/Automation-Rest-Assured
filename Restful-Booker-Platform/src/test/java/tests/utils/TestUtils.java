package tests.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.request.BookingRequest;
import models.response.BookingResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static constants.ApiConstants.BOOKING_ENDPOINT;
import static tests.base.BaseTest.givenRequest;
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
     */
    public static <T> T loadRequestFromFile(String filePath, Class<T> type) {
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
     * @param baseJsonPath The path to the base JSON file (e.g., ApiConstants.CORRECT_BOOKING_PATH).
     * @param roomId The room ID to set for the booking (e.g., "1").
     * @return A {@link BookingRequest} object ready for use in API calls.
     */
    public static BookingRequest buildBookingRequest(String baseJsonPath, String roomId) {
        BookingRequest bookingRequest = loadRequest(baseJsonPath, BookingRequest.class);

        bookingRequest.setRoomid(roomId);
        bookingRequest.setBookingdates(generateRandomBookingDates());
        return bookingRequest;
    }

    /**
     * Generates a random positive long number with a specified number of digits.
     * @param length The desired number of digits for the long. Must be between 1 and 18 (inclusive, for a long).
     * @return A positive long number with the specified number of digits.
     * @throws IllegalArgumentException if length is not within the valid range for a long (1 to 18).
     */
    public static long generateLongWithDigits(int length) {
        if (length <= 0 || length > 18) { // Max 18 digits to comfortably fit in a long
            throw new IllegalArgumentException("Length must be between 1 and 18 for a long number.");
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        sb.append(random.nextInt(9) + 1);

        for (int i = 1; i < length; i++) {
            sb.append(random.nextInt(10));
        }

        return Long.parseLong(sb.toString());
    }

    /**
     * Generates a 10-character string containing only digits (0-9).
     * @return A 10-character string composed of random digits.
     */
    public static String generate10DigitNumericString() {
        return String.valueOf(generateLongWithDigits(10));
    }

    /**
     * Retrieves the first {@link BookingResponse} object from the list returned by the GET /booking endpoint.
     * @param authToken The authentication token to include in the request's Cookie header.
     * @param roomId The room ID to query for, filtering the list of bookings returned.
     * @return The first {@link BookingResponse} object found for the specified room ID
     * if no bookings match the criteria or the response is empty/malformed.
     */
    public static BookingResponse getFirstBookingId(String authToken, Integer roomId) {
        List<BookingResponse> bookings = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .queryParam("roomid", roomId)
                .when()
                .get(BOOKING_ENDPOINT)
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("bookings", BookingResponse.class);

        if (bookings != null && !bookings.isEmpty()) {
            return bookings.getFirst();
        } else if (roomId != 15) {
             return getFirstBookingId(authToken, roomId+1);
        }

        return null;
    }

    /**
     * Creates a shallow copy of the given BookingResponse object.
     * @param original the original BookingResponse to clone
     * @return a new BookingResponse object with the same field values
     */
    public static BookingResponse cloneBooking(BookingResponse original) {
        BookingResponse copy = new BookingResponse();
        copy.setBookingid(original.getBookingid());
        copy.setRoomid(original.getRoomid());
        copy.setFirstname(original.getFirstname());
        copy.setLastname(original.getLastname());
        copy.setDepositpaid(original.getDepositpaid());
        copy.setBookingdates(original.getBookingdates());

        return copy;
    }

    /**
     * Converts the first character of the given string to lowercase.
     * @param s the input string to transform
     * @return the transformed string with the first letter in lowercase
     */
    public static String decapitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}