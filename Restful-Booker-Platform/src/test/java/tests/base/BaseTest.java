package tests.base;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;
import models.LoginRequest;
import tests.utils.TestUtils;

import static constants.ApiConstants.*;
import static constants.ApiConstants.TOKEN_JSON_PATH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tests.utils.TestUtils.loadLoginRequest;

/**
 * Base class for all API test cases.
 * Configurations defined here are applied once when the class is loaded,
 * making them available for all test methods in subclasses.
 */
public class BaseTest {

    static {
        RestAssured.baseURI = "https://automationintesting.online/api";

        RestAssured.config = RestAssured.config()
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2));
    }

    /**
     * Provides a base RequestSpecification with content type set to JSON.
     * This avoids repetition of `given().contentType("application/json")`.
     * @return A RequestSpecification pre-configured for JSON content.
     */
    protected static RequestSpecification givenAuthRequest() {
        return given().contentType(ContentType.JSON);
    }

    /**
     * Performs a login operation and retrieves a valid authentication token.
     * This method leverages common utility functions to load login data
     * and sends a POST request to the authentication endpoint.
     * @return A valid authentication token as a String.
     * @throws RuntimeException if login fails or token is not retrieved.
     */
    protected static String getAuthToken() {
        // Perform the login request
        LoginRequest loginRequest = loadLoginRequest(CORRECT_LOGIN_PATH);

        String token = givenAuthRequest()
                .body(loginRequest)
                .when()
                .post(AUTH_LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .body(TOKEN_JSON_PATH, notNullValue()) // Ensure token is not null
                .extract()
                .path(TOKEN_JSON_PATH);// Extract the token

        // Ensure token is not null before returning
        assertNotNull(token, "Failed to obtain a valid token during login.");
        System.out.println("Successfully obtained authentication token for tests."); // Optional: for debugging
        return token;
    }
}