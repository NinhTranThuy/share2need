package com.example.share2need.activities;

import static android.webkit.URLUtil.isValidUrl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.adapters.ProductAdapter;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.models.Product;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private TextView fullnameText, emailText, phoneText, addressText, createdAtText, ratingText, DanhSachYeuThich, SuaThongTin;
    private ImageView avatarImage;
    private RecyclerView recyclerViewProductsList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    String userId = "u1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initView();

        DanhSachYeuThich = findViewById(R.id.DanhSachYeuThich);
        DanhSachYeuThich.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });

        SuaThongTin = findViewById(R.id.SuaThongTin);
        SuaThongTin.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();



        // Truy xuất dữ liệu người dùng từ Firestore
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(this::displayUserInfo)
                .addOnFailureListener(e -> Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show());
    }

    private void initView() {
        avatarImage = findViewById(R.id.avatarImage);
        fullnameText = findViewById(R.id.fullnameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        addressText = findViewById(R.id.addressText);
        createdAtText = findViewById(R.id.createdAtText);
        ratingText = findViewById(R.id.ratingText);
        recyclerViewProductsList = findViewById(R.id.recyclerViewProductsList);
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


            fullnameText.setText(fullname != null ? fullname : "");
            emailText.setText(email != null ? email : "");
            phoneText.setText(phone != null ? phone : "");
            addressText.setText(address != null ? address : "");
            createdAtText.setText("Ngày tạo: " + createdAt != null ? createdAt.toDate().toString() : "N/A");
            ratingText.setText((rating != null ? rating : 0) +
                    " (" + (totalRatings != null ? totalRatings : 0) + " lượt)");

            displayImage(profileImage);
            //Hien danh sach san pham
            ProductRepository productRepository = new ProductRepository();
            productRepository.getAllProductByUserID(userId).observe(this,products -> {
                if(products != null){
                    Log.e("MainActivityy", "Received" + products.size());
                       ProductAdapter adapter = new ProductAdapter(this, products);
                       recyclerViewProductsList.setLayoutManager(new GridLayoutManager(this, 2));
                       recyclerViewProductsList.setAdapter(adapter);
                }else{
                    Log.e("MainActivityy", "Received null products");
                }
            });
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    public void backActivity_onClick(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void displayImage(String profileImage){
        if (avatarImage != null) {
            if (profileImage != null) {
                if (isValidUrl(profileImage)) {
                    Log.d("CheckImageee", "profileImage: " );

                    // 🔗 Ảnh là URL
                    Glide.with(avatarImage.getContext())
                            .load(profileImage)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.broken_image_24px)
                            .into(avatarImage);
                } else {
                    // 🧬 Ảnh là Base64
                    try {
                        Log.d("CheckImageee", "profileImage: " + profileImage);
                        byte[] decodedBytes = Base64.decode(profileImage, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        avatarImage.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Log.e("Base64Error", "Lỗi decode ảnh base64", e);
                        avatarImage.setImageResource(R.drawable.broken_image_24px);
                    }
                }
            } else {
                avatarImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
    private boolean isValidUrl(String string) {
        return string.startsWith("http://") || string.startsWith("https://");
    }
}
