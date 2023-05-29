package com.example.match_app.helpers;

public class Match {
    private String sender_UID;
    private String receiver_UID;
    private String status;//pending, accepted, rejected

    public Match(){

    }
    public Match(String sender_UID, String receiver_UID, String status) {
        this.sender_UID = sender_UID;
        this.receiver_UID = receiver_UID;
        this.status = status;
    }

    public String getSender_UID() {
        return sender_UID;
    }
    public void setSender_UID(String sender_UID) {
        this.sender_UID = sender_UID;
    }
    public String getReceiver_UID() {
        return receiver_UID;
    }
    public void setReceiver_UID(String receiver_UID) {
        this.receiver_UID = receiver_UID;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
