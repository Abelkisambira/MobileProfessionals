package com.innovations.mobileprofessionals;

import java.util.Date;

public class ChatMessage {
    private String message;
    private String professionalName,employerName;
    private String professionalId,employerId;
    private boolean isSentByProfessional;

    private Date timestamp;

    public ChatMessage() {
        // Default constructor required for Firebase
    }

    public ChatMessage(String message, String professionalName,String employerName, String professionalId,String employerId, Date timestamp,boolean isSentByProfessional) {
        this.message = message;
        this.professionalName=professionalName;
        this.employerName=employerName;
        this.professionalId=professionalId;
        this.employerId=employerId;
        this.timestamp=timestamp;
        this.isSentByProfessional = isSentByProfessional;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfessionalName() {
        return professionalName;
    }

    public void setProfessionalName(String professionalName) {
        this.professionalName = professionalName;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public boolean isSentByProfessional() {
        return isSentByProfessional;
    }

    public void setSentByProfessional(boolean sentByProfessional) {
        isSentByProfessional = sentByProfessional;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
