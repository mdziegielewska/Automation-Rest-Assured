package tests.authorization;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import models.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tests.base.BaseTest;
import tests.utils.TestUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CreateTokenTests extends BaseTest {

    // Thread-safe set to keep track of generated tokens during repeated tests
    // to ensure each token is unique across repeated login attempts.
    private static final Set<String> generatedTokens = new HashSet<>();

    @BeforeEach
    public void clearGeneratedTokens() {
        synchronized (generatedTokens) {
            generatedTokens.clear();
        }
    }

    // --- Constants for better readability and maintainability ---
    private static final String AUTH_LOGIN_ENDPOINT = "/auth/login";
    private static final String TOKEN_JSON_PATH = "token";
    private static final String ERROR_JSON_PATH = "error";
    private static final String INVALID_CREDENTIALS_ERROR_MESSAGE = "Invalid credentials";

    // --- Test Data Paths ---
    private static final String CORRECT_DATA_PATH = "testData/authorization/correctData.json";
    private static final String INCORRECT_DATA_PATH = "testData/authorization/incorrectData.json";

    /**
     * Helper method to load LoginRequest object from a JSON file.
     * @param resourcePath The path to the JSON file containing login data.
     * @return A LoginRequest object populated with data from the file.
     * @throws RuntimeException if loading the file fails.
     */
    private LoginRequest loadLoginRequest(String resourcePath) {
        try {
            return TestUtils.loadLoginRequestFromFile(resourcePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data from: " + resourcePath, e);
        }
    }

    /**
     * Provides a base RequestSpecification with content type set to JSON.
     * This avoids repetition of `given().contentType("application/json")`.
     * @return A RequestSpecification pre-configured for JSON content.
     */
    private static RequestSpecification givenAuthRequest() {
        return given().contentType(ContentType.JSON);
    }

    // --- Assertion Helper Methods ---
    /**
     * Asserts that a login response is successful (status 200 and token not null).
     * @param response The ValidatableResponse object to assert against.
     */
    private void assertSuccessfulLoginResponse(ValidatableResponse response) {
        response.statusCode(200)
                .body(TOKEN_JSON_PATH, notNullValue());
    }

    /**
     * Asserts that a login response indicates failure with specific status code and error message.
     * @param response The ValidatableResponse object to assert against.
     */
    private void assertFailedLoginResponse(ValidatableResponse response) {
        response.statusCode(401)
                .body(ERROR_JSON_PATH, equalTo(CreateTokenTests.INVALID_CREDENTIALS_ERROR_MESSAGE));
    }

    // --- Test Cases ---
    @Test
    @DisplayName("Should create token successfully with valid credentials")
    public void testSuccessfulLogin() {
        LoginRequest loginRequest = loadLoginRequest(CORRECT_DATA_PATH);

        ValidatableResponse response = givenAuthRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then();

        assertSuccessfulLoginResponse(response);
    }

    @Test
    @DisplayName("Should fail login with incorrect credentials from file")
    public void testFailedLoginWithIncorrectCredentials() {
        LoginRequest loginRequest = loadLoginRequest(INCORRECT_DATA_PATH);

        ValidatableResponse response = givenAuthRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then();

        assertFailedLoginResponse(response);
    }

    /**
     * Provides a stream of arguments for parameterized tests,
     * covering various invalid login request scenarios.
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
    @DisplayName("Should fail login with various invalid credentials scenarios")
    public void testInvalidCredentials(LoginRequest loginRequest, String displayName) {
        ValidatableResponse response = givenAuthRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then();

        assertFailedLoginResponse(response);
    }

    @Test
    @DisplayName("Should return 500 for malformed JSON request body")
    public void testMalformedJson() {
        String malformedJson = "{ \"username\": \"admin\", \"password\": \"pass\""; // Missing closing brace

        givenAuthRequest()
                .body(malformedJson)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then()
                .statusCode(500);
    }

    @RepeatedTest(5)
    @DisplayName("Each repeated login request should create a new, unique token")
    public void testRepeatedLoginTokenUniqueness() {
        LoginRequest loginRequest = loadLoginRequest(CORRECT_DATA_PATH);

        String token = givenAuthRequest()
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
            assertTrue(isNew, "Token '" + token + "' should be unique across repeated test runs, but it was duplicated.");
        }
    }
}