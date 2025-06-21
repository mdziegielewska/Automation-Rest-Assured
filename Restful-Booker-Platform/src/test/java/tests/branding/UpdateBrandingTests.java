package tests.branding;

import io.restassured.response.ValidatableResponse;
import models.response.BrandingResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static constants.ApiConstants.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.assertions.CommonAssertions.*;

public class UpdateBrandingTests {

    // --- Reusable Token for Valid Scenarios ---
    private static String authToken;
    private static BrandingResponse branding;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken(TestInfo testInfo) {
        authToken = getAuthToken();

        branding = givenRequest()
                .when()
                .get(BRANDING_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .as(BrandingResponse.class);

        assertNotNullOrBlank(branding, "Branding Response");

        if (testInfo.getDisplayName().equals("testBrandingUpdateSameData")) {
            return;
        }

        branding.setDescription("Changed");
        branding.setLogoUrl("https://www.mwtestconsultancy.co.uk/img/image.png");
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    public void testUpdateWithoutAuthentication() {
        ValidatableResponse response = givenRequest()
                .body(branding)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertFailedResponse(response, 401, AUTHENTICATION_REQUIRED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when token is invalid")
    public void testUpdateWithInvalidToken() {
        List<String> errors = givenRequest()
                .header("Cookie", "token=test123")
                .body(branding)
                .when()
                .put(BRANDING_ENDPOINT)
                .then()
                .statusCode(500)
                .extract().jsonPath().getList(ERRORS_JSON_PATH, String.class);

        assertListContains(errors, UNEXPECTED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should update branding successfully")
    public void testSuccessfulBrandingUpdate() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(branding)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);
    }
}
