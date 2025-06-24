package models.common;

import java.util.Objects;


/**
 * Represents the nested 'Contact' object within a branding response.
 */
public class Contact {

    private String name;
    private String phone;
    private String email;

    public Contact() {}

    /**
     * Constructs a new Contact object with the specified contact information.
     * @param name The name of the B&B that provides rooms available for rent.
     * @param phone The phone number associated with the contact.
     * @param email The email address associated with the contact.
     */
    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Contact(Contact other) {
        this.name = other.name;
        this.phone = other.phone;
        this.email = other.email;
    }

    // --- Getters ---
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    // --- Setters ---
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    /**
     * Provides a string representation of the Contact object.
     * @return A formatted String representing the Contact object.
     */
    @Override
    public String toString() {
        return String.format("Contact{ name='%s', phone='%s', email='%s'}", name, phone, email);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on matching name, phone and email.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact that = (Contact) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email);
    }
}
