package models;

/**
 * Represents a login request payload containing a username and password.
 */
public class LoginRequest {
    private String username;
    private String password;

    public LoginRequest() {}
    /**
     * Constructs a new LoginRequest with the specified username and password.
     * This is the primary constructor for creating instances with initial data.
     * @param username The username for the login.
     * @param password The password for the login.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Retrieves the username from the login request.
     * @return The username as a String.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the login request.
     * @param username The String value to set as the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the password from the login request.
     * @return The password as a String.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the login request.
     * @param password The String value to set as the password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Provides a string representation of the LoginRequest object.
     * This method is overridden for debugging and logging purposes,
     * carefully masking the password for security reasons.
     * @return A formatted String representing the LoginRequest,
     * with the password obfuscated.
     */
    @Override
    public String toString() {
        // Obfuscate password for security in logs/debugging
        String maskedPassword = (password == null) ? "null" : (password.isEmpty() ? "empty" : "****");
        return String.format("LoginRequest(username='%s', password='%s')",
                username, maskedPassword);
    }
}
