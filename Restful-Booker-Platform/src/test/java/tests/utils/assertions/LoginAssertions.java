package tests.utils.assertions;

import io.restassured.response.ValidatableResponse;

import static constants.ApiConstants.ERROR_JSON_PATH;
import static constants.ApiConstants.TOKEN_JSON_PATH;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


/**
 * Utility class containing assertion methods specifically for authentication/login API responses.
 */
public final class LoginAssertions {

    private LoginAssertions() {
        // hidden constructor
    }

    /**
     * Asserts that a login response is successful (status 200 and token not null).
     * @param response The ValidatableResponse object to assert against.
     */
    public static void assertSuccessfulLoginResponse(ValidatableResponse response) {
        response.statusCode(200)
                .body(TOKEN_JSON_PATH, notNullValue());
    }

    /**
     * Asserts that a login response indicates failure with specific status code and error message.
     * @param response The ValidatableResponse object to assert against.
     */
    public static void assertFailedLoginResponse(ValidatableResponse response, String expectedErrorMessage) {
        response.statusCode(401)
                .body(ERROR_JSON_PATH, equalTo(expectedErrorMessage));
    }
}