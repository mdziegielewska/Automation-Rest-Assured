package tests.utils;

import models.common.BookingDates;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static tests.utils.assertions.CommonAssertions.assertStringMatchesRegex;


/**
 * Utility class for common date-related validation operations.
 */
public final class DateUtils {

    // Regex pattern for YYYY-MM-DD date format
    private static final Pattern DATE_REGEX_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Random RANDOM = new Random();

    private DateUtils() {
        // hidden constructor
    }

    /**
     * Validates a date string against the YYYY-MM-DD regex pattern and then attempts to parse it
     * to ensure it represents a valid calendar date.
     * @param dateString The date string to validate (e.g., "2024-01-01").
     * @param fieldName The name of the date field (e.g., "Checkin date", "Checkout date") for error messages.
     * @param contextInfo A string providing context about the object being validated (e.g., "for booking: XYZ").
     */
    public static void validateDateFormatAndValidity(String dateString, String fieldName, String contextInfo) {
        // 1. Check format using regex
        assertStringMatchesRegex(dateString, DATE_REGEX_PATTERN.pattern(), fieldName);

        // 2. Check actual date validity
        try {
            LocalDate.parse(dateString);
        } catch (DateTimeParseException e) {
            String parseErrorMessage = String.format("%s '%s' is not a valid calendar date " +
                            "(e.g., 2024-02-30 is invalid) %s. Error: %s",
                    fieldName, dateString, contextInfo, e.getMessage());
            assertThat(parseErrorMessage, true, is(false));
        }
    }

    /**
     * Generates a {@link BookingDates} object with random check-in and check-out dates.
     * The check-in date is a random day between today and up to 5 years from now.
     * The duration between check-in and check-out is a random number of nights
     * between 3 and 7 days (inclusive).
     * @return A new {@link BookingDates} object with generated dates in "YYYY-MM-DD" format.
     */
    public static BookingDates generateRandomBookingDates() {
        LocalDate today = LocalDate.now();
        LocalDate farFuture = today.plusYears(3);

        long daysInRange = ChronoUnit.DAYS.between(today, farFuture);

        long randomDaysToAdd = RANDOM.nextLong(daysInRange + 1);
        LocalDate checkin = today.plusDays(randomDaysToAdd);

        int nights = RANDOM.nextInt(5) + 3;
        LocalDate checkout = checkin.plusDays(nights);

        return new BookingDates(checkin.toString(), checkout.toString());
    }
}
