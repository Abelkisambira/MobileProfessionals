package com.innovations.mobileprofessionals;

import com.google.firebase.Timestamp;

public class ChatEmployer {
    private String employerID,professionalID;
    private String employerName;
    private String employerProfilePicUrl;
    private String message;
    private boolean sentByProfessional;
    private  Timestamp timestamp;

    public ChatEmployer() {
        // Default constructor required for Firebase
    }

    public ChatEmployer(String employerId, String employerName, String employerProfilePicUrl, String message, boolean sentByProfessional) {
        this.employerID = employerId;
        this.employerName = employerName;
        this.employerProfilePicUrl = employerProfilePicUrl;
        this.message = message;
        this.sentByProfessional=sentByProfessional;
//        this.timestamp = timestamp;
    }


    public String getEmployerID() {
        return employerID;
    }

    public void setEmployerID(String employerID) {
        this.employerID = employerID;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getEmployerProfilePicUrl() {
        return employerProfilePicUrl;
    }

    public void setEmployerProfilePicUrl(String employerProfilePicUrl) {
        this.employerProfilePicUrl = employerProfilePicUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isSentByProfessional() {


        return sentByProfessional;
    }

    public void setSentByProfessional(boolean sentByProfessional) {
        this.sentByProfessional = sentByProfessional;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
