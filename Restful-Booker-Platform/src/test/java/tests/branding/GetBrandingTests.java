package tests.branding;

import models.common.Address;
import models.common.Contact;
import models.common.Map;
import models.response.BrandingResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static constants.ApiConstants.BRANDING_ENDPOINT;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.assertions.CommonAssertions.*;


public class GetBrandingTests {

    private static BrandingResponse branding;

    // --- Test Cases ---
    @BeforeAll
    public static void fetchBrandingData() {
        branding = givenRequest()
                .when()
                .get(BRANDING_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .as(BrandingResponse.class);

        assertNotNullOrBlank(branding, "Branding Response Object");
    }

    /**
     * Provides a stream of arguments for parameterized tests, covering various required fields
     * within the BrandingResponse. Each argument includes the field's value and a display name.
     */
    static Stream<Arguments> requiredFieldsProvider() {
        return Stream.of(
                Arguments.of(branding.getName(), "Name"),
                Arguments.of(branding.getMap(), "Map"),
                Arguments.of(branding.getLogoUrl(), "LogoUrl"),
                Arguments.of(branding.getDescription(), "Description"),
                Arguments.of(branding.getDirections(), "Directions"),
                Arguments.of(branding.getContact(), "Contact"),
                Arguments.of(branding.getAddress(), "Address")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("requiredFieldsProvider")
    @DisplayName("Should return branding with all required fields present")
    public void testBrandingStructure(Object value, String displayName) {
        assertNotNullOrBlank(value, displayName);
    }

    @Test
    @DisplayName("Should return logoUrl with valid image extension")
    public void testLogoUrlFormat() {
        assertStringMatchesRegex(branding.getLogoUrl(),
                "^(/images/.*|https?://.*).*\\.(jpg|jpeg|png|gif|webp|svg)$", "logoUrl");
    }

    /**
     * Provides a stream of arguments for parameterized tests, covering various map coordinates (latitude and longitude).
     * Each argument includes the coordinate's value, name, and its min/max valid range.
     */
    private static Stream<Arguments> mapCoordinatesProvider() {
        Map map = branding.getMap();
        assertNotNullOrBlank(map, "Map Data Object for Coordinates");

        return Stream.of(
                Arguments.of(map.getLatitude(), "Latitude", -90.0, 90.0),
                Arguments.of(map.getLongitude(), "Longitude", -180.0, 180.0)
        );
    }

    @ParameterizedTest(name = "{1} should be a valid number within range [{2}, {3}]")
    @MethodSource("mapCoordinatesProvider")
    @DisplayName("Should return valid map coordinates within expected ranges")
    public void testMapCoordinates(Number coordinateValue, String coordinateName, double min, double max) {
        assertNumberWithinRange(coordinateValue, coordinateName, min, max);
    }

    /**
     * Provides a stream of arguments for parameterized tests, covering various contact fields.
     * Each argument includes the contact field's value, a display name, and an optional regex pattern.
     */
    private static Stream<Arguments> contactFieldsProvider() {
        Contact contact = branding.getContact();

        return Stream.of(
                Arguments.of(contact.getName(), "name", null),
                Arguments.of(contact.getPhone(), "phone", "^[+]?\\d[\\d\\s]*$"),
                Arguments.of(contact.getEmail(), "email", "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("contactFieldsProvider")
    @DisplayName("Should return valid contact information fields")
    public void testContactFields(String value, String displayName, String regex) {
        assertNotNullOrBlank(value, displayName);
        if (regex != null) {
            assertStringMatchesRegex(value, regex, displayName);
        }
    }

    /**
     * Provides a stream of arguments for parameterized tests, covering various address fields.
     * Each argument includes the address field's value and a display name.
     */
    private static Stream<Arguments> addressFieldsProvider() {
        Address address = branding.getAddress();

        return Stream.of(
                Arguments.of(address.getLine1(), "Line1"),
                Arguments.of(address.getLine2(), "Line2"),
                Arguments.of(address.getPostTown(), "PostTown"),
                Arguments.of(address.getCounty(), "County"),
                Arguments.of(address.getPostCode(), "PostCode")
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("addressFieldsProvider")
    @DisplayName("Should return valid address fields")
    public void testAddressFields(String value, String displayName) {
        assertNotNullOrBlank(value, displayName);
    }
}
