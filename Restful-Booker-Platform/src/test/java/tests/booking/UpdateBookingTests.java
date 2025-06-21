package tests.booking;

import io.restassured.response.ValidatableResponse;
import models.common.BookingDates;
import models.response.BookingResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static constants.ApiConstants.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.TestUtils.*;
import static tests.utils.assertions.CommonAssertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpdateBookingTests {

    // --- Reusable Token for Valid Scenarios ---
    private static String authToken;
    private static BookingResponse retrievedBooking;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken() {
        authToken = getAuthToken();
    }

    @BeforeEach
    public void setup(TestInfo testInfo) {
        // to do: add booking creation in setup to make tests more independent

        retrievedBooking = getFirstBookingId(authToken, 1);
        assertNotNullOrBlank(retrievedBooking, "Booking Response");

        if (testInfo.getDisplayName().equals("testBookingUpdateSameData")) {
            return;
        }

        retrievedBooking.setFirstname("Changed");
        retrievedBooking.setDepositpaid(true);
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    public void testUpdateWithoutAuthentication() {
        ValidatableResponse response = givenRequest()
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .body(retrievedBooking)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertFailedResponse(response, 401, AUTHENTICATION_REQUIRED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when token is invalid")
    public void testUpdateWithInvalidToken() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", "token=test123")
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .body(retrievedBooking)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(response, BOOKING_UPDATE_GENERIC_FAILURE_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should update booking successfully")
    public void testSuccessfulBookingUpdate() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .body(retrievedBooking)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);
    }

    @Test
    @DisplayName("Should update booking with the same data successfully")
    public void testBookingUpdateSameData() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .body(retrievedBooking)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);
    }

    @Test
    @DisplayName("Should return 500 for malformed JSON request body")
    public void testUpdateWithMalformedJson() {
        String malformedJson = "{" +
                "  \"roomid\": 1," +
                "  \"firstname\": \"Test\"" + // Missing comma
                "  \"lastname\": \"User\"," +
                "  \"depositpaid\": false," +
                "  \"bookingdates\": {" +
                "    \"checkin\": \"2025-07-23\"," +
                "    \"checkout\": \"2025-07-24\"" +
                "}";

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .body(malformedJson)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(response, BOOKING_UPDATE_GENERIC_FAILURE_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when request has empty body")
    public void testUpdateWithEmptyRequestBody() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(response, BOOKING_UPDATE_GENERIC_FAILURE_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should handle booking non-existent booking id")
    public void testUpdateNonExistentBooking() {
        Integer nonExistentBookingId = Math.toIntExact(generateLongWithDigits(5));

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", nonExistentBookingId)
                .body(retrievedBooking)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(response, BOOKING_UPDATE_GENERIC_FAILURE_ERROR_MESSAGE);
    }

    /**
     * Provides arguments for testing boundary dates in the BookingRequest.
     * Each argument includes a Booking Request object, a display name and a status code.
     */
    private Stream<Arguments> boundaryDatesProvider() {
        retrievedBooking = getFirstBookingId(authToken, 1);
        assert retrievedBooking != null;

        // Scenario 1: Checkin and Checkout are the same day
        LocalDate today = LocalDate.now();
        BookingResponse sameDayUpdate = cloneBooking(retrievedBooking);
        sameDayUpdate.setBookingdates(new BookingDates(today.toString(), today.toString()));

        // Scenario 2: Checkout date is before Checkin date (should fail)
        BookingResponse invalidDateRangeUpdate = cloneBooking(retrievedBooking);
        invalidDateRangeUpdate.setBookingdates(new BookingDates(today.toString(), today.minusDays(10).toString()));

        // Scenario 3: Booking far in the future (assuming valid)
        LocalDate futureCheckin = LocalDate.now().plusYears(generateLongWithDigits(2));
        LocalDate futureCheckout = futureCheckin.plusDays(2);
        BookingResponse futureUpdate = cloneBooking(retrievedBooking);
        futureUpdate.setBookingdates(new BookingDates(futureCheckin.toString(), futureCheckout.toString()));

        return Stream.of(
                Arguments.of(sameDayUpdate, "Same booking dates", 500),
                Arguments.of(invalidDateRangeUpdate, "Checkout date before Checkin date", 500),
                Arguments.of(futureUpdate, "Booking far in the future", 200)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("boundaryDatesProvider")
    @DisplayName("Should handle boundary dates scenarios")
    public void testUpdateWithBoundaryDates(BookingResponse boundaryDate, String displayName, Integer expectedStatusCode) {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .body(boundaryDate)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        if (expectedStatusCode == 500) {
            assertInternalServerError(response, BOOKING_UPDATE_GENERIC_FAILURE_ERROR_MESSAGE);
        } else if (expectedStatusCode == 200) {
            assertJsonBooleanValue(response,  SUCCESS_JSON_PATH, true);
        }
    }

    @Test
    @DisplayName("Should fail with 500 when request has missing fields")
    public void testUpdateWithMissingMandatoryFields() {
        String missingFieldJson = "{" +
                "  \"roomid\": 1," +
                "  \"firstname\": \"Test\"," +
                "  \"lastname\": \"User\"," +
                "  \"bookingdates\": {" +
                "  }," +
                "}";

        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .body(missingFieldJson)
                .when()
                .put(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(response, BOOKING_UPDATE_GENERIC_FAILURE_ERROR_MESSAGE);
    }
}
