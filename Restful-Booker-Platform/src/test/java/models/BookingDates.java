package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;


/**
 * Represents the nested 'bookingdates' object within a booking response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDates {

    private String checkin;
    private String checkout;

    public BookingDates() {}

    /**
     * Constructs a new BookingDates object with the specified check-in and check-out dates.
     * @param checkin The check-in date as a String (expected format: YYYY-MM-DD).
     * @param checkout The check-out date as a String (expected format: YYYY-MM-DD).
     */
    public BookingDates(String checkin, String checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    // --- Getters ---
    public String getCheckin() { return checkin; }
    public String getCheckout() { return checkout; }

    // --- Setters ---
    public void setCheckin(String checkin) { this.checkin = checkin; }
    public void setCheckout(String checkout) { this.checkout = checkout; }

    /**
     * Provides a string representation of the BookingDates object.
     * @return A formatted String representing the BookingDates object.
     */
    @Override
    public String toString() {
        return String.format("BookingDates{ checkin='%s', checkout='%s'}", checkin, checkout);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on matching check-in and check-out dates.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDates that = (BookingDates) o;
        return Objects.equals(checkin, that.checkin) &&
                Objects.equals(checkout, that.checkout);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(checkin, checkout);
    }
}