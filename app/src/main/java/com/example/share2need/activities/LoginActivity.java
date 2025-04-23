package com.example.share2need.activities;

import android.content.Intent;
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

import com.example.share2need.MainActivity;
import com.example.share2need.R;
import com.example.share2need.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private EditText editUsername, editPassword;
    private DatabaseReference databaseReference;
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";
    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9_!@#$%^&*]{6,20}$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initView();
    }

    private void initView() {
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);

    }

    public void login_onClick(View view) {
        String username = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Kiểm tra đầu vào
        if (!isValidInput(username, password)) {
            Toast.makeText(LoginActivity.this,
                    "Username và mật khẩu chỉ được chứa chữ cái, số, và một số ký tự đặc biệt (_,!@#$%^&*).",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        checkLogin(username, password);
    }

    private boolean isValidInput(String username, String password) {
        // Kiểm tra username
        if (!Pattern.matches(USERNAME_PATTERN, username)) {
            return false;
        }

        // Kiểm tra password
        if (!Pattern.matches(PASSWORD_PATTERN, password)) {
            return false;
        }

        return true;
    }

    private void checkLogin(String username, String password) {
        databaseReference.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null && user.getPassword().equals(password)) {
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!",
                                            Toast.LENGTH_SHORT).show();

                                    // Chuyển sang HomeActivity và truyền thông tin người dùng
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                    finish(); // Đóng LoginActivity
                                    return;
                                }
                            }
                            Toast.makeText(LoginActivity.this, "Mật khẩu không đúng!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Username không tồn tại!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void regigter_onClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}