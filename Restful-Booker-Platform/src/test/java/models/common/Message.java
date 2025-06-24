package models.common;

import java.util.Objects;


public class Message {

    private Integer id;
    private String name;
    private String subject;
    private Boolean read;

    public Message() {}

    /**
     * Constructs a new Message object with the specified details.
     * @param id The unique identifier for the message.
     * @param name The name of the sender of the message.
     * @param subject The subject line of the message.
     * @param read A boolean indicating whether the message has been read (true) or not (false).
     */
    public Message(Integer id, String name, String subject, Boolean read) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.read = read;
    }

    // --- Getters ---
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getSubject() { return subject; }
    public Boolean getRead() { return read; }

    // --- Setters ---
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setRead(Boolean read) { this.read = read; }

    /**
     * Provides a string representation of the Message object.
     * @return A formatted String representing the Message object.
     */
    @Override
    public String toString() {
        return String.format("Message{id=%d, name='%s', subject='%s', read=%b}", id, name, subject, read);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on matching id, name, subject and read.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(name, message.name) &&
                Objects.equals(subject, message.subject) &&
                Objects.equals(read, message.read);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, subject, read);
    }
}
