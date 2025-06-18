package models.request;

import models.common.BookingDates;

import java.util.Objects;


public class BookingRequest {

    private String roomid;
    private String firstname;
    private String lastname;
    private Boolean depositpaid;
    private String email;
    private String phone;
    private BookingDates bookingdates;

    public BookingRequest() {}

    /**
     * Constructs a new BookingRequest with all specified booking details.
     * @param roomid The ID of the room associated with this booking.
     * @param firstname The first name of the person who made the booking.
     * @param lastname The last name of the person who made the booking.
     * @param depositpaid Indicates whether the deposit for the booking has been paid.
     * @param bookingdates The nested object containing check-in and check-out dates.
     * @param email The email of the person who made the booking.
     * @param phone The phone number of the person who made the booking.
     */
    public BookingRequest(String roomid, String firstname, String lastname, Boolean depositpaid, String email,
                          String phone, BookingDates bookingdates) {
        this.roomid = roomid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
        this.email = email;
        this.phone = phone;
    }

    // --- Getters ---
    public String getRoomid() { return roomid; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public Boolean getDepositpaid() { return depositpaid; }
    public BookingDates getBookingdates() { return bookingdates; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // --- Setters ---
    public void setRoomid(String roomid) { this.roomid = roomid; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public void setDepositpaid(Boolean depositpaid) { this.depositpaid = depositpaid; }
    public void setBookingdates(BookingDates bookingdates) { this.bookingdates = bookingdates; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Provides a string representation of the BookingRequest object.
     * @return A formatted String representing the BookingRequest object.
     */
    @Override
    public String toString() {
        return String.format("BookingRequest{" +
                        "roomid='%s', firstname='%s', lastname='%s', depositpaid=%b, bookingdates=%s," +
                        "email='%s', phone='%s'}",
                roomid, firstname, lastname, depositpaid, bookingdates, email, phone);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Equality is based on matching all booking detail fields.
     * @param o The reference object with which to compare.
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRequest that = (BookingRequest) o;
        return Objects.equals(roomid, that.roomid) &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(depositpaid, that.depositpaid) &&
                Objects.equals(bookingdates, that.bookingdates) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * The hash code is based on all booking fields.
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(roomid, firstname, lastname, depositpaid, bookingdates, email, phone);
    }
}
