package tests.branding;

import io.restassured.response.ValidatableResponse;
import models.response.BrandingResponse;
import org.junit.jupiter.api.*;

import java.util.List;

import static tests.utils.TestUtils.*;
import static constants.ApiConstants.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.assertions.CommonAssertions.*;
import static tests.utils.assertions.BrandingAssertions.*;


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
    }

    @AfterEach
    public void resetBranding() {
        if (branding != null) {
            givenRequest()
                    .header("Cookie", String.format("token=%s", authToken))
                    .body(branding) // Reset to the original state
                    .when()
                    .put(BRANDING_ENDPOINT)
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    public void testUpdateWithoutAuthentication() {
        BrandingResponse brandingToUpdate = new BrandingResponse(branding);

        ValidatableResponse response = givenRequest()
                .body(brandingToUpdate)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertFailedResponse(response, 401, AUTHENTICATION_REQUIRED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when token is invalid")
    public void testUpdateWithInvalidToken() {
        BrandingResponse brandingToUpdate = new BrandingResponse(branding);

        List<String> errors = givenRequest()
                .header("Cookie", "token=test123")
                .body(brandingToUpdate)
                .when()
                .put(BRANDING_ENDPOINT)
                .then()
                .statusCode(500)
                .extract().jsonPath().getList(ERRORS_JSON_PATH, String.class);

        assertListContains(errors, UNEXPECTED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should update one field in branding successfully with polling")
    public void testSuccessfulBrandingUpdateWithPolling() {
        BrandingResponse brandingToUpdate = new BrandingResponse(branding);
        String newDescription = "Changed description";
        brandingToUpdate.setDescription(newDescription);

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(brandingToUpdate)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);

        int maxWaitSeconds = 60;
        int pollIntervalSeconds = 5;

        BrandingResponse updatedBranding = waitForBrandingUpdate(
                branding -> branding != null && newDescription.equals(branding.getDescription()),
                maxWaitSeconds,
                pollIntervalSeconds);

        assertNotNullOrBlank(updatedBranding, "Branding Response");
        assertBrandingMatchesExpected(brandingToUpdate, updatedBranding);
    }

    @Test
    @DisplayName("Should update multiple fields in branding successfully")
    public void testSuccessfulMultipleFieldsBrandingUpdate() {
        BrandingResponse brandingToUpdate = loadRequestFromFile(MULTIPLE_BRANDING_FIELDS_UPDATE_PATH, BrandingResponse.class);

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(brandingToUpdate)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);

        int maxWaitSeconds = 60;
        int pollIntervalSeconds = 5;

        BrandingResponse updatedBranding = waitForBrandingUpdate(
                branding -> (branding != null) && branding.equals(brandingToUpdate),
                maxWaitSeconds,
                pollIntervalSeconds
        );

        assertNotNullOrBlank(updatedBranding, "Branding Response");
        assertBrandingMatchesExpected(brandingToUpdate, updatedBranding);
    }
}
