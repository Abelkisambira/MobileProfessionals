package com.innovations.mobileprofessionals;

public class ServiceProviders {
    private String imageUrl;
    private String description;
    private String category;
    private String speciality;
    private String phoneNumber;
    private String selectedLocation;


    public ServiceProviders(String desc, String category, String phoneNumber, String selectedLocation, String imageUrl) {
    }

    public ServiceProviders(String description, String category, String speciality, String phoneNumber,String selectedLocation,String imageUrl) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.category = category;
        this.speciality = speciality;
        this.phoneNumber = phoneNumber;
        this.selectedLocation=selectedLocation;
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

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}