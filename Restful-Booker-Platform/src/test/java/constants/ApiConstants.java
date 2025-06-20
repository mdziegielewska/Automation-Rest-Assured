package constants;

/**
 * Centralized constants for API endpoints, JSON paths, and common error messages.
 */
public final class ApiConstants {

    private ApiConstants() {
        // hidden constructor
    }

    // --- API Endpoints ---
    public static final String AUTH_LOGIN_ENDPOINT = "/auth/login";
    public static final String AUTH_VALIDATE_ENDPOINT = "/auth/validate";
    public static final String BOOKING_ENDPOINT = "/booking";

    // --- Common JSON Paths ---
    public static final String TOKEN_JSON_PATH = "token";
    public static final String ERROR_JSON_PATH = "error";
    public static final String VALID_JSON_PATH = "valid";
    public static final String ERRORS_JSON_PATH = "errors";
    public static final String SUCCESS_JSON_PATH = "success";

    // --- Authorization Error Messages ---
    public static final String INVALID_CREDENTIALS_ERROR_MESSAGE = "Invalid credentials";
    public static final String INVALID_TOKEN_ERROR_MESSAGE = "Invalid token";
    public static final String NO_TOKEN_PROVIDED_ERROR_MESSAGE = "No token provided";
    // --- Booking Error Messages ---
    public static final String ROOM_ID_REQUIRED_ERROR_MESSAGE = "Room ID is required";
    public static final String BOOKING_CREATION_GENERIC_FAILURE_ERROR_MESSAGE = "Failed to create booking";
    public static final String BOOKING_UPDATE_GENERIC_FAILURE_ERROR_MESSAGE = "Failed to update booking";
    public static final String LAST_NAME_BLANK_ERROR_MESSAGE = "Lastname should not be blank";
    public static final String FIELD_MUST_NOT_BE_NULL_ERROR_MESSAGE = "must not be null";
    public static final String FIRST_NAME_SIZE_ERROR_MESSAGE = "size must be between 3 and 18";
    public static final String ROOM_ID_MIN_VALUE_ERROR_MESSAGE = "must be greater than or equal to 1";
    public static final String FIELD_MUST_NOT_BE_EMPTY_ERROR_MESSAGE =  "must not be empty";
    public static final String AUTHENTICATION_REQUIRED_ERROR_MESSAGE = "Authentication required";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
    public static final String BOOKING_DELETION_GENERIC_FAILURE_ERROR_MESSAGE = "Failed to delete booking";

    // --- Test Data Paths ---
    public static final String CORRECT_LOGIN_PATH = "testData/authorization/correctData.json";
    public static final String INCORRECT_LOGIN_PATH = "testData/authorization/incorrectData.json";
    public static final String CORRECT_BOOKING_PATH = "testData/booking/correctData.json";
}