package models.response;

import models.common.Message;

import java.util.List;
import java.util.Objects;


public class MessageResponse {

    private List<Message> messages;

    public MessageResponse() {}

    /**
     * Constructs a new MessageResponse object, encapsulating a list of Message objects.
     * This is typically used when deserializing a response that contains multiple messages.
     * @param messages A list of Message objects.
     */
    public MessageResponse(List<Message> messages) {
        this.messages = messages;
    }

    // --- Getter ---
    public List<Message> getMessages() { return messages; }

    // --- Setter ---
    public void setMessages(List<Message> messages) { this.messages = messages; }

    /**
     * Provides a string representation of the MessageResponse object.
     * @return A formatted String representing the MessageResponse object, including its list of messages.
     */
    @Override
    public String toString() {
        return String.format("MessageResponse{messages=%s}", messages);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on message.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageResponse that = (MessageResponse) o;
        return Objects.equals(messages, that.messages);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(messages);
    }
}
