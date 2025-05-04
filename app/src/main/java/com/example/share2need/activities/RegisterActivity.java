package com.example.share2need.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.share2need.R;
import com.example.share2need.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText editUsername, editFullname, editPassword, editPhone;
    private Button btnRegister;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regigter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initView();
    }

    private void initView() {
        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Ánh xạ các view
        editUsername = findViewById(R.id.editUsername);
        editFullname = findViewById(R.id.editFullname);
        editPassword = findViewById(R.id.editPassword);
        editPhone = findViewById(R.id.editPhone);
        btnRegister = findViewById(R.id.btnRegister);

        if (editUsername == null || editFullname == null || editPassword == null ||
                editPhone == null || btnRegister == null) {
            Toast.makeText(this, "Lỗi giao diện, vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    public void login_fromRegister_onClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void register_fromRegister_onClick(View view) {
        String username = editUsername.getText().toString().trim();
        String fullname = editFullname.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (username.isEmpty() || fullname.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra kết nối mạng
        if (!isNetworkAvailable()) {
            Toast.makeText(RegisterActivity.this, "Không có kết nối mạng!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra username đã tồn tại chưa
        checkUsernameExists(username, exists -> {
            if (exists) {
                Toast.makeText(RegisterActivity.this, "Username đã tồn tại!",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Tìm số lớn nhất hiện tại và tạo userId mới
                generateUserId(userId -> {
                    if (userId == null) {
                        Toast.makeText(RegisterActivity.this, "Lỗi tạo userId!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Tạo user mới
//                    User user = new User(userId, username, fullname, password, phone,
//                            "123456789012", "123 Đường ABC, Quận 1, TP.HCM");

                    // Lưu vào Firebase
//                    databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!",
//                                    Toast.LENGTH_SHORT).show();
//                            finish();
//                        } else {
//                            Toast.makeText(RegisterActivity.this, "Lỗi lưu dữ liệu: " +
//                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
                });
            }
        });
    }
    private void checkUsernameExists(String username, final FirebaseCallback callback) {
        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onCallback(snapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RegisterActivity.this, "Lỗi kiểm tra username: " +
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                        callback.onCallback(false); // Để tiếp tục luồng xử lý
                    }
                });
    }

    private void generateUserId(final UserIdCallback callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int maxId = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        String userId = child.getKey();
                        if (userId != null && userId.startsWith("user")) {
                            try {
                                int idNumber = Integer.parseInt(userId.replace("user", ""));
                                if (idNumber > maxId) {
                                    maxId = idNumber;
                                }
                            } catch (NumberFormatException e) {
                                // Bỏ qua nếu userId không đúng định dạng
                            }
                        }
                    }
                }
                // Tạo userId mới bằng số lớn nhất + 1
                String newUserId = "user" + (maxId + 1);
                callback.onCallback(newUserId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Lỗi truy vấn userId: " +
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onCallback(null); // Trả về null nếu lỗi
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // Interface callback cho kiểm tra username
    private interface FirebaseCallback {
        void onCallback(boolean exists);
    }

    // Interface callback cho tạo userId
    private interface UserIdCallback {
        void onCallback(String userId);
    }
}