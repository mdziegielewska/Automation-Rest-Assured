package tests.utils.assertions;

import models.common.BookingDates;
import models.response.BookingResponse;
import tests.utils.DateUtils;

import java.util.List;

import static constants.ApiConstants.SUCCESS_JSON_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Utility class containing assertion methods specifically for BookingResponse objects.
 */
public final class BookingAssertions {

    private BookingAssertions() {
        // hidden constructor
    }

    /**
     * Asserts the common details and structure of a single BookingResponse object.
     * @param booking The {@link BookingResponse} object to assert.
     * @param expectedRoomId The room ID that the booking's roomid should match. Pass {@code null}
     *                       if this check is not applicable.
     */
    public static void assertSingleBookingDetails(BookingResponse booking, Integer expectedRoomId) {
        assertThat("Booking ID should not be null", booking.getBookingid(), is(notNullValue()));
        assertThat("Room ID should not be null", booking.getRoomid(), is(notNullValue()));
        assertThat("First name should not be null", booking.getFirstname(), is(notNullValue()));
        assertThat("Last name should not be null", booking.getLastname(), is(notNullValue()));
        assertThat("Deposit paid should not be null", booking.getDepositpaid(), is(notNullValue()));
        assertThat("Booking dates object should not be null", booking.getBookingdates(), is(notNullValue()));

        if (expectedRoomId != null) {
            assertThat(java.text.MessageFormat.format("Room ID of booking {0} should match the expected room ID {1}",
                            booking.getBookingid(), expectedRoomId),
                    booking.getRoomid(), equalTo(expectedRoomId));
        }

        BookingDates bookingDates = booking.getBookingdates();
        assertThat("Checkin date string should not be null", bookingDates.getCheckin(), is(notNullValue()));
        assertThat("Checkout date string should not be null", bookingDates.getCheckout(), is(notNullValue()));

        DateUtils.validateDateFormatAndValidity(bookingDates.getCheckin(), "Checkin date",
                java.text.MessageFormat.format("for booking: {0}", booking.toString()));
        DateUtils.validateDateFormatAndValidity(bookingDates.getCheckout(), "Checkout date",
                java.text.MessageFormat.format("for booking: {0}", booking.toString()));
    }

    /**
     * Asserts that a list of BookingResponse objects is not null, is not empty, and meets a minimum expected size.
     * @param bookings The list of {@link BookingResponse} objects.
     * @param minimumExpectedSize The minimum number of bookings expected in the list.
     */
    public static void assertBookingsListNotEmptyAndMinimumSize(List<BookingResponse> bookings, int minimumExpectedSize) {
        assertThat("Bookings list should not be null", bookings, is(notNullValue()));
        assertThat("Bookings list should not be empty", bookings, is(not(empty())));
        assertThat(java.text.MessageFormat.format("Bookings list size should be at least {0}",
                minimumExpectedSize), bookings.size(), greaterThanOrEqualTo(minimumExpectedSize));
    }

    /**
     * Asserts that a list of BookingResponse objects is not null and is empty.
     * @param bookings The list of {@link BookingResponse} objects.
     */
    public static void assertBookingsListIsEmpty(List<BookingResponse> bookings) {
        assertThat("Bookings list should not be null", bookings, is(notNullValue()));
        assertThat("Bookings list should be empty", bookings, is(empty()));
        assertThat("Bookings list size should be 0", bookings.size(), equalTo(0));
    }
}
