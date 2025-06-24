package tests.utils.assertions;

import models.response.BrandingResponse;
import static tests.utils.assertions.CommonAssertions.*;


/**
 * Utility class containing assertion methods specifically for BrandingResponse responses.
 */
public class BrandingAssertions {
    private BrandingAssertions() {
        // hidden constructor
    }

    public static void assertBrandingMatchesExpected(
            BrandingResponse expectedBranding,
            BrandingResponse actualBranding) {

        assertObjectsAreEqual(actualBranding.getName(), expectedBranding.getName(), "name");
        assertObjectsAreEqual(actualBranding.getLogoUrl(), expectedBranding.getLogoUrl(), "logoUrl");
        assertObjectsAreEqual(actualBranding.getDescription(), expectedBranding.getDescription(), "description");
        assertObjectsAreEqual(actualBranding.getDirections(), expectedBranding.getDirections(), "directions");

        // --- Compare Nested Map Object ---
        assertNotNullOrBlank(expectedBranding.getMap().getLatitude(), "latitude");
        assertNotNullOrBlank(actualBranding.getMap().getLongitude(), "longitude");
        assertNumericEqualsWithDelta(expectedBranding.getMap().getLatitude(), actualBranding.getMap().getLatitude(),
                0.000001, "latitude");
        assertNumericEqualsWithDelta(expectedBranding.getMap().getLongitude(), actualBranding.getMap().getLongitude(),
                0.000001, "longitude");

        // --- Compare Nested Contact Object ---
        assertObjectsAreEqual(actualBranding.getContact().getName(), expectedBranding.getContact().getName(), "contact name");
        assertObjectsAreEqual(actualBranding.getContact().getPhone(), expectedBranding.getContact().getPhone(), "contact phone");
        assertObjectsAreEqual(actualBranding.getContact().getEmail(), expectedBranding.getContact().getEmail(), "contact email");

        // --- Compare Nested Address Object ---
        assertObjectsAreEqual(actualBranding.getAddress().getLine1(), expectedBranding.getAddress().getLine1(), "address line 1");
        assertObjectsAreEqual(actualBranding.getAddress().getLine2(), expectedBranding.getAddress().getLine2(), "address line 2");
        assertObjectsAreEqual(actualBranding.getAddress().getPostTown(), expectedBranding.getAddress().getPostTown(), "address post town");
        assertObjectsAreEqual(actualBranding.getAddress().getCounty(), expectedBranding.getAddress().getCounty(), "address county");
        assertObjectsAreEqual(actualBranding.getAddress().getPostCode(), expectedBranding.getAddress().getPostCode(), "postCode");
    }
}
