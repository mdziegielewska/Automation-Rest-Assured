package tests.message;

import io.restassured.response.ValidatableResponse;
import models.common.FullMessage;
import models.common.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static constants.ApiConstants.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.TestUtils.generate10DigitNumericString;
import static tests.utils.assertions.CommonAssertions.*;
import static tests.utils.assertions.MessageAssertions.*;


public class GetMessageTests {

    // --- Reusable Token for Valid Scenarios ---
    private static String authToken;
    private static FullMessage message;

    // --- Test Cases ---
    @BeforeAll
    public static void setup() {
        authToken = getAuthToken();
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    public void testGetMessageWithNoAuthentication()  {
        ValidatableResponse response = givenRequest()
                .pathParams("messageid", 1)
                .when()
                .get(String.format("%s/%s", MESSAGE_ENDPOINT, "{messageid}"))
                .then();

        assertFailedResponse(response, 401, AUTHENTICATION_REQUIRED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 404 when non numeric id")
    public void testGetMessageWithNonNumericId() {
        String nonNumericMessageId = "abc";

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("messageid", nonNumericMessageId)
                .when()
                .get(String.format("%s/%s", MESSAGE_ENDPOINT, "{messageid}"))
                .then();

        assertNotFoundMessageResponse(response, 404, NOT_FOUND_ERROR_MESSAGE, nonNumericMessageId);
    }

    @Test
    @DisplayName("Should return 400 when message id is too long")
    public void testGetMessageWithTooLongId() {
        String longMessageId = generate10DigitNumericString();

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("messageid", longMessageId)
                .when()
                .get(String.format("%s/%s", MESSAGE_ENDPOINT, "{messageid}"))
                .then();

        assertNotFoundMessageResponse(response, 400, BAD_REQUEST_ERROR_MESSAGE, longMessageId);
    }

    /**
     * Provides a stream of arguments, covering required fields of all messages in the list.
     * Each argument contains: (fieldValue, displayLabel).
     */
    static Stream<Arguments> requiredFieldsProvider() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("messageid", 1)
                .when()
                .get(String.format("%s/%s", MESSAGE_ENDPOINT, "{messageid}"))
                .then()
                .statusCode(200);

        FullMessage message = response.extract().as(FullMessage.class);

        return Stream.of(
                Arguments.of(message.getMessageid(), "Messageid", Integer.class, response),
                Arguments.of(message.getName(), "Name", String.class, response),
                Arguments.of(message.getEmail(), "Email", String.class, response),
                Arguments.of(message.getPhone(), "Phone", String.class, response),
                Arguments.of(message.getSubject(), "Subject", String.class, response),
                Arguments.of(message.getDescription(), "Description", String.class, response)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("requiredFieldsProvider")
    @DisplayName("Should return message with all required fields present")
    public void testMessageStructure(Object value, String displayName,
                                     Class<?> expectedType, ValidatableResponse response) {
        assertNotNullOrBlank(value, displayName);
        assertJsonFieldType(response, displayName, expectedType);
    }
}
