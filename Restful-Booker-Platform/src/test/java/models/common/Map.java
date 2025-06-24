package models.common;

import java.util.Objects;


/**
 * Represents the nested 'map' object within a branding response.
 */
public class Map {

    private float latitude;
    private float longitude;

    public Map() {}

    /**
     * Constructs a new Map object with the specified latitude and longitude.
     * @param latitude The latitude as a float.
     * @param longitude The longitude as a float.
     */
    public Map(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Map(Map other) { // <-- ADDED COPY CONSTRUCTOR
        this.latitude = other.latitude;
        this.longitude = other.longitude;
    }

    // --- Getters ---
    public float getLatitude() { return latitude; }
    public float getLongitude() { return longitude; }

    // --- Setters ---
    public void setLatitude(float latitude) { this.latitude = latitude; }
    public void setLongitude(float longitude) { this.longitude = longitude; }

    /**
     * Provides a string representation of the Map object.
     * @return A formatted String representing the Map object.
     */
    @Override
    public String toString() {
        return String.format("Map{ latitude='%s', longitude='%s'}", latitude, longitude);
    }

    /**
     * Indicates if this object is equal to another.
     * Equality is based on matching latitude and longitude.
     * @param o The object to compare with.
     * @return {@code true} if objects are equal; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Map that = (Map) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);

    }

    /**
     * Generates a hash code for this object.
     * Ensures consistent hash codes for equal objects, vital for hash-based collections.
     * @return A hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
