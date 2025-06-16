package constants;

/**
 * Centralized constants for API endpoints, JSON paths, and common error messages.
 */
public final class ApiConstants { // Using 'final' to prevent inheritance

    // --- API Endpoints ---
    public static final String AUTH_LOGIN_ENDPOINT = "/auth/login";
    public static final String AUTH_VALIDATE_ENDPOINT = "/auth/validate";

    // --- Common JSON Paths ---
    public static final String TOKEN_JSON_PATH = "token";
    public static final String ERROR_JSON_PATH = "error";
    public static final String VALID_JSON_PATH = "valid";

    // --- Common Error Messages ---
    public static final String INVALID_CREDENTIALS_ERROR_MESSAGE = "Invalid credentials";
    public static final String INVALID_TOKEN_ERROR_MESSAGE = "Invalid token";
    public static final String NO_TOKEN_PROVIDED_ERROR_MESSAGE = "No token provided";

    // --- Test Data Paths ---
    public static final String CORRECT_LOGIN_PATH = "testData/authorization/correctData.json";
    public static final String INCORRECT_LOGIN_PATH = "testData/authorization/incorrectData.json";

    private ApiConstants() {
        // hidden constructor
    }
}