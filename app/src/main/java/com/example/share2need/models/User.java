package com.example.share2need.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class User {
    private String id;
    private String username;
    private String fullname;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String profileImage;
    private double rating;
    private int totalRatings;
    private Timestamp createdAt;
    private GeoPoint geopointUser;

    // Constructor rỗng (bắt buộc cho Firestore)
    public User() {}

    // Constructor đầy đủ
    public User(String id, String username, String fullname, String password, String email, String phone, String address,
                String profileImage, double rating, int totalRatings,
                Timestamp createdAt, GeoPoint geopointUser) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profileImage = profileImage;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.createdAt = createdAt;
        this.geopointUser = geopointUser;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public GeoPoint getGeopointUser() {
        return geopointUser;
    }

    public void setGeopointUser(GeoPoint geopointUser) {
        this.geopointUser = geopointUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}