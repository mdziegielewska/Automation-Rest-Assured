package models.common;

import java.util.Objects;


/**
 * Represents the nested 'Address' object within a branding response.
 */
public class Address {

    private String line1;
    private String line2;
    private String postTown;
    private String county;
    private String postCode;

    public Address() {}

    /**
     * Constructs a new Address object with all fields specified.
     * @param line1 The first line of the address (e.g., street and number).
     * @param line2 The second line of the address (optional).
     * @param postTown The post-town or city.
     * @param county The county name.
     * @param postCode The postal code.
     */
    public Address(String line1, String line2, String postTown, String county, String postCode) {
        this.line1 = line1;
        this.line2 = line2;
        this.postTown = postTown;
        this.county = county;
        this.postCode = postCode;
    }

    // --- Getters ---
    public String getLine1() { return line1; }
    public String getLine2() { return line2; }
    public String getPostTown() { return postTown; }
    public String getCounty() { return county; }
    public String getPostCode() { return postCode; }

    // --- Setters ---
    public void setLine1(String line1) { this.line1 = line1; }
    public void setLine2(String line2) { this.line2 = line2; }
    public void setPostTown(String postTown) { this.postTown = postTown; }
    public void setCounty(String county) { this.county = county; }
    public void setPostCode(String postCode) { this.postCode = postCode; }

    /**
     * Provides a string representation of the Address object.
     * @return A formatted String representing the Address object.
     */
    @Override
    public String toString() {
        return String.format("Address{line1='%s', line2='%s', postTown='%s', county='%s', postCode='%s'}",
                line1, line2, postTown, county, postCode);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on matching all address fields.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address that = (Address) o;
        return Objects.equals(line1, that.line1) &&
                Objects.equals(line2, that.line2) &&
                Objects.equals(postTown, that.postTown) &&
                Objects.equals(county, that.county) &&
                Objects.equals(postCode, that.postCode);
    }

    /**
     * Generates a hash code for this object.
     * @return A hash code value for the Address.
     */
    @Override
    public int hashCode() {
        return Objects.hash(line1, line2, postTown, county, postCode);
    }
}
