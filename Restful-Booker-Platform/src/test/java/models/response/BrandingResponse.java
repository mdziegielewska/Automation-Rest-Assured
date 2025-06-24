package models.response;

import models.common.Address;
import models.common.Contact;
import models.common.Map;

import java.util.Objects;


/**
 * Represents branding-related information including contact, address, logo, and description.
 */
public class BrandingResponse {

    private String name;
    private Map map;
    private String logoUrl;
    private String description;
    private String directions;
    private Contact contact;
    private Address address;

    public BrandingResponse() {}

    /**
     * Constructs a new BrandingResponse with all fields specified.
     * @param name The branding name.
     * @param map The map object containing location data.
     * @param logoUrl The URL to the logo image.
     * @param description The branding description text.
     * @param directions Directions to the location.
     * @param contact Contact information.
     * @param address Address details.
     */
    public BrandingResponse(String name, Map map, String logoUrl, String description,
                            String directions, Contact contact, Address address) {
        this.name = name;
        this.map = map;
        this.logoUrl = logoUrl;
        this.description = description;
        this.directions = directions;
        this.contact = contact;
        this.address = address;
    }

    public BrandingResponse(BrandingResponse other) {
        this.name = other.name;
        this.logoUrl = other.logoUrl;
        this.description = other.description;
        this.directions = other.directions;

        if (other.map != null) {
            this.map = new Map(other.map);
        } else {
            this.map = null;
        }

        if (other.contact != null) {
            this.contact = new Contact(other.contact);
        } else {
            this.contact = null;
        }

        if (other.address != null) {
            this.address = new Address(other.address);
        } else {
            this.address = null;
        }
    }

    // --- Getters ---
    public String getName() { return name; }
    public Map getMap() { return map; }
    public String getLogoUrl() { return logoUrl; }
    public String getDescription() { return description; }
    public String getDirections() { return directions; }
    public Contact getContact() { return contact; }
    public Address getAddress() { return address; }

    // --- Setters ---
    public void setName(String name) { this.name = name; }
    public void setMap(Map map) { this.map = map; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setDirections(String directions) { this.directions = directions; }
    public void setContact(Contact contact) { this.contact = contact; }
    public void setAddress(Address address) { this.address = address; }

    /**
     * Provides a string representation of the BrandingResponse object.
     * @return A formatted String representing the BrandingResponse.
     */
    @Override
    public String toString() {
        return String.format("BrandingResponse{name='%s', map=%s, logoUrl='%s', description='%s', directions='%s', contact=%s, address=%s}",
                name, map, logoUrl, description, directions, contact, address);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on all fields.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrandingResponse that = (BrandingResponse) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(map, that.map) &&
                Objects.equals(logoUrl, that.logoUrl) &&
                Objects.equals(description, that.description) &&
                Objects.equals(directions, that.directions) &&
                Objects.equals(contact, that.contact) &&
                Objects.equals(address, that.address);
    }

    /**
     * Generates a hash code for this object.
     * @return A hash code value for the BrandingResponse.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, map, logoUrl, description, directions, contact, address);
    }
}
