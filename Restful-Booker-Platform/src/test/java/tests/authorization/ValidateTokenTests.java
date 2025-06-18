package tests.authorization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.base.BaseTest;

import static constants.ApiConstants.*;
import static org.hamcrest.Matchers.*;


public class ValidateTokenTests extends BaseTest {

    // --- Reusable Token for Valid Scenarios ---
    private static String validAuthToken;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken() {
        validAuthToken = getAuthToken();
        System.out.printf("Obtained valid token for validation tests: %s", validAuthToken);
    }

    @Test
    @DisplayName("Should successfully validate a valid token")
    public void testValidTokenValidation() {
        String requestBody = String.format("{ \"%s\": \"%s\" }", TOKEN_JSON_PATH, validAuthToken);

        givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then()
                .statusCode(200)
                .body(VALID_JSON_PATH, is(true));
    }

    @Test
    @DisplayName("Should fail validation for an invalid token")
    public void testInvalidTokenValidation() {
        String invalidToken = "thisisajustsomeinvalidtokenstringforexample";
        String requestBody = String.format("{ \"%s\": \"%s\" }", TOKEN_JSON_PATH, invalidToken);

        givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then()
                .statusCode(403)
                .body(ERROR_JSON_PATH, equalTo(INVALID_TOKEN_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Should fail validation for an empty token string")
    public void testEmptyTokenStringValidation() {
        String emptyToken = "";
        String requestBody = String.format("{ \"%s\": \"%s\" }", TOKEN_JSON_PATH, emptyToken);

        givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then()
                .statusCode(401)
                .body(ERROR_JSON_PATH, equalTo(NO_TOKEN_PROVIDED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Should fail validation for a null token value")
    public void testNullTokenValueValidation() {
        String requestBody = String.format("{ \"%s\": %s }", TOKEN_JSON_PATH, "null");

        givenRequest()
                .body(requestBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then()
                .statusCode(401)
                .body(ERROR_JSON_PATH, equalTo(NO_TOKEN_PROVIDED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Should handle malformed token format")
    public void testMalformedJsonRequestBody() {
        String malformedTokenBody = "{ \"token\": thisisnotastring }";

        givenRequest()
                .body(malformedTokenBody)
                .when()
                .post(AUTH_VALIDATE_ENDPOINT)
                .then()
                .statusCode(500);
    }
}