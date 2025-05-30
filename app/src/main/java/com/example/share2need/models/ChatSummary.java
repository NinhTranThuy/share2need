package com.example.share2need.models;

import com.google.firebase.Timestamp;

public class ChatSummary {
    private String userID;
    private String receiverUserID;
    private String userName;
    private String lastMessage;
    private long timestamp;
    private String productId;

    public ChatSummary() {
    }

    public ChatSummary(String userID,String receiverUserID, String userName, String lastMessage, long timestamp, String productId) {
        this.userID = userID;
        this.receiverUserID = receiverUserID;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReceiverUserID() {
        return receiverUserID;
    }

    public void setReceiverUserID(String receiverUserID) {
        this.receiverUserID = receiverUserID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}