package tests.branding;

import io.restassured.response.ValidatableResponse;
import models.response.BrandingResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static tests.utils.TestUtils.*;
import static constants.ApiConstants.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.assertions.CommonAssertions.*;
import static tests.utils.assertions.BrandingAssertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpdateBrandingTests {

    // --- Reusable Token for Valid Scenarios ---
    private static String authToken;
    private static BrandingResponse originalBranding;
    private boolean brandingModified = false;
    private static final int maxWaitSeconds = 60;
    private static final int pollIntervalSeconds = 5;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken() {
        authToken = getAuthToken();

        originalBranding = givenRequest()
                .when()
                .get(BRANDING_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .as(BrandingResponse.class);

        assertNotNullOrBlank(originalBranding, "Branding Response");
    }

    @AfterEach
    public void resetBranding() {
        if (brandingModified) {
            BrandingResponse clean = cloneBranding(originalBranding);

            givenRequest()
                    .header("Cookie", String.format("token=%s", authToken))
                    .body(clean)
                    .when()
                    .put(BRANDING_ENDPOINT)
                    .then()
                    .statusCode(200);

            brandingModified = false;
        }
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    public void testUpdateWithoutAuthentication() {
        BrandingResponse brandingToUpdate = cloneBranding(originalBranding);

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
        BrandingResponse brandingToUpdate = cloneBranding(originalBranding);

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
        BrandingResponse brandingToUpdate = cloneBranding(originalBranding);
        String newDescription = "Changed description";
        brandingToUpdate.setDescription(newDescription);
        brandingModified = true;

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(brandingToUpdate)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);

        BrandingResponse updatedBranding = waitForCondition(
                () -> givenRequest()
                        .when()
                        .get(BRANDING_ENDPOINT)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(BrandingResponse.class),
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
        brandingModified = true;

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(brandingToUpdate)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);

        BrandingResponse updatedBranding = waitForCondition(
                () -> givenRequest()
                        .when()
                        .get(BRANDING_ENDPOINT)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(BrandingResponse.class),
                branding -> (branding != null) && branding.equals(brandingToUpdate),
                maxWaitSeconds,
                pollIntervalSeconds
        );

        assertNotNullOrBlank(updatedBranding, "Branding Response");
        assertBrandingMatchesExpected(brandingToUpdate, updatedBranding);
    }

    @Test
    @DisplayName("Should update branding with the same data successfully")
    public void testBrandingUpdateSameData() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(originalBranding)
                .when()
                .put(BRANDING_ENDPOINT)
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);

        BrandingResponse updatedBranding = waitForCondition(
                () -> givenRequest()
                        .when()
                        .get(BRANDING_ENDPOINT)
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(BrandingResponse.class),
                newBranding -> (newBranding != null) && newBranding.equals(originalBranding),
                maxWaitSeconds,
                pollIntervalSeconds
        );

        assertNotNullOrBlank(updatedBranding, "Branding Response");
        assertBrandingMatchesExpected(originalBranding, updatedBranding);
    }

    /**
     * Provides invalid branding update scenarios.
     * This method generates a stream of arguments for parameterized tests,
     * each representing a distinct invalid state of the BrandingResponse.
    */
    private Stream<Arguments> invalidDataProvider() {
        BrandingResponse branding1 = cloneBranding(originalBranding);
        branding1.setName("");

        BrandingResponse branding2 = cloneBranding(originalBranding);
        branding2.setLogoUrl("/img/rbp-logo.png");

        BrandingResponse branding3 = cloneBranding(originalBranding);
        branding3.getContact().setPhone(null);

        return Stream.of(
                Arguments.of(branding1, "Blank name", NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(branding2, "Incorrect url", INCORRECT_URL_FORMAT_ERROR_MESSAGE),
                Arguments.of(branding3, "Null phone", PHONE_NOT_NULL_ERROR_MESSAGE)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("invalidDataProvider")
    @DisplayName("Should return expected error for invalid branding data")
    public void testUpdateWithInvalidData(BrandingResponse brandingToUpdate, String displayName, String expectedErrorMessage) {
        brandingModified = true;

        List<String> errors = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(brandingToUpdate)
                .when()
                .put(BRANDING_ENDPOINT)
                .then()
                .statusCode(400)
                .extract()
                .jsonPath().getList(FIELD_ERRORS_PATH, String.class);

        assertListContains(errors, expectedErrorMessage);
    }

    @Test
    @DisplayName("Should return 500 for malformed JSON request body")
    public void testUpdateWithMalformedJson() {
        String malformedJson = "{" +
                "  \"name\": \"Shady Meadows B&B\"," +
                "  \"map\": {" +
                "    \"latitude\": 52.6351204," +
                "    \"longitude\": 1.2733774," + // Missing closing brace for 'map' object
                "  \"logoUrl\": \"https://www.mwtestconsultancy.co.uk/img/rbp-logo.png\"," +
                "  description: \"Changed description\"," + // Unquoted key 'description'
                "  \"directions\": \"Welcome to Shady Meadows...\"," +
                "  \"contact\": {" +
                "    \"name\": \"Shady Meadows B&B\"," +
                "    \"phone\": \"012345678901\"," +
                "    \"email\": \"fake@fakeemail.com\"" +
                "  }," +
                "  \"address\": {" +
                "    \"line1\": \"Shady Meadows B&B\"," +
                "    \"line2\": \"Shadows valley\"," +
                "    \"postTown\": \"Newingtonfordburyshire\"," +
                "    \"county\": \"Dilbert\"," +
                "    \"postCode\": \"N1 1AA\"" +
                "  }" +
                "}";

        List<String> errors = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(malformedJson)
                .when()
                .put(BRANDING_ENDPOINT)
                .then()
                .statusCode(500)
                .extract()
                .jsonPath().getList(ERRORS_JSON_PATH, String.class);

        assertListContains(errors, UNEXPECTED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when request has empty body")
    public void testUpdateWithEmptyRequestBody() {
       List<String> errors = givenRequest()
               .header("Cookie", String.format("token=%s", authToken))
               .when()
               .put(BRANDING_ENDPOINT)
               .then()
               .statusCode(500)
               .extract()
               .jsonPath().getList(ERRORS_JSON_PATH, String.class);

        assertListContains(errors, UNEXPECTED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should fail with 500 when request has missing fields")
    public void testUpdateWithMissingMandatoryFields() {
        String missingFieldJson = "{" +
                "  \"name\": \"Shady Meadows B&B\"," +
                "  \"contact\": {" +
                "    \"name\": \"Shady Meadows B&B\"," +
                "    \"phone\": \"012345678901\"," +
                "    \"email\": \"fake@fakeemail.com\"" +
                "  }," +
                "}";

        List<String> errors = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .body(missingFieldJson)
                .when()
                .put(BRANDING_ENDPOINT)
                .then()
                .statusCode(500)
                .extract()
                .jsonPath().getList(ERRORS_JSON_PATH, String.class);

        assertListContains(errors, UNEXPECTED_ERROR_MESSAGE);
    }
}
