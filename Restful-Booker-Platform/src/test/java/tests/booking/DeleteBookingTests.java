package tests.booking;

import io.restassured.response.ValidatableResponse;
import models.response.BookingResponse;
import org.junit.jupiter.api.*;

import static constants.ApiConstants.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.TestUtils.getFirstBookingId;
import static tests.utils.assertions.CommonAssertions.*;


public class DeleteBookingTests {

    // --- Reusable Token for Valid Scenarios ---
    private static String authToken;
    private static BookingResponse retrievedBooking;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken() {
        authToken = getAuthToken();
    }

    @BeforeEach
    public void setup() {
        // to do: add booking creation in setup to make tests more independent

        retrievedBooking = getFirstBookingId(authToken, 1);
        assertNotNullOrBlank(retrievedBooking, "Booking Response");
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    public void testDeletionWithoutAuthentication() {
        ValidatableResponse response = givenRequest()
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .when()
                .delete(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertFailedResponse(response, 401, AUTHENTICATION_REQUIRED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when token is invalid")
    public void testDeletionWithInvalidToken() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", "token=test123")
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .when()
                .delete(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(response, BOOKING_DELETION_GENERIC_FAILURE_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should delete booking successfully")
    public void testSuccessfulBookingDeletion() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .when()
                .delete(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);
    }

    @Test
    @DisplayName("Should return 500 when delete booking with the same booking id")
    public void testBookingDeletionSameBookingId() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .when()
                .delete(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertJsonBooleanValue(response, SUCCESS_JSON_PATH, true);

        ValidatableResponse secondAttempt = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", retrievedBooking.getBookingid())
                .when()
                .delete(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(secondAttempt, BOOKING_DELETION_GENERIC_FAILURE_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when delete booking with non existent booking id")
    public void testBookingDeletionWithNonExistentBookingId() {
        ValidatableResponse secondAttempt = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .pathParams("bookingId", "AAA")
                .when()
                .delete(String.format("%s/%s", BOOKING_ENDPOINT, "{bookingId}"))
                .then();

        assertInternalServerError(secondAttempt, BOOKING_DELETION_GENERIC_FAILURE_ERROR_MESSAGE);
    }
}
