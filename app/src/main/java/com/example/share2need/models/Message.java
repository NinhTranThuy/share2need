package com.example.share2need.models;
public class Message {
    private String text;
    private boolean isSentByMe;
    private String senderId;
    private long timestamp;

    // Constructor mặc định (yêu cầu bởi Firebase)
    public Message() {
    }

    public Message(String text, boolean isSentByMe, String senderId, long timestamp) {
        this.text = text;
        this.isSentByMe = isSentByMe;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    // Getters và Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSentByMe() {
        return isSentByMe;
    }

    public void setSentByMe(boolean sentByMe) {
        isSentByMe = sentByMe;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
