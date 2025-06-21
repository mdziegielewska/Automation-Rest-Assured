package tests.utils.assertions;

import io.restassured.response.ValidatableResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static constants.ApiConstants.ERROR_JSON_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tests.utils.TestUtils.decapitalize;


/**
 * Utility class containing common assertion methods for API responses using RestAssured and Hamcrest.
 */
public class CommonAssertions {

    private CommonAssertions() {
        // Hidden constructor
    }

    /**
     * Asserts that the response indicates a successful operation with a 200 OK status code.
     * @param response The RestAssured ValidatableResponse object.
     */
    public static void assertSuccessfulResponse(ValidatableResponse response) {
        response.statusCode(200)
                .body(equalTo("[]"));
    }

    /**
     * Asserts that the response indicates a failed operation with a specific status code
     * and that the response body contains a specific error message at the defined error JSON path.
     * @param response The RestAssured ValidatableResponse object.
     * @param statusCode The expected HTTP status code for the failure (e.g., 400, 401).
     * @param expectedErrorMessage The expected error message string present in the response body.
     */
    public static void assertFailedResponse(ValidatableResponse response, Integer statusCode, String expectedErrorMessage) {
        response.statusCode(statusCode)
                .body(ERROR_JSON_PATH, equalTo(expectedErrorMessage));
    }

    /**
     * Asserts that the response status code is 500 (Internal Server Error).
     * @param response The RestAssured ValidatableResponse object.
     */
    public static void assertInternalServerError(ValidatableResponse response) {
        response.statusCode(500)
                .body(equalTo("[]"));
    }
    /**
     * Asserts that the response status code is 500 (Internal Server Error)
     * and checks the error message in the response body.
     * @param response The RestAssured ValidatableResponse object.
     * @param expectedErrorMessage The expected error message in the body.
     */
    public static void assertInternalServerError(ValidatableResponse response, String expectedErrorMessage) {
        response.statusCode(500)
                .body(ERROR_JSON_PATH, is(expectedErrorMessage));
    }

    /**
     * Asserts that a boolean value at a specified JSON path in the response body matches the expected boolean
     * @param response The RestAssured ValidatableResponse object.
     * @param jsonPath The JSON path to the boolean field.
     * @param expectedBooleanValue The boolean value expected at the given JSON path (true or false).
     */
    public static void assertJsonBooleanValue(ValidatableResponse response, String jsonPath, boolean expectedBooleanValue) {
        response.statusCode(200)
                .body(jsonPath, is(expectedBooleanValue));
    }

    /**
     * Asserts that a list of messages is not null, is a list, and contains a specific expected message.
     * @param list The list of messages.
     * @param expectedMessage The specific message expected to be in the list.
     */
    public static void assertListContains(List<String> list, String expectedMessage) {
        assertIsNotEmpty(list, "List of messages");
        assertThat("List should contain the expected element", list, hasItem(expectedMessage));
    }

    /**
     * Asserts that the given string matches the provided regular expression pattern.
     * @param value The string value to validate.
     * @param regex The regular expression pattern to match.
     * @param fieldName The name of the field being checked (for a readable assertion message).
     */
    public static void assertStringMatchesRegex(String value, String regex, String fieldName) {
        assertThat(
                String.format("%s should match the pattern: %s", fieldName, regex),
                value,
                matchesPattern(regex)
        );
    }

    /**
     * Asserts that a given object is not null and if is String — is not empty or blank.
     * @param value The object to check.
     * @param name The name of the field (for clear error messages).
     */
    public static void assertNotNullOrBlank(Object value, String name) {
        String fieldName = decapitalize(name);

        assertThat(String.format("%s should not be null", fieldName), value, notNullValue());
        if (value instanceof String str) {
            assertThat(String.format("%s should not be blank", fieldName), str.trim(), not(equalTo("")));
        }
    }

    /**
     * Asserts that a given Number value is within a specified range (inclusive).
     * Also checks if the value is a Number type (e.g., Float, Double, Integer).
     * @param value The Number value to validate.
     * @param fieldName The name of the field being checked (for a readable assertion message).
     * @param min The minimum allowed value (inclusive).
     * @param max The maximum allowed value (inclusive).
     */
    public static void assertNumberWithinRange(Number value, String fieldName, double min, double max) {
        String name = decapitalize(fieldName);

        assertNotNullOrBlank(value, fieldName);
        assertThat(String.format("%s should be a number", name), value, instanceOf(Number.class));
        assertThat(String.format("%s (%s) should be greater than or equal to %s", name, value, min),
                value.doubleValue(), greaterThanOrEqualTo(min));
        assertThat(String.format("%s (%s) should be less than or equal to %s", name, value, max),
                value.doubleValue(), lessThanOrEqualTo(max));
    }

    /**
     * Asserts that a given string's length is within a specified range (inclusive), or an exact length,
     * or at least a minimum, or at most a maximum.
     * @param value The string value whose length to validate.
     * @param fieldName The name of the field (for a readable assertion message).
     * @param minLength The minimum allowed length (inclusive). Pass {@code null} if no minimum is specified.
     * @param maxLength The maximum allowed length (inclusive). Pass {@code null} if no maximum is specified.
     */
    public static void assertStringLength(String value, String fieldName, Integer minLength, Integer maxLength) {
        String name = decapitalize(fieldName);

        assertThat(String.format("%s should not be null to check its length", name), value, notNullValue());

        int actualLength = value.length();

        boolean hasMin = minLength != null;
        boolean hasMax = maxLength != null;
        boolean isExact = hasMin && hasMax && minLength.equals(maxLength);

        if (isExact) {
            assertThat(String.format("%s length should be exactly %d characters, but was %d",
                            name, minLength, actualLength),
                    actualLength, equalTo(minLength));
        } else if (hasMin && hasMax) {
            assertThat(String.format("%s length (%d) should be between %d and %d characters (inclusive)",
                            name, actualLength, minLength, maxLength),
                    actualLength, both(greaterThanOrEqualTo(minLength)).and(lessThanOrEqualTo(maxLength)));
        } else if (hasMin) {
            assertThat(String.format("%s length (%d) should be at least %d characters",
                            name, actualLength, minLength),
                    actualLength, greaterThanOrEqualTo(minLength));
        } else if (hasMax) {
            assertThat(String.format("%s length (%d) should be at most %d characters",
                            name, actualLength, maxLength),
                    actualLength, lessThanOrEqualTo(maxLength));
        } else {
            System.out.printf("Warning: No length bounds specified for %s%n", name);
        }
    }

    /**
     * Asserts that a given object is considered "empty".
     * Checks for null, empty String, empty Collection, or empty Map.
     * @param value The object to check for emptiness.
     * @param fieldName The name of the field (for clear error messages).
     */
    public static void assertIsEmpty(Object value, String fieldName) {
        String name = decapitalize(fieldName);

        switch (value) {
            case null -> assertThat(String.format("%s should be null or empty", name), null, nullValue());
            case String str -> assertThat(String.format("%s should be empty", name), str, emptyString());
            case Collection<?> collection ->
                    assertThat(String.format("%s collection should be empty", name), collection, empty());
            case Map<?, ?> map -> assertThat(String.format("%s map should be empty", name), map.entrySet(), empty());
            default ->
                    throw new IllegalArgumentException(String.format("assertIsEmpty does not support checking for" +
                            " emptiness of type %s. Please define 'empty' for this type.",
                            value.getClass().getSimpleName()));
        }
    }

    /**
     * Asserts that a given object is considered "not empty".
     * Checks for non-null, non-empty String, non-empty Collection, or non-empty Map.
     * @param value The object to check for non-emptiness.
     * @param fieldName The name of the field (for clear error messages).
     */
    public static void assertIsNotEmpty(Object value, String fieldName) {
        String name = decapitalize(fieldName);

        assertThat(String.format("%s should not be null", name), value, notNullValue());

        switch (value) {
            case String str -> assertThat(String.format("%s should not be empty", name), str, not(emptyString()));
            case Collection<?> collection ->
                    assertThat(String.format("%s collection should not be empty", name), collection, not(empty()));
            case Map<?, ?> map ->
                    assertThat(String.format("%s map should not be empty", name), map.entrySet(), not(empty()));
            default -> {
            }
        }
    }
}
