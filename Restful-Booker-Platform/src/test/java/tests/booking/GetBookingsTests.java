package tests.booking;

import io.restassured.response.ValidatableResponse;
import models.common.BookingDates;
import models.response.BookingResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static constants.ApiConstants.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.DateUtils.validateDateFormatAndValidity;
import static tests.utils.assertions.BookingAssertions.*;
import static tests.utils.assertions.CommonAssertions.*;


public class GetBookingsTests {

    // --- Reusable Token for Valid Scenarios ---
    private static String authToken;
    private static List<BookingResponse> bookingsForRoom1;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken() {
        authToken = getAuthToken();

        // to do: add booking creation in setup to make tests more independent

        bookingsForRoom1 = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .queryParam("roomid", 1)
                .when()
                .get(BOOKING_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("bookings", BookingResponse.class);

        assertBookingsListNotEmptyAndMinimumSize(bookingsForRoom1, 1);
    }

    /**
     * Provides a stream of specific room IDs for parameterized tests.
     * Will always provide room IDs 1, 2, and 3.
     * @return Stream of Arguments, each containing a single integer (room ID).
     */
    private static Stream<Arguments> roomIdsProvider() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(2),
                Arguments.of(3)
        );
    }

    @ParameterizedTest(name = "Room ID: {0}")
    @MethodSource("roomIdsProvider")
    @DisplayName("Should get a list of bookings for a specific room ID and verify content of each")
    public void testGetBookingsByExistingRoomId(int roomIdToQuery) {
        List<BookingResponse> currentBookings;

        if (roomIdToQuery == 1) {
            currentBookings = bookingsForRoom1;
        } else {
           currentBookings = givenRequest()
                    .header("Cookie", String.format("token=%s", authToken))
                    .queryParam("roomid", roomIdToQuery)
                    .when()
                    .get(BOOKING_ENDPOINT)
                    .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath().getList("bookings", BookingResponse.class);
        }

        assertBookingsListNotEmptyAndMinimumSize(currentBookings, 1);

        for (BookingResponse booking : currentBookings) {
            assertSingleBookingDetails(booking, roomIdToQuery);
        }
    }

    @Test
    @DisplayName("Should verify booking dates fields format and validity")
    public void testBookingDateFieldsFormatAndValidity() {
        for (BookingResponse booking : bookingsForRoom1) {
            BookingDates bookingDates = booking.getBookingdates();
            assertNotNullOrBlank(bookingDates.getCheckin(), "checkin");
            assertNotNullOrBlank(bookingDates.getCheckin(), "checkout");

            // The specific date format/validity checks
            validateDateFormatAndValidity(bookingDates.getCheckin(), "Checkin date",
                    java.text.MessageFormat.format("for booking: {0}", booking.toString()));
            validateDateFormatAndValidity(bookingDates.getCheckout(), "Checkout date",
                    java.text.MessageFormat.format("for booking: {0}", booking.toString()));
        }
    }
    @Test
    @DisplayName("Should get an empty list of bookings for a non-existent room ID")
    public void testGetBookingsByNonExistentRoomId() {
        int nonExistentRoomId = 99999;

        List<BookingResponse> bookings = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .queryParam("roomid", nonExistentRoomId)
                .when()
                .get(BOOKING_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("bookings", BookingResponse.class);

        assertIsEmpty(bookings, "Bookings");
    }

    @Test
    @DisplayName("Should return 400 when room ID is not provided")
    public void testGetBookingsWithoutRoomId() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .when()
                .get(BOOKING_ENDPOINT)
                .then();

        assertFailedResponse(response, 400, ROOM_ID_REQUIRED_ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Should return 500 when roomid is not integer")
    public void testGetBookingsWithNonIntegerRoomId() {
        ValidatableResponse response = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .queryParam("roomid", "A")
                .when()
                .get(BOOKING_ENDPOINT)
                .then();

        assertInternalServerError(response);
    }

    @Test
    @DisplayName("Should return 401 without authentication")
    public void testGetBookingsWithNoAuthentication()  {
        ValidatableResponse response = givenRequest()
                .queryParam("roomid", "1")
                .when()
                .get(BOOKING_ENDPOINT)
                .then();

        assertFailedResponse(response, 401, AUTHENTICATION_REQUIRED_ERROR_MESSAGE);
    }
}
