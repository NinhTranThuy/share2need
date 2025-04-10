package com.example.share2need.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private TextView fullnameText, emailText, phoneText, addressText, createdAtText, ratingText, DanhSachYeuThich;
    private ImageView avatarImage;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Ánh xạ view
        avatarImage = findViewById(R.id.avatarImage);
        fullnameText = findViewById(R.id.fullnameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        addressText = findViewById(R.id.addressText);
        createdAtText = findViewById(R.id.createdAtText);
        ratingText = findViewById(R.id.ratingText);

        DanhSachYeuThich = findViewById(R.id.DanhSachYeuThich);
        DanhSachYeuThich.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Lấy userId từ Firebase hoặc hardcode nếu chưa login
//        String userId = auth.getCurrentUser() != null
//                ? auth.getCurrentUser().getUid()
//                : "userID"; // hoặc userA, user123 tùy bạn
        String userId = "userID";

        // Truy xuất dữ liệu người dùng từ Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(this::displayUserInfo)
                .addOnFailureListener(e -> Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show());
    }

    private void displayUserInfo(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            String fullname = documentSnapshot.getString("fullname");
            String email = documentSnapshot.getString("email");
            String phone = documentSnapshot.getString("phone");
            String address = documentSnapshot.getString("address");
            String profileImage = documentSnapshot.getString("profileImage");
            Double rating = documentSnapshot.getDouble("rating");
            Long totalRatings = documentSnapshot.getLong("totalRatings");
            Timestamp createdAt = documentSnapshot.getTimestamp("createdAt");

            fullnameText.setText("👤 " + (fullname != null ? fullname : ""));
            emailText.setText("📧 " + (email != null ? email : ""));
            phoneText.setText("📱 " + (phone != null ? phone : ""));
            addressText.setText("📍 " + (address != null ? address : ""));
            createdAtText.setText("🗓️ Tham gia: " +
                    (createdAt != null ? createdAt.toDate().toString() : "N/A"));
            ratingText.setText("⭐ " +
                    (rating != null ? rating : 0) +
                    " (" + (totalRatings != null ? totalRatings : 0) + " lượt)");

            if (profileImage != null && !profileImage.isEmpty()) {
                Glide.with(this)
                        .load(profileImage)
                        .placeholder(R.drawable.baseline_account_circle_24)
                        .into(avatarImage);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}
