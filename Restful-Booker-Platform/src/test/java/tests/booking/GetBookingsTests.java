package tests.booking;

import models.BookingResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static constants.ApiConstants.*;
import static org.hamcrest.Matchers.*;
import static tests.base.BaseTest.getAuthToken;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.assertions.BookingAssertions.*;


public class GetBookingsTests {

    // --- Reusable Token for Valid Scenarios ---
    private static String authToken;

    // --- Test Cases ---
    @BeforeAll
    public static void setupToken() {
        authToken = getAuthToken();
        System.out.printf("Obtained valid token for validation tests: %s", authToken);
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
    public void getBookingByExistingRoomIdTests(int roomIdToQuery) {
        System.out.printf("Running test for roomId: %d", roomIdToQuery);

        List<BookingResponse> bookings = givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .queryParam("roomid", roomIdToQuery)
                .when()
                .get(BOOKING_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("bookings", BookingResponse.class);

        assertBookingsListNotEmptyAndMinimumSize(bookings, 1);

        for (BookingResponse booking : bookings) {
            assertSingleBookingDetails(booking, roomIdToQuery);
        }
    }

    @Test
    @DisplayName("Should get an empty list of bookings for a non-existent room ID")
    public void getBookingByNonExistentRoomIdTests() {
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

        assertBookingsListIsEmpty(bookings);
    }

    @Test
    @DisplayName("Should return 400 when room ID is not provided")
    public void getBookingWithoutRoomIdTests() {
        givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .when()
                .get(BOOKING_ENDPOINT)
                .then()
                .statusCode(400)
                .body(ERROR_JSON_PATH, is(ROOM_ID_REQUIRED_ERROR_MESSAGE));
    }

    @Test
    @DisplayName("Should fail when roomid is not int")
    public void getBookingWithCharRoomIdTests() {
        givenRequest()
                .header("Cookie", String.format("token=%s", authToken))
                .queryParam("roomid", "A")
                .when()
                .get(BOOKING_ENDPOINT)
                .then()
                .statusCode(500);
    }
}
