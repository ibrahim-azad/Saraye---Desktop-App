package models;

public class Address {
    private String addressID;
    private String street;
    private String city;
    private String country;
    private String zipCode;

    public Address(String addressID, String street, String city, String country, String zipCode) {
        this.addressID = addressID;
        this.street = street;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
    }

    // Getters and Setters
    public String getAddressID() {
        return addressID;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getFullAddress() {
        return street + ", " + city + ", " + country;
    }
}