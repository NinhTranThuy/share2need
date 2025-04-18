package com.example.share2need.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.share2need.R;
import com.example.share2need.adapters.MessageAdapter;
import com.example.share2need.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageButton sendButton;
    private TextView userName;
    private DatabaseReference messagesRef;

    //Baoh co Login Firebase thi xoa
    private String currentUserId = "user1";
    private String chatId;
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

        processMessage();
    }
    private void processMessage() {
        String user = getIntent().getStringExtra("userName");
        // Loại bỏ ký tự không hợp lệ
        String sanitizedUser = user.toLowerCase().replaceAll("[.#$\\[\\]]", "");
        chatId = sanitizedUser + "_" + currentUserId;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
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

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                long timestamp = System.currentTimeMillis();
                Message message = new Message(messageText, true, currentUserId, timestamp);

                // Ghi tin nhắn vào Firebase
                String messageId = messagesRef.push().getKey();
                messagesRef.child(messageId).setValue(message);

                // Cập nhật chatSummaries
                DatabaseReference chatSummaryRef = FirebaseDatabase.getInstance().getReference("chatSummaries").child(chatId);
                chatSummaryRef.child("userName").setValue(user);
                chatSummaryRef.child("lastMessage").setValue(messageText);
                chatSummaryRef.child("timestamp").setValue(new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date(timestamp)));

                messageInput.setText("");
            }
        });
    }
    private void initView() {
        userName = findViewById(R.id.userName);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
    }

    public void backActivity_onClick(View view) {
        finish();
    }
}