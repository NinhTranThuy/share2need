package com.example.share2need.models;

import com.google.firebase.Timestamp;

public class Order {
    private String orderId;
    private String orderState;
    private String productId;
    private String giverId;
    private String receiverId;
    private String productName;
    private long quantity;
    private long timestamp;
    private String chatId;
    public Order() {}
    public Order(String orderId,String orderState, String productId, String receiverId, String giverId,
                 String productName, long quantity, long timestamp, String chatId) {
        this.orderId = orderId;
        this.orderState = orderState;
        //Thong tin dat
        this.productId = productId;
        this.receiverId = receiverId;
        this.giverId = giverId;
        //Thong tin san pham
        this.productName = productName;
        this.quantity = quantity;
        //Thoi gian dat hang
        this.timestamp = timestamp;
        //Doan chat
        this.chatId = chatId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getGiverId() {
        return giverId;
    }

    public void setGiverId(String giverId) {
        this.giverId = giverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
