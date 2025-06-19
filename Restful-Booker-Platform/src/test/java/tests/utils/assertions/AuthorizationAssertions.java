package tests.utils.assertions;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;

import static constants.ApiConstants.ERROR_JSON_PATH;
import static constants.ApiConstants.TOKEN_JSON_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


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

        assertThat("Token should not be null", token, is(notNullValue()));
        assertThat("Token should not be empty", token, is(not(emptyString())));
        assertThat("Token length should be 13 characters", token.length(), is(greaterThanOrEqualTo(13)));
    }
}