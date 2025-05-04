package com.example.share2need.models;

public class Order {
    private String bookingId;
    private String productId;
    private String giverId;
    private String receiverId;
    private String productName;
    private int quantity;
    private double price;
    private String status;
    private long timestamp;
    private String chatId;
    public Order() {}
    public Order(String bookingId, String productId, String giverId, String receiverId,
                 String productName, int quantity, double price, String status,
                 long timestamp, String note, String chatId) {
        this.bookingId = bookingId;
        this.productId = productId;
        this.giverId = giverId;
        this.receiverId = receiverId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.timestamp = timestamp;
        this.chatId = chatId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
