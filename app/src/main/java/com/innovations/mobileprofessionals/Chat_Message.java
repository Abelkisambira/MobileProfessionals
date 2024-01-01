package com.innovations.mobileprofessionals;



public class Chat_Message {
    private String senderID;
    private String receiverName;
    private String message;

    public Chat_Message() {
    }

    public Chat_Message(String senderID, String receiverName, String message) {
        this.senderID = senderID;
        this.receiverName = receiverName;
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
