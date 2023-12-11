package com.innovations.mobileprofessionals;

public class ServiceProviders {
    private String imageUrl;
    private String description;
    private String category;
    private String speciality;
    private String phoneNumber;
    private double latitude;
    private double longitude;

    public ServiceProviders(String desc, String category, String phoneNumber, double selectedLatitude, double selectedLongitude, String imageUrl) {
    }

    public ServiceProviders(String description, String category, String speciality, String phoneNumber, double latitude, double longitude, String imageUrl) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
        this.speciality = speciality;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}