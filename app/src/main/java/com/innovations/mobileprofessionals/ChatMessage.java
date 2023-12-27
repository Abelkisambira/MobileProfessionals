package com.innovations.mobileprofessionals;

public class ChatMessage {
    private String message;
    private boolean sentByProfessional;

    public ChatMessage() {
        // Default constructor required for Firebase
    }

    public ChatMessage(String message, boolean sentByProfessional) {
        this.message = message;
        this.sentByProfessional = sentByProfessional;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByProfessional() {
        return sentByProfessional;
    }
}
