package tests.authorization;

import io.restassured.response.ValidatableResponse;
import models.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tests.base.BaseTest;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static constants.ApiConstants.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tests.utils.TestUtils.loadRequest;
import static tests.utils.assertions.CommonAssertions.assertFailedResponse;
import static tests.utils.assertions.CommonAssertions.assertInternalServerError;
import static tests.utils.assertions.AuthorizationAssertions.assertSuccessfulLoginResponse;


public class CreateTokenTests extends BaseTest {

    /** Thread-safe set to keep track of generated tokens during repeated tests
     * to ensure each token is unique across repeated login attempts.
     */
    private static final Set<String> generatedTokens = new HashSet<>();

    @BeforeEach
    public void clearGeneratedTokens() {
        synchronized (generatedTokens) {
            generatedTokens.clear();
        }
    }

    // --- Test Cases ---
    @Test
    @DisplayName("Should create token successfully with valid credentials")
    public void testSuccessfulLogin() {
        LoginRequest loginRequest = loadRequest(CORRECT_LOGIN_PATH, LoginRequest.class);

        ValidatableResponse response = givenRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then();

        assertSuccessfulLoginResponse(response);
    }

    @Test
    @DisplayName("Should fail login with incorrect credentials")
    public void testFailedLoginWithIncorrectCredentials() {
        LoginRequest loginRequest = loadRequest(INCORRECT_LOGIN_PATH, LoginRequest.class);

        ValidatableResponse response = givenRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then();

        assertFailedResponse(response, 401, INVALID_CREDENTIALS_ERROR_MESSAGE);
    }

    /**
     * Provides a stream of arguments for parameterized tests, covering various invalid login request scenarios.
     * Each argument includes a LoginRequest object and a display name.
     */
    private static Stream<Arguments> invalidLoginRequests() {
        return Stream.of(
                Arguments.of(new LoginRequest(null, "password"), "Missing username"),
                Arguments.of(new LoginRequest("admin", null), "Missing password"),
                Arguments.of(new LoginRequest("", "password"), "Empty username"),
                Arguments.of(new LoginRequest("admin", ""), "Empty password"),
                Arguments.of(new LoginRequest(null, null), "Null username and password"),
                Arguments.of(new LoginRequest("", ""), "Empty username and password")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("invalidLoginRequests")
    @DisplayName("Should return 401 for various invalid credentials scenarios")
    public void testInvalidCredentials(LoginRequest loginRequest, String displayName) {
        ValidatableResponse response = givenRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then();

        assertFailedResponse(response, 401, INVALID_CREDENTIALS_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 for malformed JSON request body")
    public void testLoginWithMalformedJson() {
        String malformedJson = "{ \"username\": \"admin\", \"password\": \"pass\"";

        ValidatableResponse response = givenRequest()
                .body(malformedJson)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then();

        assertInternalServerError(response, UNEXPECTED_ERROR_MESSAGE);
    }

    @RepeatedTest(5)
    @DisplayName("Each repeated login request should create a new, unique token")
    public void testRepeatedLoginTokenUniqueness() {
        LoginRequest loginRequest = loadRequest(CORRECT_LOGIN_PATH, LoginRequest.class);

        String token = givenRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .body(TOKEN_JSON_PATH, notNullValue())
                .extract()
                .path(TOKEN_JSON_PATH);

        assertNotNull(token, "Extracted token should not be null");

        synchronized (generatedTokens) {
            boolean isNew = generatedTokens.add(token);
            assertTrue(isNew, String.format("Token '%s' should be unique across repeated test runs, " +
                    "but it was duplicated.", token));
        }
    }
}