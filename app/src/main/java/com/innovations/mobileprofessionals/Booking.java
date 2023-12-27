package com.innovations.mobileprofessionals;

public class Booking {
    private String bookingId;  // New field for the booking ID
    private String seekerName;
    private String selectedLocation;
    private String selectedDate;
    private String selectedTime;
    private String bookingStatus;

    private String professionalId;
    private String userID;
    private float employerRating;


    public Booking() {
        // Default constructor required for Firebase
    }

    public Booking(String seekerName, String selectedLocation, String selectedDate, String selectedTime, String bookingStatus,String professionalID) {
        this.seekerName = seekerName;
        this.selectedLocation = selectedLocation;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.bookingStatus = bookingStatus;
        this.professionalId =professionalID;
    }


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getSeekerName() {
        return seekerName;
    }

    public void setSeekerName(String seekerName) {
        this.seekerName = seekerName;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public float getEmployerRating() {
        return employerRating;
    }

    public void setEmployerRating(float employerRating) {
        this.employerRating = employerRating;
    }
}
