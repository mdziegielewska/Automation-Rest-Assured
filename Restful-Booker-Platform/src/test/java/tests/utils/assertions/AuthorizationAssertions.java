package tests.utils.assertions;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import static constants.ApiConstants.TOKEN_JSON_PATH;
import static tests.utils.assertions.CommonAssertions.assertNotNullOrBlank;
import static tests.utils.assertions.CommonAssertions.assertStringLength;


/**
 * Utility class containing assertion methods specifically for authentication/login API responses.
 */
public final class AuthorizationAssertions {

    private AuthorizationAssertions() {
        // hidden constructor
    }

    /**
     * Asserts that a login response is successful (status 200 and token not null).
     * @param response The ValidatableResponse object to assert against.
     */
    public static void assertSuccessfulLoginResponse(ValidatableResponse response) {
        JsonPath jsonPath = response.statusCode(200)
                        .extract().jsonPath();

        String token = jsonPath.getString(TOKEN_JSON_PATH);

        assertNotNullOrBlank(token, "Token");
        assertStringLength(token, "Token", 13, null);
    }
}