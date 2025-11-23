package models;

import java.util.ArrayList;
import java.util.List;

public class Property {
    private String propertyID;
    private String hostID;
    private String title;
    private String description;
    private double pricePerNight;
    private int maxGuests;
    private int bedrooms;
    private int bathrooms;
    private String status;

    // ✅ COMPOSITION: Property HAS-A Address (Strong relationship)
    private Address address;

    // ✅ AGGREGATION: Property HAS-MANY Amenities (Weaker relationship)
    private List<Amenity> amenities;

    public Property(String propertyID, String hostID, String title, double price, Address address) {
        this.propertyID = propertyID;
        this.hostID = hostID;
        this.title = title;
        this.pricePerNight = price;
        this.address = address;
        this.amenities = new ArrayList<>();
    }

    public void addAmenity(Amenity amenity) {
        this.amenities.add(amenity);
    }

    // Getters and Setters
    public String getPropertyID() {
        return propertyID;
    }

    public String getHostID() {
        return hostID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Convenience methods for UI compatibility
    public String getPropertyId() {
        return propertyID;
    }

    public String getCity() {
        return address != null ? address.getCity() : "";
    }
}