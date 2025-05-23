package com.example.share2need.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Product {
    private String userId;
    private String name;
    private String lowercase_name;
    private String description;
    private String category; // "thực phẩm", "vệ sinh", "y tế", "khác"
    private List<String> images;
    private double price;
    private int quantity;
    private String status; // "còn hàng", "đã đặt", "đã bán"
    private String address;
    private GeoPoint location;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product(String id, String userId, String name, String lowercase_name, String description, String category,
                   List<String> images, int quantity, String status, String address,
                   GeoPoint location, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.lowercase_name = lowercase_name;
        this.description = description;
        this.category = category;
        this.images = images;
        this.quantity = quantity;
        this.status = status;
        this.address = address;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Product() {
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLowercase_name() {
        return lowercase_name;
    }

    public void setLowercase_name(String lowercase_name) {
        this.lowercase_name = lowercase_name;
    }
}
