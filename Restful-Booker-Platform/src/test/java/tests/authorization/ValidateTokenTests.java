package tests.authorization;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.base.BaseTest;

import static constants.ApiConstants.*;
import static tests.utils.assertions.CommonAssertions.*;


public class ValidateTokenTests extends BaseTest {

    // --- Reusable Token for Valid Scenarios ---
    private static String validAuthToken;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken() {
        validAuthToken = getAuthToken();
    }

    @Test
    @DisplayName("Should successfully validate a valid token")
    public void testValidTokenValidation() {
        String requestBody = String.format("{ \"%s\": \"%s\" }", TOKEN_JSON_PATH, validAuthToken);

        ValidatableResponse response = givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then();

        assertJsonBooleanValue(response, VALID_JSON_PATH, true);
    }

    @Test
    @DisplayName("Should fail validation with 401 for an invalid token")
    public void testInvalidTokenValidation() {
        String invalidToken = "thisisajustsomeinvalidtokenstringforexample";
        String requestBody = String.format("{ \"%s\": \"%s\" }", TOKEN_JSON_PATH, invalidToken);

        ValidatableResponse response = givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then();

        assertFailedResponse(response, 403, INVALID_TOKEN_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should fail validation with 401 for an empty token string")
    public void testEmptyTokenStringValidation() {
        String emptyToken = "";
        String requestBody = String.format("{ \"%s\": \"%s\" }", TOKEN_JSON_PATH, emptyToken);

        ValidatableResponse response = givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then();

        assertFailedResponse(response, 401, NO_TOKEN_PROVIDED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should fail validation with 401 for a null token value")
    public void testNullTokenValueValidation() {
        String requestBody = String.format("{ \"%s\": %s }", TOKEN_JSON_PATH, "null");

        ValidatableResponse response = givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then();

        assertFailedResponse(response, 401, NO_TOKEN_PROVIDED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 for malformed token format")
    public void testValidationWithMalformedJson() {
        String malformedTokenBody = "{ \"token\": thisisnotastring }";

        ValidatableResponse response = givenRequest()
                .body(malformedTokenBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then();

        assertInternalServerError(response, UNEXPECTED_ERROR_MESSAGE);
    }
}