package tests.utils.assertions;

import models.common.BookingDates;
import models.response.BookingResponse;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static tests.utils.DateUtils.validateDateFormatAndValidity;
import static tests.utils.assertions.CommonAssertions.*;


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
        List<Map.Entry<Object, String>> bookingElements = Arrays.asList(
                new AbstractMap.SimpleEntry<>(booking.getBookingid(), "Bookingid"),
                new AbstractMap.SimpleEntry<>(booking.getRoomid(), "Roomid"),
                new AbstractMap.SimpleEntry<>(booking.getFirstname(), "Firstname"),
                new AbstractMap.SimpleEntry<>(booking.getLastname(), "Lastname"),
                new AbstractMap.SimpleEntry<>(booking.getDepositpaid(), "Depositpaid"),
                new AbstractMap.SimpleEntry<>(booking.getBookingdates(), "Bookingdates")
        );

        for (Map.Entry<Object, String> entry : bookingElements) {
            assertNotNullOrBlank(entry.getKey(), entry.getValue());
        }

        if (expectedRoomId != null) {
            assertThat(java.text.MessageFormat.format("Room ID of booking {0} should match the expected room ID {1}",
                            booking.getBookingid(), expectedRoomId),
                    booking.getRoomid(), equalTo(expectedRoomId));
        }
    }


    /**
     * Asserts that a list of BookingResponse objects is not null, is not empty, and meets a minimum expected size.
     * @param bookings The list of {@link BookingResponse} objects.
     * @param minimumExpectedSize The minimum number of bookings expected in the list.
     */
    public static void assertBookingsListNotEmptyAndMinimumSize(List<BookingResponse> bookings, int minimumExpectedSize) {
        assertNotNullOrBlank(bookings, "Bookings");
        assertThat("Bookings list should not be empty", bookings, is(not(empty())));
        assertThat(java.text.MessageFormat.format("Bookings list size should be at least {0}",
                minimumExpectedSize), bookings.size(), greaterThanOrEqualTo(minimumExpectedSize));
    }

    /**
     * Asserts that a list of BookingResponse objects is not null and is empty.
     * @param bookings The list of {@link BookingResponse} objects.
     */
    public static void assertBookingsListIsEmpty(List<BookingResponse> bookings) {
        assertNotNullOrBlank(bookings, "Bookings");
        assertThat("Bookings list should be empty", bookings, is(empty()));
        assertThat("Bookings list size should be 0", bookings.size(), equalTo(0));
    }
}
