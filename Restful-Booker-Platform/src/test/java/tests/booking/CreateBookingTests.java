package tests.booking;

import io.restassured.response.ValidatableResponse;
import models.common.BookingDates;
import models.request.BookingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static constants.ApiConstants.*;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.TestUtils.*;
import static tests.utils.assertions.CommonAssertions.assertListContains;
import static tests.utils.assertions.CommonAssertions.assertInternalServerError;
import static tests.utils.assertions.CommonAssertions.assertSuccessfulResponse;


public class CreateBookingTests {

    // --- Test Cases ---
    /**
     * Provides a stream of Arguments, each containing a fully built {@link BookingRequest} object.
     * @return A Stream of Arguments, where each argument is a {@link BookingRequest} object.
     */
    private static Stream<Arguments> bookingDataProvider() {
        return Stream.of(
                Arguments.of(buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1")),
                Arguments.of(buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "2")),
                Arguments.of(buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "3"))
        );
    }

    @ParameterizedTest(name = "Booking Room ID: {index}")
    @MethodSource("bookingDataProvider")
    @DisplayName("Should create a booking correctly")
    public void testSuccessfulBookingCreation(BookingRequest bookingRequest) {
        ValidatableResponse response = givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then();

        assertSuccessfulResponse(response);
    }

    @Test
    @DisplayName("Should not book a room with the same date twice")
    public void testBookingSameRoomSameDates() {
         BookingRequest bookingRequest = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");

        ValidatableResponse response = givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then();

        assertSuccessfulResponse(response);
        
        bookingRequest.setFirstname("New");
        bookingRequest.setLastname("User");
        bookingRequest.setEmail("new.user@gmail.com");
        bookingRequest.setPhone("09876543210");

        ValidatableResponse anotherResponse = givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then();

        assertInternalServerError(anotherResponse);
    }

    @Test
    @DisplayName("Should return 500 for malformed JSON request body")
    public void testCreationWithMalformedJson() {
        String malformedJson = "{" +
                "  \"roomid\": 1," +
                "  \"firstname\": \"Test\"" + // Missing comma
                "  \"lastname\": \"User\"," +
                "  \"depositpaid\": false," +
                "  \"bookingdates\": {" +
                "    \"checkin\": \"2025-07-23\"," +
                "    \"checkout\": \"2025-07-24\"" +
                "  }," +
                "  \"email\": \"test.user@gmail.pl\"," +
                "  \"phone\": \"12345678900\"" +
                "}";

        ValidatableResponse response = givenRequest()
                .body(malformedJson)
                .when()
                .post(BOOKING_ENDPOINT)
                .then();

        assertInternalServerError(response);
    }

    /**
     * Provides arguments for testing invalid fields in the BookingRequest.
     */
    private static Stream<Arguments> invalidFieldsProvider() {
        // Scenario 1: Null roomid
        BookingRequest nullRoomId = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        nullRoomId.setRoomid(null);

        // Scenario 2: Short firstname
        BookingRequest shortFirstname = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        shortFirstname.setFirstname("Jo");

        // Scenario 3: Empty lastname string
        BookingRequest emptyLastname = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        emptyLastname.setLastname("");

        // Scenario 4: Empty email
        BookingRequest emptyEmail = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        emptyEmail.setEmail("");

        // Scenario 5: Null phone
        BookingRequest nullPhone = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        nullPhone.setPhone(null);

        // Scenario 6: Null checkout date
        BookingRequest nullCheckoutDate = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        nullCheckoutDate.setBookingdates(new BookingDates(nullCheckoutDate.getBookingdates().getCheckin(), null));

        return Stream.of(
                Arguments.of(nullRoomId, "Null room id", ROOM_ID_MIN_VALUE_ERROR_MESSAGE),
                Arguments.of(shortFirstname, "Short firstname", FIRST_NAME_SIZE_ERROR_MESSAGE),
                Arguments.of(emptyLastname, "Empty lastname", LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(emptyEmail, "Empty email", FIELD_MUST_NOT_BE_EMPTY_ERROR_MESSAGE),
                Arguments.of(nullPhone, "Null phone", BOOKING_CREATION_GENERIC_FAILURE_ERROR_MESSAGE),
                Arguments.of(nullCheckoutDate, "Null checkout date", FIELD_MUST_NOT_BE_NULL_ERROR_MESSAGE)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("invalidFieldsProvider")
    @DisplayName("Should return errors for request with invalid fields")
    public void testBookingWithInvalidFields(BookingRequest bookingRequest, String displayName, String expectedError) {
        List<String> errors = givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(400)
                .extract().jsonPath().getList(ERRORS_JSON_PATH, String.class);

        assertListContains(errors, expectedError);
    }

    @Test
    @DisplayName("Should handle booking non-existent room id")
    public void testBookingForNonExistentRoomId() {
        String nonExistentRoomId = generate10DigitNumericString();
        BookingRequest bookingForNonExistentRoom = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, nonExistentRoomId);

        List<String> errors = givenRequest()
                .body(bookingForNonExistentRoom)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(400)
                .extract()
                .jsonPath().getList(ERRORS_JSON_PATH, String.class);

        assertListContains(errors, BOOKING_CREATION_GENERIC_FAILURE_ERROR_MESSAGE);
    }

    /**
     * Provides arguments for testing boundary dates in the BookingRequest.
     * Each argument includes a Booking Request object, a display name and a status code.
     */
    private static Stream<Arguments> boundaryDatesProvider() {
        // Scenario 1: Checkin and Checkout are the same day
        LocalDate today = LocalDate.now();
        BookingRequest sameDayBooking = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        sameDayBooking.setBookingdates(new BookingDates(today.toString(), today.toString()));

        // Scenario 2: Checkout date is before Checkin date
        BookingRequest invalidDateRangeBooking = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        invalidDateRangeBooking.setBookingdates(new BookingDates(today.toString(), today.minusDays(10).toString()));

        // Scenario 3: Booking far in the future
        LocalDate futureCheckin = LocalDate.now().plusYears(generateLongWithDigits(2));
        LocalDate futureCheckout = futureCheckin.plusDays(2);
        BookingRequest futureBooking = buildBookingRequest(CORRECT_BOOKING_CREATION_PATH, "1");
        futureBooking.setBookingdates(new BookingDates(futureCheckin.toString(), futureCheckout.toString()));

        return Stream.of(
                Arguments.of(sameDayBooking, "Same booking dates", 500),
                Arguments.of(invalidDateRangeBooking, "Checkout date before Checkin date", 500),
                Arguments.of(futureBooking, "Booking far in the future", 200)
        );
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("boundaryDatesProvider")
    @DisplayName("Should handle boundary dates")
    public void testBookingHandlesBoundaryDates(BookingRequest boundaryDate, String displayName, Integer expectedStatusCode) {
        ValidatableResponse response = givenRequest()
                .body(boundaryDate)
                .when()
                .post(BOOKING_ENDPOINT)
                .then();

        if (expectedStatusCode == 500) {
            assertInternalServerError(response);
        } else if (expectedStatusCode == 200) {
            assertSuccessfulResponse(response);
        }
    }

    @Test
    @DisplayName("Should return 500 when request has missing fields")
    public void testBookingWithMissingMandatoryFields()  {
        String missingFieldJson = "{" +
                "  \"roomid\": 1," +
                "  \"firstname\": \"Test\"," +
                "  \"lastname\": \"User\"," +
                "  \"bookingdates\": {" +
                "    \"checkin\": \"2025-07-23\"," +
                "    \"checkout\": \"2025-07-24\"" +
                "  }," +
                "}";

        ValidatableResponse response = givenRequest()
                .body(missingFieldJson)
                .when()
                .post(BOOKING_ENDPOINT)
                .then();

        assertInternalServerError(response);
    }
}
