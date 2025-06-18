package models.response;

import models.common.BookingDates;

import java.util.Objects;


public class BookingResponse {

    private Integer bookingid;
    private Integer roomid;
    private String firstname;
    private String lastname;
    private Boolean depositpaid;
    private BookingDates bookingdates; // Nested object

    public BookingResponse() {}

    /**
     * Constructs a new BookingResponse with all specified booking details.
     * @param bookingid The unique identifier for the booking.
     * @param roomid The ID of the room associated with this booking.
     * @param firstname The first name of the person who made the booking.
     * @param lastname The last name of the person who made the booking.
     * @param depositpaid Indicates whether the deposit for the booking has been paid.
     * @param bookingdates The nested object containing check-in and check-out dates.
     */
    public BookingResponse(Integer bookingid, Integer roomid, String firstname, String lastname,
                           Boolean depositpaid, BookingDates bookingdates) {
        this.bookingid = bookingid;
        this.roomid = roomid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
    }

    // --- Getters ---
    public Integer getBookingid() { return bookingid; }
    public Integer getRoomid() { return roomid; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public Boolean getDepositpaid() { return depositpaid; }
    public BookingDates getBookingdates() { return bookingdates; }

    // --- Setters ---
    public void setBookingid(Integer bookingid) { this.bookingid = bookingid; }
    public void setRoomid(Integer roomid) { this.roomid = roomid; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setDepositpaid(Boolean depositpaid) { this.depositpaid = depositpaid; }
    public void setBookingdates(BookingDates bookingdates) { this.bookingdates = bookingdates; }

    /**
     * Provides a string representation of the BookingResponse object.
     * @return A formatted String representing the BookingResponse.
     */
    @Override
    public String toString() {
        return String.format("BookingResponse{ bookingid=%d, roomid=%d, firstname='%s', lastname='%s', depositpaid=%b" +
                        ", bookingdates=%s }", bookingid, roomid, firstname, lastname, depositpaid, bookingdates);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on matching all booking detail fields.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingResponse that = (BookingResponse) o;
        return Objects.equals(bookingid, that.bookingid) &&
                Objects.equals(roomid, that.roomid) &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(depositpaid, that.depositpaid) &&
                Objects.equals(bookingdates, that.bookingdates);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * The hash code is based on all booking fields.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(bookingid, roomid, firstname, lastname, depositpaid, bookingdates);
    }
}
