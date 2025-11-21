package models;

/**
 * Property Model - Temporary placeholder for UI
 * Will be replaced by Ibrahim's business logic implementation
 */
public class Property {
    private int propertyId;
    private int hostId;
    private String title;
    private String description;
    private String address;
    private String city;
    private double pricePerNight;
    private int maxGuests;
    private int bedrooms;
    private int bathrooms;
    private String amenities;
    private String status; // "available", "unavailable"

    // Constructors
    public Property() {}

    public Property(int propertyId, int hostId, String title, String description,
                    String address, String city, double pricePerNight, int maxGuests,
                    int bedrooms, int bathrooms, String amenities, String status) {
        this.propertyId = propertyId;
        this.hostId = hostId;
        this.title = title;
        this.description = description;
        this.address = address;
        this.city = city;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.amenities = amenities;
        this.status = status;
    }

    // Getters and Setters
    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
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

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Property{" +
                "propertyId=" + propertyId +
                ", title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", pricePerNight=" + pricePerNight +
                '}';
    }
}
