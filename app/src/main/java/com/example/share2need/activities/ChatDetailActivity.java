package com.example.share2need.activities;

import static android.webkit.URLUtil.isValidUrl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.share2need.R;
import com.example.share2need.adapters.ChatDetailAdapter;
import com.example.share2need.firebase.ProductRepository;
import com.example.share2need.firebase.UserRepository;
import com.example.share2need.models.ChatSummary;
import com.example.share2need.models.Message;
import com.example.share2need.models.Product;
import com.example.share2need.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatDetailAdapter adapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageButton sendButton;
    private TextView userName, tvNameProduct, tvQuantity;
    private ImageView btnConfirm, ivProduct;
    private DatabaseReference messagesRef;
    private ConstraintLayout productConfirm;

    //Baoh co Login Firebase thi xoa
    String currentUserId = "u1";
    String productId, receiverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initView();
        setupProductConfirm();
        setupRecyclerView();
        processMessage();

//        btnConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ChatDetailActivity.this, ConfirmProduct.class);
//                intent.putExtra("product_id", productId);
//                intent.putExtra("user_id_OwnProduct", receiverUserId);
//                startActivity(intent);
//            }
//        });

        productConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatDetailActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", productId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupProductConfirm() {
        ProductRepository productRepository = new ProductRepository();
        productRepository.getProductById(productId, new ProductRepository.ProductCallback() {
            @Override
            public void onProductLoaded(Product product) {
                if (product != null){
                    try {
                        // Kiểm tra product null
                        if (product == null) {
                            Log.e("ProductError", "Product is null");
                            return;
                        }
                        // Hiển thị thông tin cơ bản với kiểm tra null
                        if (tvNameProduct != null) {
                            tvNameProduct.setText(product.getName() != null ? product.getName() : "Không có tên");
                        }

                        if (tvQuantity != null) {
                            tvQuantity.setText(String.format("Số lượng: %d", product.getQuantity()));
                        }
                        // Xử lý ảnh
                        String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                                product.getImages().get(0) : null;

                        if (ivProduct != null) {
                            if (imageUrl != null) {
                                if (isValidUrl(imageUrl)) {
                                    // 🔗 Ảnh là URL
                                    Glide.with(ivProduct)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .error(R.drawable.broken_image_24px)
                                            .into(ivProduct);
                                } else {
                                    // 🧬 Ảnh là Base64
                                    try {
                                        byte[] decodedBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                        ivProduct.setImageBitmap(bitmap);
                                    } catch (Exception e) {
                                        Log.e("Base64Error", "Lỗi decode ảnh base64", e);
                                        ivProduct.setImageResource(R.drawable.broken_image_24px);
                                    }
                                }
                            } else {
                                ivProduct.setImageResource(R.drawable.ic_launcher_background);
                            }
                        }
                    } catch (Exception e) {
                        Log.e("ProductDisplay", "Error displaying product", e);
                    }
                } else {
                    tvNameProduct.setText("CRASH ROI");
                }
            }

            @Override
            public void onProductNotFound() {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        adapter = new ChatDetailAdapter(messageList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void processMessage() {
        UserRepository newUserRepository = new UserRepository();
        newUserRepository.getUserInfo(receiverUserId, new UserRepository.UserCallback() {
                    @Override
                    public void onUserLoaded(User user) {
                        userName.setText(user.getFullname());
                    }

                    @Override
                    public void onUserNotFound() {
                        userName.setText("Không tìm thấy người dùng");
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String chatId = currentUserId.compareTo(receiverUserId) < 0
                ? currentUserId + "-" + receiverUserId
                : receiverUserId + "-" + currentUserId;

        // Lấy danh sách tin nhắn từ Firebase
        messagesRef = database.getReference("chats").child(chatId).child("messages");
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Message message = snapshot.getValue(Message.class);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiển thị thông báo lỗi
                Toast.makeText(ChatDetailActivity.this, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Gửi tin nhắn
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                Message message = new Message(currentUserId, receiverUserId,messageText,System.currentTimeMillis());

                // Ghi tin nhắn vào Firebase
                String messageId = messagesRef.push().getKey();
                messagesRef.child(messageId).setValue(message);

                // Cập nhật ChatSummary
                DatabaseReference chatSummaryRef = FirebaseDatabase.getInstance()
                        .getReference("chatSummaries");
                ChatSummary chatSummary = new ChatSummary(currentUserId,receiverUserId, "u1", messageText, System.currentTimeMillis());
                chatSummaryRef.child(currentUserId).child(chatId).setValue(chatSummary);

                ChatSummary reverseSummary = new ChatSummary(receiverUserId,currentUserId, "u2", messageText, System.currentTimeMillis());
                chatSummaryRef.child(receiverUserId).child(chatId).setValue(reverseSummary);

                messageInput.setText("");
            }
        });
    }
    private void initView() {
        //Lay du lieu tu ChatDetailActivity truyen vao
        Intent intent = getIntent();
        productId = intent.getStringExtra("product_id");
        receiverUserId = intent.getStringExtra("user_id_OwnProduct");

        ivProduct = findViewById(R.id.ivProduct);
        userName = findViewById(R.id.userName);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        tvNameProduct = findViewById(R.id.tvNameProduct);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnConfirm = findViewById(R.id.btnConfirm);
        productConfirm = findViewById(R.id.productConfirm);
    }

    public void backActivity_onClick(View view) {
        finish();
    }
}