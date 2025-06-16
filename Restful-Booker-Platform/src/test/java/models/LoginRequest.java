package models;

import java.util.Objects;


/**
 * Represents a login request payload containing a username and password.
 */
public class LoginRequest {

    private String username;
    private String password;

    public LoginRequest() {}

    /**
     * Constructs a new LoginRequest with the specified username and password.
     * @param username The username for the login.
     * @param password The password for the login.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // --- Getters ---
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    // --- Setters ---
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Provides a string representation of the LoginRequest.
     * The password is masked for security in logs/debugging.
     * @return A formatted String representing the LoginRequest.
     */
    @Override
    public String toString() {
        String maskedPassword = (password == null) ? "null" : (password.isEmpty() ? "empty" : "****");
        return String.format("LoginRequest(username='%s', password='%s')",
                username, maskedPassword);
    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * The hash code is based on the username and password.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
