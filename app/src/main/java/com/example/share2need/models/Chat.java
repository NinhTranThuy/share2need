package com.example.share2need.models;

public class Chat {
    private String userName;
    private String lastMessage;
    private String timestamp;

    public Chat() {
    }

    public Chat(String userName, String lastMessage, String timestamp) {
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}