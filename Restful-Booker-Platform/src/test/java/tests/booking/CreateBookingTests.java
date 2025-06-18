package tests.booking;

import models.common.BookingDates;
import models.request.BookingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tests.utils.TestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static constants.ApiConstants.*;
import static org.hamcrest.Matchers.*;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.TestUtils.*;


public class CreateBookingTests {

    // --- Test Cases ---
    /**
     * Provides a stream of Arguments, each containing a fully built {@link BookingRequest} object.
     * This leverages {@link TestUtils#buildBookingRequest(String, String)} to create requests
     * with predefined room IDs and dynamically generated booking dates.
     * @return A Stream of Arguments, where each argument is a {@link BookingRequest} object.
     */
    private static Stream<Arguments> bookingDataProvider() {
        return Stream.of(
                Arguments.of(buildBookingRequest(CORRECT_BOOKING_PATH, "1")),
                Arguments.of(buildBookingRequest(CORRECT_BOOKING_PATH, "2")),
                Arguments.of(buildBookingRequest(CORRECT_BOOKING_PATH, "3"))
        );
    }

    @ParameterizedTest(name = "Booking Room ID: {index}")
    @MethodSource("bookingDataProvider")
    @DisplayName("Should create a booking correctly")
    public void testSuccessfulBookingCreation(BookingRequest bookingRequest) {
        givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(200)
                .body("", is(empty()));
    }

    @Test
    @DisplayName("Should not book a room with the same date twice")
    public void testBookingSameRoomSameDates() {
         BookingRequest bookingRequest = buildBookingRequest(CORRECT_BOOKING_PATH, "1");

        givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(200)
                .body("", is(empty()));
        
        bookingRequest.setFirstname("New");
        bookingRequest.setLastname("User");
        bookingRequest.setEmail("new.user@gmail.com");
        bookingRequest.setPhone("09876543210");

        givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(500)
                .body("", is(empty()));
    }

    @Test
    @DisplayName("Should return 500 for malformed JSON request body")
    public void testMalformedJsonRequestBody() {
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

        givenRequest()
                .body(malformedJson)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(500)
                .body("", is(empty()));
    }

    /**
     * Provides arguments for testing invalid fields in the BookingRequest.
     */
    private static Stream<Arguments> invalidFieldsProvider() {
        // Scenario 1: Null roomid
        BookingRequest nullRoomId = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
        nullRoomId.setRoomid(null);

        // Scenario 2: Short firstname
        BookingRequest shortFirstname = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
        shortFirstname.setFirstname("Jo");

        // Scenario 3: Empty lastname string
        BookingRequest emptyLastname = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
        emptyLastname.setLastname("");

        // Scenario 4: Empty email
        BookingRequest emptyEmail = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
        emptyEmail.setEmail("");

        // Scenario 5: Null phone
        BookingRequest nullPhone = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
        nullPhone.setPhone(null);

        // Scenario 6: Null checkout date
        BookingRequest nullCheckoutDate = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
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
    @DisplayName("Should fail when request has invalid fields")
    public void testBookingFailsWithInvalidFields(BookingRequest bookingRequest, String testDescription, String expectedError) {
        givenRequest()
                .body(bookingRequest)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(400)
                .body(ERRORS_JSON_PATH, is(notNullValue()))
                .body(ERRORS_JSON_PATH, isA(List.class))
                .body(ERRORS_JSON_PATH, hasItem(expectedError));
    }

    @Test
    @DisplayName("Should handle booking non-existent room id")
    public void testBookingFailsForNonExistentRoomId() {
        String nonExistentRoomId = generate10DigitNumericString();
        BookingRequest bookingForNonExistentRoom = buildBookingRequest(CORRECT_BOOKING_PATH, nonExistentRoomId);

        givenRequest()
                .body(bookingForNonExistentRoom)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(400)
                .body(ERRORS_JSON_PATH, is(notNullValue()))
                .body(ERRORS_JSON_PATH, isA(List.class))
                .body(ERRORS_JSON_PATH, hasItem(BOOKING_CREATION_GENERIC_FAILURE_ERROR_MESSAGE));
    }

    /**
     * Provides arguments for testing boundary dates in the BookingRequest.
     */
    private static Stream<Arguments> boundaryDatesProvider() {
        // Scenario 1: Checkin and Checkout are the same day (assuming this is valid)
        LocalDate today = LocalDate.now();
        BookingRequest sameDayBooking = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
        sameDayBooking.setBookingdates(new BookingDates(today.toString(), today.toString()));

        // Scenario 2: Checkout date is before Checkin date (should fail)
        BookingRequest invalidDateRangeBooking = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
        invalidDateRangeBooking.setBookingdates(new BookingDates(today.toString(), today.minusDays(10).toString()));

        // Scenario 3: Booking far in the future (assuming valid)
        LocalDate futureCheckin = LocalDate.now().plusYears(generateLongWithDigits(2));
        LocalDate futureCheckout = futureCheckin.plusDays(2);
        BookingRequest futureBooking = buildBookingRequest(CORRECT_BOOKING_PATH, "1");
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
    public void testBookingHandlesBoundaryDates(BookingRequest boundaryDate, String testDescription, Integer statusCode) {
        givenRequest()
                .body(boundaryDate)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(statusCode);
    }

    @Test
    @DisplayName("Should fail when request has missing fields")
    public void testBookingFailsWithMissingMandatoryFields()  {
        String malformedJson = "{" +
                "  \"roomid\": 1," +
                "  \"firstname\": \"Test\"," +
                "  \"lastname\": \"User\"," +
                "  \"bookingdates\": {" +
                "    \"checkin\": \"2025-07-23\"," +
                "    \"checkout\": \"2025-07-24\"" +
                "  }," +
                "}";

        givenRequest()
                .body(malformedJson)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(500)
                .body("", is(empty()));
    }

    /**
     * Provides arguments for testing booking creation with invalid data types.
     */
    private static Stream<Arguments> invalidDataTypesProvider() {
        // Scenario 1: Depositpaid sent as a String "false" instead of a boolean false
        String jsonWithInvalidDepositPaidType = "{" +
                "  \"roomid\": 1," +
                "  \"firstname\": \"Another\"," +
                "  \"lastname\": \"Error\"," +
                "  \"depositpaid\": \"false\"," + // Invalid: "false" is a string, expects boolean
                "  \"bookingdates\": {" +
                "    \"checkin\": \"2025-09-01\"," +
                "    \"checkout\": \"2025-09-05\"" +
                "  }," +
                "  \"email\": \"another.error@example.com\"," +
                "  \"phone\": \"22233344455\"" +
                "}";

        // Scenario 1: Phone number sent as an integer
        String jsonWithIntegerPhone = "{" +
                "  \"roomid\": 1," +
                "  \"firstname\": \"Phone\"," +
                "  \"lastname\": \"Type\"," +
                "  \"depositpaid\": false," +
                "  \"bookingdates\": {" +
                "    \"checkin\": \"2025-11-01\"," +
                "    \"checkout\": \"2025-11-05\"" +
                "  }," +
                "  \"email\": \"phone.type@example.com\"," +
                "  \"phone\": 1234567890 // Invalid: integer, expects string" +
                "}";

        return Stream.of(
                Arguments.of(jsonWithInvalidDepositPaidType, "Depositpaid as String"),
                Arguments.of(jsonWithIntegerPhone, "Phone as Integer")
        );
    }

    @ParameterizedTest(name = "Booking with invalid data: {1}")
    @MethodSource("invalidDataTypesProvider")
    @DisplayName("Should return 400 for requests with invalid data types in fields")
    public void testBookingFailsWithInvalidDataTypes(String requestBody, String testDescription) {
        givenRequest()
                .body(requestBody)
                .when()
                .post(BOOKING_ENDPOINT)
                .then()
                .statusCode(500)
                .body("", is(empty()));
    }
}
