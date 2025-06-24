package models.common;

import java.util.Objects;


public class FullMessage {

    private Integer messageid;
    private String name;
    private String email;
    private String phone;
    private String subject;
    private String description;

    public FullMessage() {}

    public FullMessage(Integer messageid, String name, String email, String phone, String subject, String description) {
        this.messageid = messageid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.subject = subject;
        this.description = description;
    }

    // --- Getters ---
    public Integer getMessageid() { return messageid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }

    // --- Setters ---
    public void setMessageid(Integer messageid) { this.messageid = messageid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("FullMessage{messageid=%d, name='%s', email='%s', phone='%s', " +
                        "subject='%s', description='%s'}",
                messageid, name, email, phone, subject, description);
    }

    /**
     * Indicates if this object is equal to another.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullMessage that = (FullMessage) o;
        return Objects.equals(messageid, that.messageid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(description, that.description);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(messageid, name, email, phone, subject, description);
    }
}
