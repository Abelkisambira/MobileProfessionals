package com.innovations.mobileprofessionals;
// Booking.java
public class Booking {
    private String seekerName;
    private String selectedLocation;
    private String selectedDate;

    private String selectedTime;

    public Booking() {
    }

    public Booking(String seekerName, String selectedLocation, String selectedDate, String selectedTime) {
        this.seekerName = seekerName;
        this.selectedLocation = selectedLocation;
        this.selectedDate = selectedDate;
        this.selectedTime=selectedTime;
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
}
