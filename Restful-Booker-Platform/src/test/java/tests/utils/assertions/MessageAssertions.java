package tests.utils.assertions;

import io.restassured.response.ValidatableResponse;

import static constants.ApiConstants.MESSAGE_ENDPOINT;
import static tests.utils.assertions.CommonAssertions.*;


/**
 * Utility class containing assertion methods specifically for MessageResponse.
 */
public class MessageAssertions {

    private MessageAssertions() {
        // hidden constructor
    }

    public static void assertNotFoundMessageResponse(ValidatableResponse response, Integer statusCode,
                                                    String pathValue, String expectedMessageId) {
        response.statusCode(statusCode);

        assertJsonFieldType(response, "status", Integer.class);
        assertJsonValueEquals(response, "status", statusCode);

        assertJsonFieldType(response, "error", String.class);
        assertJsonValueEquals(response, "error", pathValue);

        assertJsonFieldType(response, "path", String.class);
        assertJsonPathPrefixAndSuffix(response, "path", String.format("%s/", MESSAGE_ENDPOINT), expectedMessageId);

        assertJsonFieldType(response, "timestamp", String.class);
        assertNotNullOrBlank(response.extract().jsonPath().getString("timestamp"), "Timestamp");
    }
}
